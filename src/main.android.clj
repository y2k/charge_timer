(ns im.y2k.chargetimer
  (:import [android.app Activity NotificationChannel Notification NotificationManager]
           [android.webkit WebView JavascriptInterface]
           [android.content Intent Context IntentFilter BroadcastReceiver]
           [android.media AudioManager RingtoneManager]))

(defn main [^Activity activity ^WebView webView]
  (defn play_alarm []
    (let [am (as (.getSystemService activity "audio") AudioManager)
          sound_stream_id 5
          max (.getStreamMaxVolume am sound_stream_id)]
      (.setStreamVolume am sound_stream_id max 0)
      (let [notification (.getDefaultUri RingtoneManager RingtoneManager/TYPE_ALARM)
            r (.getRingtone RingtoneManager activity notification)]
        (.play r))))

  (let [webSettings (.getSettings webView)]
    (.setJavaScriptEnabled webSettings true)
    (.setAllowUniversalAccessFromFileURLs webSettings true)
    (.addJavascriptInterface
     webView
     (proxy [] []

       JavascriptInterface
       (load_info [_]
         (.runOnUiThread activity
                         (fn []
                           (let [m (android.app.job.JobInfo/getMinPeriodMillis)]
                             (.evaluateJavascript webView (str "show_info('" m "')") null)))))

       JavascriptInterface
       (open_settings [_]
         (.runOnUiThread activity
                         (fn []
                          ;;  (let [i (Intent. "android.settings.NOTIFICATION_POLICY_ACCESS_SETTINGS")]
                          ;;    (.startActivityForResult activity i 0))
                           (let [nm (as (.getSystemService activity "notification") NotificationManager)
                                 channel_id "def_id"
                                 ch (NotificationChannel. channel_id "def_title" 3)]
                             (.createNotificationChannel nm ch)

                             (.notify nm 1
                                      (->
                                       (Notification.Builder. activity channel_id)
                                       (.setSmallIcon android.R.drawable.ic_dialog_info)
                                       (.setContentTitle "Foo")
                                       (.setContentText "Text")
                                       .build))))))

       JavascriptInterface
       (play_alarm [_]
         (.runOnUiThread activity (fn [] (play_alarm))))

       JavascriptInterface
       (register_broadcast [_ ^String topic ^String action]
         (.runOnUiThread activity
                         (fn []
                           (do_register_receiver
                            activity action
                            (fn [^String json]
                              (.evaluateJavascript webView (str topic "('" json "')") null)))))))
     "Android")
    (.loadUrl webView "file:///android_asset/index.html")))

(defn do_register_receiver [^Context context ^String action ^"(String)->Unit" callback]
  (.registerReceiver
   context
   (proxy
    [BroadcastReceiver] []
     (onReceive [_ ^Context context ^Intent intent]
       (let [extras (requireNotNull intent.extras)
             allValues (->
                        extras
                        .keySet
                        (.map (fn [key] (Pair. key (.get extras key))))
                        (.associate (fn [x] x)))]
         (callback (.toJson (com.google.gson.Gson.) allValues)))))
   (IntentFilter. action)))
