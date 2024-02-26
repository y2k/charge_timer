(ns im.y2k.chargetimer
  (:import [android.app Activity NotificationChannel Notification NotificationManager]
           [android.webkit WebView JavascriptInterface]
           [android.content Intent Context IntentFilter BroadcastReceiver ComponentName]
           [android.media AudioManager RingtoneManager]
           [android.app.job JobScheduler JobParameters JobInfo]))

(gen-class
 :name MyJobService
 :extends android.app.job.JobService
 :prefix "_"
 :methods [[onStartJob [JobParameters] Boolean]
           [onStopJob [JobParameters] Boolean]])

(defn _onStartJob [^MyJobService context ^JobParameters _]
  (println "FIXME: onStartJob 1")
  (let [result (.registerReceiver context null (IntentFilter. "android.intent.action.BATTERY_CHANGED"))]
    (println (str "FIXME: onStartJob 2 - " result)))
  false)

(defn _onStopJob [^MyJobService _ ^JobParameters _]
  (println "FIXME: onStopJob")
  false)

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
       (start_job [_]
         (.runOnUiThread activity
                         (fn []
                           (println (str "FIXME: schedule() [1]"))
                           (let [job_info (->
                                           (JobInfo.Builder. 123 (ComponentName. activity "im.y2k.chargetimer.MyJobService"))
                                           (.setPeriodic 300000)
                                           (.setRequiredNetworkType JobInfo.NETWORK_TYPE_ANY)
                                           .build)
                                 job_scheduler (as (.getSystemService activity Context.JOB_SCHEDULER_SERVICE) JobScheduler)]
                             (println (str "FIXME: schedule() [2] " job_info))
                             (println (str "FIXME: schedule(" job_info ") -> " (.schedule job_scheduler job_info)))))))

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

      ;;  JavascriptInterface
      ;;  (open_settings [_]
      ;;    (.runOnUiThread activity
      ;;                    (fn []
      ;;                      (let [nm (as (.getSystemService activity "notification") NotificationManager)
      ;;                            channel_id "def_id"
      ;;                            ch (NotificationChannel. channel_id "def_title" 3)]
      ;;                        (.createNotificationChannel nm ch)

      ;;                        (.notify nm 1
      ;;                                 (->
      ;;                                  (Notification.Builder. activity channel_id)
      ;;                                  (.setSmallIcon android.R.drawable.ic_dialog_info)
      ;;                                  (.setContentTitle "Foo")
      ;;                                  (.setContentText "Text")
      ;;                                  .build))))))

      ;;  JavascriptInterface
      ;;  (play_alarm [_]
      ;;    (.runOnUiThread activity (fn [] (play_alarm))))

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
