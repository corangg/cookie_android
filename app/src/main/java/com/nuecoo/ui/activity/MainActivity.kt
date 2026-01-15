package com.nuecoo.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.nuecoo.R
import com.nuecoo.core.ui.BaseActivity
import com.nuecoo.core.ui.showSimpleDialog
import com.nuecoo.core.util.hasNotificationPermission
import com.nuecoo.databinding.ActivityMainBinding
import com.nuecoo.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>

    private val allPermissions = arrayOf(
        hasNotificationPermission()
    ).filterNotNull().toTypedArray()

    override fun setUpUi() {
        binding.viewModel = viewModel
        bindingOnClick()
        bindingNavigation()
    }

    override fun setUpData() {
        initRequestLauncher()
        checkPerMissions()
    }

    override fun setUpObserver(lifecycleOwner: LifecycleOwner) {
    }

    private fun bindingOnClick() {

    }

    private fun bindingNavigation() {
        val navController = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.findNavController() ?: return
        binding.bottomNavigation.setupWithNavController(navController)
        binding.bottomNavigation.itemIconTintList = null
        applyBottomNaviScale(binding.bottomNavigation.selectedItemId)
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            applyBottomNaviScale(item.itemId)

            when (item.itemId) {
                R.id.navi_oven, R.id.navi_collection, R.id.navi_menu -> {
                    NavigationUI.onNavDestinationSelected(item, navController)
                    true
                }

                else -> false
            }
        }
    }

    private fun applyBottomNaviScale(selectedItemId: Int) {
        val menuView = binding.bottomNavigation.getChildAt(0) as ViewGroup

        for (i in 0 until menuView.childCount) {
            val itemView = menuView.getChildAt(i)
            val menuItem = binding.bottomNavigation.menu.getItem(i)

            val isSelected = menuItem.itemId == selectedItemId

            itemView.animate()
                .scaleX(if (isSelected) 1.25f else 1.0f)
                .scaleY(if (isSelected) 1.25f else 1.0f)
                .setDuration(0)
                .start()
        }
    }

    private fun initRequestLauncher() {
        requestPermissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
                val allGranted = results.values.all { it }
                if (!allGranted) {
                    val deniedPermissions = results.filterValues { !it }.keys
                    val permanentlyDenied = deniedPermissions.any { permission ->
                        ContextCompat.checkSelfPermission(
                            this,
                            permission
                        ) != PackageManager.PERMISSION_GRANTED && !shouldShowRequestPermissionRationale(
                            permission
                        )
                    }
                    if (permanentlyDenied) {
                        this@MainActivity.showSimpleDialog(
                            getString(R.string.text_permission_title),
                            getString(R.string.text_message_permission_permanent_refusal),
                            getString(R.string.text_move_setting),
                            "취소",
                        ) {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", packageName, null)
                                }
                            startActivity(intent)
                        }
                    } else {
                        this@MainActivity.showSimpleDialog(
                            getString(R.string.text_permission_title),
                            getString(R.string.text_message_permission_refusal),
                            getString(R.string.text_retry),
                            "취소",
                        ) {
                            requestPermissionsLauncher.launch(allPermissions)
                        }
                    }
                }
            }
    }

    private fun checkPerMissions() {
        if (allPermissionsGranted()) {
            //상관 없을듯?
        } else {
            requestPermissionsLauncher.launch(allPermissions)
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return allPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}