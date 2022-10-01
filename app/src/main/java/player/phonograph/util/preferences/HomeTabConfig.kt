/*
 * Copyright (c) 2022 chr_56
 */

package player.phonograph.util.preferences

import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import player.phonograph.model.pages.PageConfig
import player.phonograph.model.pages.PageConfigUtil
import player.phonograph.model.pages.PageConfigUtil.fromJson
import player.phonograph.model.pages.PageConfigUtil.toJson
import player.phonograph.settings.Setting

object HomeTabConfig {
    var homeTabConfig: PageConfig
        get() {
            val rawString = Setting.instance.homeTabConfigJsonString
            val config: PageConfig = try {
                JSONObject(rawString).fromJson()
            } catch (e: JSONException) {
                Log.e("Preference", "home tab config string $rawString")
                Log.e("Preference", "Fail to parse home tab config string\n ${e.message}")
                // return default
                PageConfig.DEFAULT_CONFIG
            }
            // valid // TODO
            return config
        }
        set(value) {
            val json =
                try {
                    value.toJson()
                } catch (e: JSONException) {
                    Log.e("Preference", "Save home tab config failed, use default. \n${e.message}")
                    // return default
                    PageConfigUtil.DEFAULT_CONFIG
                }
            Setting.instance.homeTabConfigJsonString = json.toString(0)
        }

    fun resetHomeTabConfig() {
        Setting.instance.homeTabConfigJsonString = PageConfigUtil.DEFAULT_CONFIG.toString(0)
    }
}