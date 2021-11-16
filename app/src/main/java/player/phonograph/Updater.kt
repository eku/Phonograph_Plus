/*
 * Copyright (c) 2021 chr_56
 */

package player.phonograph

import android.os.Bundle
import android.util.Log
import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

object Updater {
    fun checkUpdate(callback: (Bundle) -> Unit) {
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url(requestUri)
            .get()
            .build()

        okHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Fail to check new version!")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body() ?: return
                Log.i(TAG, "succeed to check new version!")

                val versionJson = Gson().fromJson<VersionJson>(responseBody.string(), VersionJson::class.java)
                Log.i(TAG, "versionCode: ${versionJson.versionCode}, version: ${versionJson.version}, logSummary: ${versionJson.logSummary}")

                when {
                    versionJson.versionCode > BuildConfig.VERSION_CODE -> {
                        Log.i(TAG, "updatable!")
                        val result = Bundle().also {
                            it.putInt(VersionCode, versionJson.versionCode)
                            it.putString(Version, versionJson.version)
                            it.putString(LogSummary, versionJson.logSummary)
                        }
                        callback.invoke(result) // call upgrade dialog
                    }
                    versionJson.versionCode == BuildConfig.VERSION_CODE -> {
                        Log.i(TAG, "no update, latest version!")
                        // do nothing
                    }
                    versionJson.versionCode < BuildConfig.VERSION_CODE -> {
                        Log.w(TAG, "no update, version is newer than latest?")
                        // do nothing
                    }
                }
            }
        })
    }

    @Keep
    class VersionJson {
        @SerializedName(Version)
        var version: String? = ""
        @SerializedName(VersionCode)
        var versionCode: Int = 0
        @SerializedName(LogSummary)
        var logSummary: String? = ""
    }

    private const val owner = "chr56"
    private const val repo = "Phonograph_Plus"
    private const val branch = "dev"
    private const val file = "version.json"

    private const val requestUri = "https://cdn.jsdelivr.net/gh/$owner/$repo@$branch/$file"

    private const val TAG = "Updater"

    const val Version = "version"
    const val VersionCode = "versionCode"
    const val LogSummary = "logSummary"
    const val Upgradable = "upgradable"
}
