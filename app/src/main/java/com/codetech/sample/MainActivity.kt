package com.codetech.sample

import android.content.IntentSender
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.codetech.lib.InAppUpdateService
import com.codetech.lib.UpdateCallBack
import com.codetech.lib.UpdateListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE

class MainActivity : AppCompatActivity() {
    private lateinit var inAppUpdateService: InAppUpdateService

    private val updateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        // Handle the result here if needed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inAppUpdateService = InAppUpdateService.Builder()
            .setContext(this)
            .setInAppUpdateLauncher(updateLauncher)
            .build()

        // Check for updates
        inAppUpdateService.checkForUpdate(object : UpdateListener {
            override fun onUpdateAvailable(version: Int, availableUpdateTypes: List<Int>) {
                // Notify user about available update
                // Optionally show a dialog to start the update

            }

            override fun onNoUpdateAvailable() {
                // Notify user that no updates are available
            }

            override fun onFailedToObserve(exception: Exception) {
                // Handle failure
            }
        })

        // For flexible updates
        inAppUpdateService.startFlexibleUpdate(object : UpdateCallBack {
            override fun availableVersion(version: Int) {
                // Handle the available version info
            }

            override fun onNoUpdateAvailable() {
                // Handle the case where no update is available
            }

            override fun onUpdateStarted() {
                // Notify user that the update has started
            }

            override fun onUpdateProgress(bytesDownloaded: Long, totalBytesToDownload: Long) {
                // Update progress UI
            }

            override fun onUpdateDownloaded() {
                // Notify user that the update has been downloaded
            }

            override fun onUpdateInstalled() {
                // Notify user that the update has been installed
            }

            override fun onUpdateFailed(errorCode: Int) {
                // Handle update failure
            }

            override fun onFailure(exception: Exception) {
                // Handle general failure
            }
        })

// For immediate updates
        inAppUpdateService.startImmediateUpdate(object : UpdateCallBack {
            override fun availableVersion(version: Int) {
                // Handle the available version info
            }

            override fun onNoUpdateAvailable() {
                // Handle the case where no update is available
            }

            override fun onUpdateStarted() {
                // Notify user that the update has started
            }

            override fun onUpdateProgress(bytesDownloaded: Long, totalBytesToDownload: Long) {
                // Update progress UI
            }

            override fun onUpdateDownloaded() {
                // Notify user that the update has been downloaded
            }

            override fun onUpdateInstalled() {
                // Notify user that the update has been installed
            }

            override fun onUpdateFailed(errorCode: Int) {
                // Handle update failure
            }

            override fun onFailure(exception: Exception) {
                // Handle general failure
            }
        })

        inAppUpdateService.isFlexibleUpdateAvailable { isAvailable ->
            if (isAvailable) {
                // Flexible update is available
            } else {
                // No flexible update available
            }
        }

        inAppUpdateService.isImmediateUpdateAvailable { isAvailable ->
            if (isAvailable) {
                // Immediate update is available
            } else {
                // No immediate update available
            }
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        inAppUpdateService.unregisterListener()
    }

}
