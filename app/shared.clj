(ns im.y2k.chargetimer
  (:import [android.app Activity NotificationChannel Notification NotificationManager]
           [android.app.job JobScheduler JobParameters JobInfo]
           [android R]
           [android.content Intent IntentFilter Context ComponentName]
           [android.media AudioManager RingtoneManager]
           [android.os Handler]
           [java.util Objects]
           [android.webkit WebView]))

(def ^int LIMIT_CHARGE 80)

(defn- get_status [env]
  (let [^Context ctx (:context env)
        ^WebView wv (:webview env)
        level (.getIntExtra (.registerReceiver ctx nil (IntentFilter. Intent/ACTION_BATTERY_CHANGED)) "level" -1)
        m (/ (JobInfo/getMinPeriodMillis) 1000)
        reason (.getPendingJob (.getSystemService ctx (class JobScheduler)) 123)]
    (.evaluateJavascript wv (str "window.update_ui(\"#text_job_status\", \"" level "% | " LIMIT_CHARGE "% | " m " sec | " reason "\")") nil)
    unit))

(defn- start_job [env]
  (let [^Context activity (:context env)
        job_info (->
                  (JobInfo.Builder. 123 (ComponentName. activity "app.main$ChargeJobService"))
                  (.setPeriodic 300000)
                  (.setRequiresCharging true)
                  .build)
        job_scheduler (.getSystemService activity (class JobScheduler))]
    (.schedule job_scheduler job_info)
    (get_status env)))

(defn- stop_job [env]
  (let [^Context activity (:context env)]
    (.cancel
     (.getSystemService activity (class JobScheduler))
     123)
    (get_status env)))

(defn- show_notification [env]
  (let [^Context context (:context env)
        nm (.getSystemService context (class NotificationManager))
        channelId "test_channel"
        ch (NotificationChannel. channelId channelId NotificationManager/IMPORTANCE_DEFAULT)
        n (->
           (Notification.Builder. context channelId)
           (.setSmallIcon R.drawable.ic_dialog_info)
           (.setContentTitle "Test")
           (.setContentText "Test")
           .build)]
    (.createNotificationChannel nm ch)
    (.notify nm 1 n)
    unit))

(defn- play_alarm [^Context context]
  (let [am (as (.getSystemService context Context/AUDIO_SERVICE) AudioManager)
        sound_stream_id 5
        max (.getStreamMaxVolume am sound_stream_id)]
    (.setStreamVolume am sound_stream_id max 0)
    (let [notification (.getDefaultUri RingtoneManager RingtoneManager/TYPE_ALARM)
          r (.getRingtone RingtoneManager context notification)]
      (.play r)
      (.postDelayed
       (Handler.)
       (runnable (fn [] (.stop r) unit))
       1000))))

(defn- play_alarm_pressed [env]
  (play_alarm (as (:context env) Context)))

(defn- job_scheduled [env]
  (let [^Context ctx (:context env)
        level (.getIntExtra (.registerReceiver ctx nil (IntentFilter. Intent/ACTION_BATTERY_CHANGED)) "level" -1)]
    (if (> level LIMIT_CHARGE)
      (play_alarm ctx)
      nil)))

(defn dispatch [env event payload]
  (case event
    :show_notification (show_notification env)
    :get_info_pressed (get_status env)
    :start_pressed (start_job env)
    :stop_pressed (stop_job env)
    :play_alarm_pressed (play_alarm_pressed env)
    :job_started (job_scheduled env)
    (println "[ERROR] unknown event: " event)))
