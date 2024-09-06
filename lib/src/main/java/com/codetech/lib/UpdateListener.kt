package com.codetech.lib

import java.lang.Exception

/**
 * Interface to handle various update-related notifications.
 */
interface UpdateListener {

    /**
     * Called when an update is available.
     *
     * @param version The version code of the available update.
     * @param availableUpdateTypes A list of available update types for the update.
     *                             For example, this could include values for immediate and flexible updates.
     */
    fun onUpdateAvailable(version: Int, availableUpdateTypes: List<Int>)

    /**
     * Called when no update is available for the app.
     */
    fun onNoUpdateAvailable()

    /**
     * Called when the process to observe updates fails.
     *
     * @param exception The exception that occurred during the observation process.
     */
    fun onFailedToObserve(exception: Exception)
}
