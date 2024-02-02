package im.y2k.chargetimer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.google.gson.Gson

class MainActivity : Activity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webView = WebView(this).also(::setContentView)

        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(
            object {
                @JavascriptInterface
                fun registerBroadcast(topic: String, action: String) {
                    runOnUiThread {
                        doRegisterReceiver(action) {
                            webView.evaluateJavascript("""globalDispatch("$topic", '$it')""", null)
                        }
                    }
                }
            },
            "Android"
        )

        webView.loadUrl("file:///android_asset/index.html")
    }

    fun doRegisterReceiver(action: String, callback: (String) -> Unit) {
        registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val allValues = intent.extras
                        ?.keySet()
                        ?.map { key ->
                            @Suppress("DEPRECATION")
                            key to intent.extras?.get(key)
                        }
                        ?.associate { it }
                        ?: emptyMap()
                    callback(Gson().toJson(allValues))
                }
            },
            IntentFilter(action)
        )
    }
}