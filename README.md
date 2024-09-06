# InAppUpdateService

`InAppUpdateService` is an Android library for managing in-app updates using the Google Play Core library. It simplifies the process of checking for updates, handling flexible and immediate updates, and tracking the update progress.

## Features

- Check for available updates (flexible and immediate)
- Start flexible and immediate updates
- Track update progress and installation status
- Register and unregister for install state updates

## Integration

### 1. Add Dependencies

Add the following dependency to your `build.gradle` file

maven { url 'https://jitpack.io' }

	dependencies {
 
	        implementation 'com.github.muhammad-ahmed-lib:easy-app-update:beta-1.0'
				 
	}

 Setup ActivityResultLauncher

In your Activity, initialize ActivityResultLauncher to handle the result of the update request:

kotlin
Copy code
import android.content.IntentSender
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

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
    }
}

3. Starting Updates

To start flexible or immediate updates, call the respective methods:

Copy code
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

4. Checking for Update Availability

Use isFlexibleUpdateAvailable and isImmediateUpdateAvailable to check if updates are available:

Copy code

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

5. Unregistering the Listener

If you need to unregister the listener (e.g., in onDestroy), call:

Copy code

override fun onDestroy() {
    super.onDestroy()
    inAppUpdateService.unregisterListener()
}

6. Handling the Update Result

In your ActivityResultLauncher callback, handle the result of the update request if needed:

kotlin

Copy code

private val updateLauncher = registerForActivityResult(

    ActivityResultContracts.StartIntentSenderForResult()
) { result ->
    // Handle the result here
}
License

This library is licensed under the MIT License.

Contact

For issues or feedback, please contact ahmed03160636141@gmail.com.
