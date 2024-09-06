package com.codetech.lib

import android.content.Context
import android.content.IntentSender
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

/**
 * @author Muhammad Ahmed
 *
 * @since 09/06/2024
 *
 * Service class to handle in-app updates.
 * @param context
 * @param mActivityLauncher
 */
class InAppUpdateService private constructor(
    private val context: Context,
    private val mActivityLauncher: ActivityResultLauncher<IntentSenderRequest>
) {
    private var mUpdateListener: UpdateListener? = null
    private var mUpdateCallBack: UpdateCallBack? = null
    private val mUpdateManager = AppUpdateManagerFactory.create(context.applicationContext)
    private var installStateUpdatedListener: InstallStateUpdatedListener? = null
    private val mAvailableUpdateTypes by lazy { ArrayList<Int>() }

    /**
     * Builder class for constructing instances of [InAppUpdateService].
     */
    class Builder {
        private lateinit var mContext: Context
        private lateinit var mLauncher: ActivityResultLauncher<IntentSenderRequest>

        /**
         * Sets the context for the service.
         */
        fun setContext(context: Context) = apply {
            this.mContext = context
        }

        /**
         * Sets the ActivityResultLauncher for handling update intents.
         */
        fun setInAppUpdateLauncher(launcher: ActivityResultLauncher<IntentSenderRequest>) = apply {
            this.mLauncher = launcher
        }

        /**
         * Builds an instance of [InAppUpdateService].
         */
        fun build(): InAppUpdateService {
            if (!::mContext.isInitialized) {
                throw IllegalStateException("Context must be set before building")
            }
            if (!::mLauncher.isInitialized) {
                throw IllegalStateException("ActivityResultLauncher must be set before building")
            }
            return InAppUpdateService(mContext, mLauncher)
        }
    }

    /**
     * Checks for available updates and informs the listener.
     */
    fun checkForUpdate(listener: UpdateListener) {
        mUpdateListener = listener
        mUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    mAvailableUpdateTypes.add(AppUpdateType.FLEXIBLE)
                }
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    mAvailableUpdateTypes.add(AppUpdateType.IMMEDIATE)
                }
                if (mAvailableUpdateTypes.isNotEmpty()) {
                    mUpdateListener?.onUpdateAvailable(
                        appUpdateInfo.availableVersionCode(),
                        mAvailableUpdateTypes
                    )
                } else {
                    mUpdateListener?.onNoUpdateAvailable()
                }
            } else {
                mUpdateListener?.onNoUpdateAvailable()
            }
        }.addOnFailureListener {
            mUpdateListener?.onFailedToObserve(exception = it)
        }
    }

    /**
     * Starts a flexible update and registers a callback for update progress.
     */
    fun startFlexibleUpdate(listener: UpdateCallBack) {
        mUpdateCallBack = listener
        try {
            mUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    mUpdateCallBack?.availableVersion(appUpdateInfo.availableVersionCode())
                    mUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        mActivityLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                    )
                    registerInstallStateUpdatedListener()
                } else {
                    mUpdateCallBack?.onNoUpdateAvailable()
                }
            }.addOnFailureListener {
                mUpdateCallBack?.onFailure(it)
            }
        } catch (e: IntentSender.SendIntentException) {
            mUpdateCallBack?.onFailure(e)
        }
    }

    /**
     * Registers a listener to track the installation state updates.
     */
    private fun registerInstallStateUpdatedListener() {
        installStateUpdatedListener = InstallStateUpdatedListener { state ->
            when (state.installStatus()) {
                InstallStatus.DOWNLOADING -> {
                    mUpdateCallBack?.onUpdateStarted()
                    mUpdateCallBack?.onUpdateProgress(state.bytesDownloaded(), state.totalBytesToDownload())
                }
                InstallStatus.DOWNLOADED -> {
                    mUpdateCallBack?.onUpdateDownloaded()
                }
                InstallStatus.INSTALLED -> {
                    mUpdateCallBack?.onUpdateInstalled()
                }
                InstallStatus.FAILED -> {
                    mUpdateCallBack?.onUpdateFailed(state.installErrorCode())
                }
                else -> {
                    // Handle other statuses if needed
                }
            }
        }
        installStateUpdatedListener?.let {
            mUpdateManager.registerListener(it)
        }
    }

    /**
     * Starts an immediate update and registers a callback for update progress.
     */
    fun startImmediateUpdate(listener: UpdateCallBack) {
        mUpdateCallBack = listener
        try {
            mUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    mUpdateCallBack?.availableVersion(appUpdateInfo.availableVersionCode())
                    mUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        mActivityLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    )
                    registerInstallStateUpdatedListener()
                } else {
                    mUpdateCallBack?.onNoUpdateAvailable()
                }
            }.addOnFailureListener {
                mUpdateCallBack?.onFailure(it)
            }
        } catch (e: IntentSender.SendIntentException) {
            mUpdateCallBack?.onFailure(e)
        }
    }

    /**
     * Checks if a flexible update is available.
     */
    fun isFlexibleUpdateAvailable(callback: (Boolean) -> Unit) {
        mUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            callback(appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE))
        }.addOnFailureListener {
            callback(false)
        }
    }

    /**
     * Checks if an immediate update is available.
     */
    fun isImmediateUpdateAvailable(callback: (Boolean) -> Unit) {
        mUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            callback(appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE))
        }.addOnFailureListener {
            callback(false)
        }
    }

    /**
     * Unregisters the install state updated listener.
     */
    fun unregisterListener() {
        installStateUpdatedListener?.let {
            mUpdateManager.unregisterListener(it)
        }
    }
}
