package com.example.settingsapp

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Bundle
import android.os.UserManager
import android.provider.Settings
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ControlCenterActivity : AppCompatActivity() {

    private lateinit var cameraManager: CameraManager
    private var flashCameraId: String? = null
    private var isTorchOn = false

    private lateinit var flashlightToggle: FrameLayout
    private lateinit var flashlightStatus: TextView
    private lateinit var brightnessSeekBar: SeekBar
    private lateinit var brightnessValue: TextView
    private lateinit var brightnessPermissionHint: TextView

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponent: ComponentName
    private lateinit var vpnBlockToggle: FrameLayout
    private lateinit var vpnBlockStatus: TextView
    private lateinit var vpnBlockHint: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_center)

        findViewById<android.widget.ImageButton>(R.id.backButton).setOnClickListener { finish() }

        flashlightToggle = findViewById(R.id.flashlightToggle)
        flashlightStatus = findViewById(R.id.flashlightStatus)
        brightnessSeekBar = findViewById(R.id.brightnessSeekBar)
        brightnessValue = findViewById(R.id.brightnessValue)
        brightnessPermissionHint = findViewById(R.id.brightnessPermissionHint)
        vpnBlockToggle = findViewById(R.id.vpnBlockToggle)
        vpnBlockStatus = findViewById(R.id.vpnBlockStatus)
        vpnBlockHint = findViewById(R.id.vpnBlockHint)

        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, VpnAdminReceiver::class.java)

        setupFlashlight()
        setupBrightness()
        setupVpnBlock()
    }

    override fun onResume() {
        super.onResume()
        refreshBrightnessPermissionState()
        refreshVpnBlockState()
    }

    private fun setupFlashlight() {
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            flashlightStatus.text = getString(R.string.flashlight_unavailable)
            flashlightToggle.isEnabled = false
            return
        }

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        flashCameraId = cameraManager.cameraIdList.firstOrNull { id ->
            val characteristics = cameraManager.getCameraCharacteristics(id)
            characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true &&
                characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
        }

        flashlightToggle.setOnClickListener { toggleFlashlight() }
    }

    private fun toggleFlashlight() {
        val cameraId = flashCameraId ?: return
        try {
            isTorchOn = !isTorchOn
            cameraManager.setTorchMode(cameraId, isTorchOn)
            updateFlashlightUi()
        } catch (e: CameraAccessException) {
            isTorchOn = false
            Toast.makeText(this, R.string.flashlight_unavailable, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFlashlightUi() {
        flashlightToggle.setBackgroundResource(if (isTorchOn) R.drawable.bg_toggle_on else R.drawable.bg_toggle_off)
        flashlightStatus.setText(if (isTorchOn) R.string.toggle_on else R.string.toggle_off)
    }

    private fun setupBrightness() {
        val currentBrightness = try {
            Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: Settings.SettingNotFoundException) {
            125
        }
        brightnessSeekBar.progress = (currentBrightness * 100) / 255
        brightnessValue.text = "${brightnessSeekBar.progress}%"

        brightnessPermissionHint.setOnClickListener {
            val intent = Intent(
                Settings.ACTION_MANAGE_WRITE_SETTINGS,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }

        brightnessSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                brightnessValue.text = "$progress%"
                if (fromUser && Settings.System.canWrite(this@ControlCenterActivity)) {
                    Settings.System.putInt(
                        contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
                    )
                    Settings.System.putInt(
                        contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS,
                        (progress * 255) / 100
                    )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun refreshBrightnessPermissionState() {
        val canWrite = Settings.System.canWrite(this)
        brightnessPermissionHint.visibility = if (canWrite) android.view.View.GONE else android.view.View.VISIBLE
        brightnessSeekBar.isEnabled = canWrite
    }

    private fun setupVpnBlock() {
        vpnBlockToggle.setOnClickListener {
            if (!devicePolicyManager.isDeviceOwnerApp(packageName)) {
                Toast.makeText(this, R.string.vpn_not_device_owner, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val isBlocked = devicePolicyManager.getUserRestrictions(adminComponent)
                .getBoolean(UserManager.DISALLOW_CONFIG_VPN, false)
            if (isBlocked) {
                devicePolicyManager.clearUserRestriction(adminComponent, UserManager.DISALLOW_CONFIG_VPN)
            } else {
                devicePolicyManager.addUserRestriction(adminComponent, UserManager.DISALLOW_CONFIG_VPN)
            }
            refreshVpnBlockState()
        }
    }

    private fun refreshVpnBlockState() {
        val isDeviceOwner = devicePolicyManager.isDeviceOwnerApp(packageName)
        vpnBlockHint.visibility = if (isDeviceOwner) android.view.View.GONE else android.view.View.VISIBLE

        val isBlocked = isDeviceOwner &&
            devicePolicyManager.getUserRestrictions(adminComponent).getBoolean(UserManager.DISALLOW_CONFIG_VPN, false)

        vpnBlockToggle.setBackgroundResource(if (isBlocked) R.drawable.bg_toggle_on else R.drawable.bg_toggle_off)
        vpnBlockStatus.setText(if (isBlocked) R.string.vpn_blocked else R.string.vpn_allowed)
    }
}
