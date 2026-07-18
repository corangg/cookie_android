package com.nuecoo.feature.widget.presentation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import com.nuecoo.R
import com.nuecoo.feature.widget.domain.usecase.SaveWidgetEnabledUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class FloatingWidgetService : Service() {

    @Inject
    lateinit var saveWidgetEnabledUseCase: SaveWidgetEnabledUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var windowManager: WindowManager? = null
    private var floatingView: ImageView? = null
    private var floatingViewParams: WindowManager.LayoutParams? = null
    private var closeZoneView: TextView? = null
    private var resultPopupView: View? = null
    private var resultPopupParams: WindowManager.LayoutParams? = null

    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, createNotification())
        addFloatingView()
    }

    override fun onDestroy() {
        removeFloatingView()
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun addFloatingView() {
        if (floatingView != null) return

        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager = wm

        val closeZone = createCloseZoneView()
        wm.addView(closeZone, closeZoneLayoutParams())
        closeZoneView = closeZone

        val density = resources.displayMetrics.density
        val widgetSizePx = (48 * density).toInt()

        val bubbleParams = WindowManager.LayoutParams(
            widgetSizePx,
            widgetSizePx,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 300
        }


        val paddingPx = (8 * density).toInt()
        val touchSlop = ViewConfiguration.get(this).scaledTouchSlop
        val closeZoneThresholdY = resources.displayMetrics.heightPixels -
            (150 * density).toInt()

        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels
        val popupWidth = (RESULT_POPUP_WIDTH_DP * density).toInt()
        val popupGap = (RESULT_POPUP_GAP_DP * density).toInt()
        val popupMargin = (RESULT_POPUP_MARGIN_DP * density).toInt()
        val popupReservedWidth = popupGap + popupWidth + popupMargin

        val view = ImageView(this).apply {
            setImageResource(R.drawable.img_cookie)
            scaleType = ImageView.ScaleType.FIT_CENTER
            setBackgroundResource(R.drawable.bg_floating_widget)
            setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
            setOnClickListener {
                if (resultPopupView == null) {
                    val safeAnchorX = (screenWidth - it.width - popupReservedWidth).coerceAtLeast(0)
                    if (bubbleParams.x > safeAnchorX) {
                        bubbleParams.x = safeAnchorX
                        windowManager?.updateViewLayout(it, bubbleParams)
                    }
                }
                toggleResultPopup(
                    anchorX = bubbleParams.x,
                    anchorY = bubbleParams.y,
                    anchorWidth = it.width,
                    anchorHeight = it.height
                )
            }
        }

        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var isDragging = false
        var isOverCloseZone = false

        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = bubbleParams.x
                    initialY = bubbleParams.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isDragging = false
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - initialTouchX
                    val dy = event.rawY - initialTouchY
                    if (!isDragging && (abs(dx) > touchSlop || abs(dy) > touchSlop)) {
                        isDragging = true
                        closeZoneView?.visibility = View.VISIBLE
                    }
                    if (isDragging) {
                        val maxBubbleX = if (resultPopupView != null) {
                            (screenWidth - v.width - popupReservedWidth).coerceAtLeast(0)
                        } else {
                            (screenWidth - v.width).coerceAtLeast(0)
                        }
                        val maxBubbleY = (screenHeight - v.height).coerceAtLeast(0)
                        bubbleParams.x = (initialX + dx.toInt()).coerceIn(0, maxBubbleX)
                        bubbleParams.y = (initialY + dy.toInt()).coerceIn(0, maxBubbleY)
                        windowManager?.updateViewLayout(v, bubbleParams)
                        updateResultPopupPosition(bubbleParams.x, bubbleParams.y, v.width, v.height)

                        val overCloseZone = event.rawY > closeZoneThresholdY
                        if (overCloseZone != isOverCloseZone) {
                            isOverCloseZone = overCloseZone
                            closeZoneView?.setBackgroundResource(
                                if (overCloseZone) R.drawable.bg_floating_close_zone_active
                                else R.drawable.bg_floating_close_zone
                            )
                        }
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    if (!isDragging) {
                        v.performClick()
                    } else if (isOverCloseZone) {
                        closeWidget()
                    } else {
                        closeZoneView?.visibility = View.GONE
                    }
                    true
                }

                else -> false
            }
        }

        wm.addView(view, bubbleParams)
        floatingView = view
        floatingViewParams = bubbleParams
    }

    private fun createCloseZoneView(): TextView {
        val paddingH = (24 * resources.displayMetrics.density).toInt()
        val paddingV = (14 * resources.displayMetrics.density).toInt()
        return TextView(this).apply {
            text = getString(R.string.text_floating_widget_close_hint)
            setTextColor(Color.WHITE)
            textSize = 13f
            setBackgroundResource(R.drawable.bg_floating_close_zone)
            setPadding(paddingH, paddingV, paddingH, paddingV)
            visibility = View.GONE
        }
    }

    private fun closeZoneLayoutParams() = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        y = (80 * resources.displayMetrics.density).toInt()
    }

    private fun closeWidget() {
        serviceScope.launch { saveWidgetEnabledUseCase(false) }
        stopSelf()
    }

    private fun toggleResultPopup(anchorX: Int, anchorY: Int, anchorWidth: Int, anchorHeight: Int) {
        if (resultPopupView != null) {
            removeResultPopup()
            return
        }

        val wm = windowManager ?: return
        val density = resources.displayMetrics.density
        val popupWidth = (RESULT_POPUP_WIDTH_DP * density).toInt()
        val popupHeight = (RESULT_POPUP_HEIGHT_DP * density).toInt()

        val params = WindowManager.LayoutParams(
            popupWidth,
            popupHeight,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.OPAQUE
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }
        applyClampedPopupPosition(params, anchorX, anchorY, anchorWidth, anchorHeight)

        val popup = View(this).apply {
            setBackgroundColor(Color.WHITE)
            setOnClickListener { removeResultPopup() }
        }

        wm.addView(popup, params)
        resultPopupView = popup
        resultPopupParams = params
        bringFloatingViewToFront()
    }

    /**
     * Overlay windows stack in add order, so the popup (added after the widget)
     * would otherwise cover it. Re-adding the widget puts it back on top whenever
     * the two overlap.
     */
    private fun bringFloatingViewToFront() {
        val wm = windowManager ?: return
        val view = floatingView ?: return
        val params = floatingViewParams ?: return
        wm.removeView(view)
        wm.addView(view, params)
    }

    private fun updateResultPopupPosition(anchorX: Int, anchorY: Int, anchorWidth: Int, anchorHeight: Int) {
        val view = resultPopupView ?: return
        val params = resultPopupParams ?: return
        applyClampedPopupPosition(params, anchorX, anchorY, anchorWidth, anchorHeight)
        windowManager?.updateViewLayout(view, params)
    }

    /**
     * Popup is anchored at a fixed gap to the right of the widget so it never covers it.
     * The widget itself is repositioned before opening (see the click listener) so this
     * placement always fits on screen.
     */
    private fun applyClampedPopupPosition(
        params: WindowManager.LayoutParams,
        anchorX: Int,
        anchorY: Int,
        anchorWidth: Int,
        anchorHeight: Int
    ) {
        val density = resources.displayMetrics.density
        val margin = (RESULT_POPUP_MARGIN_DP * density).toInt()
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels
        val maxX = (screenWidth - params.width - margin).coerceAtLeast(margin)
        val maxY = (screenHeight - params.height - margin).coerceAtLeast(margin)

        val desiredX = anchorX + anchorWidth/2
        val desiredY = anchorY + anchorHeight/2

        params.x = desiredX.coerceIn(margin, maxX)
        params.y = desiredY.coerceIn(margin, maxY)
    }

    private fun removeResultPopup() {
        resultPopupView?.let { windowManager?.removeView(it) }
        resultPopupView = null
        resultPopupParams = null
    }

    private fun removeFloatingView() {
        floatingView?.let { windowManager?.removeView(it) }
        floatingView = null
        closeZoneView?.let { windowManager?.removeView(it) }
        closeZoneView = null
        removeResultPopup()
    }

    private fun createNotification(): Notification {
        val manager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.text_floating_widget_channel_name),
            NotificationManager.IMPORTANCE_MIN
        )
        manager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_widget)
            .setContentTitle(getString(R.string.text_floating_widget_notification_title))
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "floating_widget_channel"
        private const val NOTIFICATION_ID = 4201
        private const val RESULT_POPUP_WIDTH_DP = 200
        private const val RESULT_POPUP_HEIGHT_DP = 80
        private const val RESULT_POPUP_MARGIN_DP = 8
        private const val RESULT_POPUP_GAP_DP = 8

        fun start(context: Context) {
            context.startForegroundService(Intent(context, FloatingWidgetService::class.java))
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, FloatingWidgetService::class.java))
        }
    }
}
