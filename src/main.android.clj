(ns im.y2k.chargetimer)

(defn main [^android.app.Activity activity ^android.webkit.WebView webView]
  (let [webSettings (.getSettings webView)]
    (.setJavaScriptEnabled webSettings true)
    (.addJavascriptInterface
     webView
     (proxy [] []
       android.webkit.JavascriptInterface
       (play_alarm [_ ^Int sound]
         (.runOnUiThread activity
                         (fn []
                           (let [notification (.getDefaultUri android.media.RingtoneManager sound)
                                 r (.getRingtone android.media.RingtoneManager activity notification)]
                             (.play r)))))
       android.webkit.JavascriptInterface
       (registerBroadcast [_ ^String topic ^String action]
         (.runOnUiThread activity
                         (fn []
                           (do_register_receiver
                            activity action
                            (fn [^String json]
                              (.evaluateJavascript webView (str topic "('" json "')") null)))))))
     "Android")
    (.loadUrl webView "file:///android_asset/index.html")))

(defn do_register_receiver [^android.content.Context context ^String action ^"(String)->Unit" callback]
  (.registerReceiver
   context
   (proxy
    [android.content.BroadcastReceiver] []
     (onReceive [_ ^android.content.Context context ^android.content.Intent intent]
       (let [extras (requireNotNull intent.extras)
             allValues (->
                        extras
                        .keySet
                        (.map (fn [key] (Pair. key (.get extras key))))
                        (.associate (fn [x] x)))]
         (callback (.toJson (com.google.gson.Gson.) allValues)))))
   (android.content.IntentFilter. action)))




