(ns im.y2k.chargetimer
  (:import [android.app Activity NotificationChannel Notification NotificationManager]
           [android.webkit WebView JavascriptInterface]
           [android.content Intent Context IntentFilter BroadcastReceiver ComponentName]
           [android.media AudioManager RingtoneManager]
           [android.app.job JobScheduler JobParameters JobInfo]))

(gen-class
 :name ChargeJobService
 :extends android.app.job.JobService
 :prefix "_"
 :methods [[onStartJob [JobParameters] Boolean]
           [onStopJob [JobParameters] Boolean]])

(defn _onStartJob [^ChargeJobService context ^JobParameters _]
  (let [result (checkNotNull (.registerReceiver context null (IntentFilter. "android.intent.action.BATTERY_CHANGED")))
        level (.getIntExtra result "level" -1)]
    (if (> level 90)
      (play_alarm context)
      null))
  false)

(defn _onStopJob [^ChargeJobService _ ^JobParameters _]
  false)

(defn play_alarm [^Context context]
  (let [am (as (.getSystemService context Context/AUDIO_SERVICE) AudioManager)
        sound_stream_id 5
        max (.getStreamMaxVolume am sound_stream_id)]
    (.setStreamVolume am sound_stream_id max 0)
    (let [notification (.getDefaultUri RingtoneManager RingtoneManager/TYPE_ALARM)
          r (.getRingtone RingtoneManager context notification)]
      (.play r))))

(defn main [^Activity activity ^WebView webView]
  (let [webSettings (.getSettings webView)]
    (.setJavaScriptEnabled webSettings true)
    (.setAllowUniversalAccessFromFileURLs webSettings true)
    (.addJavascriptInterface
     webView
     (proxy [] []

       JavascriptInterface
       (start_job [_]
         (.runOnUiThread activity
                         (fn []
                           (let [job_info (->
                                           (JobInfo.Builder. 123 (ComponentName. activity "im.y2k.chargetimer.ChargeJobService"))
                                           (.setPeriodic 300000)
                                           (.setRequiresCharging true)
                                           .build)
                                 job_scheduler (as (.getSystemService activity Context.JOB_SCHEDULER_SERVICE) JobScheduler)]
                             (.schedule job_scheduler job_info)))))

       JavascriptInterface
       (stop_job [_]
         (.runOnUiThread activity
                         (fn []
                           (.cancel
                            (as (.getSystemService activity Context.JOB_SCHEDULER_SERVICE) JobScheduler)
                            123))))

       JavascriptInterface
       (get_job_info [_ ^String callback]
         (.runOnUiThread activity
                         (fn []
                           (let [service (as (.getSystemService activity Context.JOB_SCHEDULER_SERVICE) JobScheduler)
                                 m (android.app.job.JobInfo/getMinPeriodMillis)
                                 reason (.getPendingJob service 123)]
                             (.evaluateJavascript webView (str callback "('" m " / " reason "')") null)))))

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
