package com.nuecoo

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.nuecoo.core.navigation.AppNavigation
import com.nuecoo.core.ui.BaseActivity
import com.nuecoo.core.ui.showSimpleDialog
import com.nuecoo.core.util.hasNotificationPermission
import com.nuecoo.core.theme.NueCooTheme
import com.nuecoo.feature.widget.domain.usecase.GetWidgetEnabledUseCase
import com.nuecoo.feature.widget.presentation.FloatingWidgetService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @Inject
    lateinit var getWidgetEnabledUseCase: GetWidgetEnabledUseCase

    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>

    private val allPermissions: Array<String> by lazy {
        listOfNotNull(hasNotificationPermission()).toTypedArray()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            val allGranted = results.values.all { it }
            if (!allGranted) {
                val deniedPermissions = results.filterValues { !it }.keys
                val permanentlyDenied = deniedPermissions.any { permission ->
                    ContextCompat.checkSelfPermission(
                        this,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                            && !shouldShowRequestPermissionRationale(permission)
                }
                if (permanentlyDenied) {
                    showSimpleDialog(
                        "권한 필요",
                        "권한이 영구적으로 거부되었습니다. 설정에서 권한을 허용해 주세요.",
                        "설정으로 이동",
                        "취소",
                    ) {
                        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", packageName, null)
                        })
                    }
                } else {
                    showSimpleDialog(
                        "권한 필요",
                        "이 기능을 사용하려면 권한이 필요합니다.",
                        "다시 시도",
                        "취소",
                    ) {
                        requestPermissionsLauncher.launch(allPermissions)
                    }
                }
            }
        }

        checkPermissions()
        startFloatingWidgetIfEnabled()

        setContent {
            NueCooTheme {
                AppNavigation()
            }
        }
    }

    private fun startFloatingWidgetIfEnabled() {
        lifecycleScope.launch {
            val enabled = getWidgetEnabledUseCase().first()
            if (enabled && Settings.canDrawOverlays(this@MainActivity)) {
                FloatingWidgetService.start(this@MainActivity)
            }
        }
    }

    private fun checkPermissions() {
        if (allPermissions.isNotEmpty() && !allPermissionsGranted()) {
            requestPermissionsLauncher.launch(allPermissions)
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return allPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}