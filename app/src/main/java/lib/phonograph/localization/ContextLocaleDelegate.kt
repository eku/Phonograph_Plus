/*
 * Copyright (c) 2022 chr_56
 */

package lib.phonograph.localization

import lib.phonograph.localization.LocalizationUtil.amendConfiguration
import lib.phonograph.localization.LocalizationUtil.createNewConfigurationContext
import android.content.ComponentCallbacks
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration

object ContextLocaleDelegate {

    /**
     * wrap [newBase] with this in [ContextWrapper.attachBaseContext]
     */
    fun attachBaseContext(newBase: Context?): Context? =
        if (newBase != null) {
            createNewConfigurationContext(
                context = newBase,
                newLocale = LocalizationStore.current(newBase)
            )
        } else {
            newBase
        }

    /**
     * Wrap [newConfig] with this in [ComponentCallbacks.onConfigurationChanged]
     */
    fun onConfigurationChanged(context: Context, newConfig: Configuration): Configuration =
        amendConfiguration(newConfig, LocalizationStore.current(context))
}
