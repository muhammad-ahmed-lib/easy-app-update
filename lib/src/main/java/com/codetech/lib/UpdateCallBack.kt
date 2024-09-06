package com.codetech.lib

import java.lang.Exception

/**
 * Interface to handle various update-related callbacks.
 */
interface UpdateCallBack {

    /**
     * Called when a new update is available.
     *
     * @param version The version code of the available update.
     */
    fun availableVersion(version: Int)

    /**
     * Called when no update is available.
     */
    fun onNoUpdateAvailable()

    /**
     * Called when the update process has started.
     */
    fun onUpdateStarted()

    /**
     * Called to report the progress of a flexible update.
     *
     * @param bytesDownloaded The number of bytes downloaded so far.
     * @param totalBytesToDownload The total number of bytes to download.
     */
    fun onUpdateProgress(bytesDownloaded: Long, totalBytesToDownload: Long)

    /**
     * Called when the update has been successfully downloaded and is ready to be installed.
     */
    fun onUpdateDownloaded()

    /**
     * Called when the update has been successfully installed.
     */
    fun onUpdateInstalled()

    /**
     * Called when the update fails to proceed.
     *
     * @param errorCode An integer representing the error code of the failure.
     */
    fun onUpdateFailed(errorCode: Int)

    /**
     * Called when an exception occurs during the update process.
     *
     * @param exception The exception that occurred.
     */
    fun onFailure(exception: Exception)
}
