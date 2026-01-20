package com.nuecoo.controller

import android.content.res.Resources
import android.graphics.PointF
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.hypot

class CookiePinchOpenController(
    private val targetView: View, private val onOpen: () -> Unit
) {
    private val activePointers = linkedMapOf<Int, PointF>()

    private var isAnimating = false
    private var initialDistance = 0f

    private val validRangeX = dp(150f)
    private val minSpreadIncrease = dp(80f)

    init {
        targetView.setOnTouchListener { _, event ->
            if (isAnimating) return@setOnTouchListener false

            val handled = handleTouch(event)
            handled
        }
    }

    fun setAnimating(value: Boolean) {
        isAnimating = value
    }

    private fun handleTouch(event: MotionEvent): Boolean {
        return when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN -> {
                onPointerDown(event)
                activePointers.isNotEmpty()
            }

            MotionEvent.ACTION_MOVE -> {
                val shouldHandle = activePointers.size == 2 && !isAnimating
                if (shouldHandle) onPointerMove(event)
                shouldHandle
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_POINTER_UP,
            MotionEvent.ACTION_CANCEL -> {
                val hadPointers = activePointers.isNotEmpty()
                onPointerUp(event)
                hadPointers
            }

            else -> false
        }
    }

    private fun onPointerDown(event: MotionEvent) {
        val actionIndex = event.actionIndex
        val pointerId = event.getPointerId(actionIndex)

        val rawPos = getRawPosition(event, actionIndex) ?: return
        val rawX = rawPos.x
        val rawY = rawPos.y

        val centerX = targetViewCenterXOnScreen()

        if (abs(rawX - centerX) <= validRangeX) {
            activePointers[pointerId] = PointF(rawX, rawY)

            if (activePointers.size == 2) {
                val pts = activePointers.values.toList()
                initialDistance = distance(pts[0], pts[1])
            }
        }
    }

    private fun onPointerMove(event: MotionEvent) {
        if (activePointers.size == 2 && !isAnimating) {

            for (i in 0 until event.pointerCount) {
                val id = event.getPointerId(i)
                if (activePointers.containsKey(id)) {
                    val rawPos = getRawPosition(event, i) ?: continue
                    activePointers[id] = PointF(rawPos.x, rawPos.y)
                }
            }

            val pts = activePointers.values.toList()
            val currentDistance = distance(pts[0], pts[1])

            if (currentDistance - initialDistance > minSpreadIncrease) {
                isAnimating = true
                onOpen.invoke()
                activePointers.clear()
            }
        }
    }

    private fun onPointerUp(event: MotionEvent) {
        val actionIndex = event.actionIndex
        val pointerId = event.getPointerId(actionIndex)

        activePointers.remove(pointerId)

        if (activePointers.size < 2) {
            initialDistance = 0f
        }
    }

    private fun targetViewCenterXOnScreen(): Float {
        val loc = IntArray(2)
        targetView.getLocationOnScreen(loc)
        val leftX = loc[0].toFloat()
        return leftX + (targetView.width / 2f)
    }

    private fun distance(a: PointF, b: PointF): Float {
        return hypot(a.x - b.x, a.y - b.y)
    }

    private fun dp(value: Float): Float {
        return value * Resources.getSystem().displayMetrics.density
    }

    private fun getRawPosition(event: MotionEvent, index: Int): PointF? {
        if (index < 0 || index >= event.pointerCount) return null

        val loc = IntArray(2)
        targetView.getLocationOnScreen(loc)
        val viewXOnScreen = loc[0].toFloat()
        val viewYOnScreen = loc[1].toFloat()

        val localX = event.getX(index)
        val localY = event.getY(index)

        return PointF(viewXOnScreen + localX, viewYOnScreen + localY)
    }

    fun dispose() {
        targetView.setOnTouchListener(null)
        activePointers.clear()
        initialDistance = 0f
    }
}