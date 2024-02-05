(ns im.y2k.chargetimer)

(defn main [^android.app.Activity activity ^android.webkit.WebView webView]
  (let [webSettings (.getSettings webView)]
    (.setJavaScriptEnabled webSettings true)
    (.addJavascriptInterface
     webView
     (proxy [] []

       android.webkit.JavascriptInterface
       (open_settings [_]
         (.runOnUiThread activity
                         (fn []
                          ;;  (let [i (android.content.Intent. "android.settings.NOTIFICATION_POLICY_ACCESS_SETTINGS")]
                          ;;    (.startActivityForResult activity i 0))
                           (let [nm (as (.getSystemService activity "notification") android.app.NotificationManager)
                                 channel_id "def_id"
                                 ch (android.app.NotificationChannel. channel_id "def_title" 3)]
                            ;;  (.createNotificationChannel nm ch)

                             (.notify nm 1
                                      (->
                                       (android.app.Notification.Builder. activity channel_id)
                                       (.setSmallIcon android.R.drawable.ic_dialog_info)
                                       (.setContentTitle "Foo")
                                       (.setContentText "Text")
                                       .build))

                             (println (str "FIXME: channel setted"))))))

       android.webkit.JavascriptInterface
       (play_alarm [_ ^Int sound]
         (.runOnUiThread activity
                         (fn []
                           (let [am (as (.getSystemService activity "audio") android.media.AudioManager)
                                 sound_stream_id 5
                                 max (.getStreamMaxVolume am sound_stream_id)]
                             (println (str "FIXME: " (.getStreamVolume am sound_stream_id) " / " max))
                             (.setStreamVolume am sound_stream_id max 0)
                             (println (str "FIXME: " (.getStreamVolume am sound_stream_id) " / " max))
                             (let [notification (.getDefaultUri android.media.RingtoneManager sound)
                                   r (.getRingtone android.media.RingtoneManager activity notification)]
                               (.play r))))))

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
