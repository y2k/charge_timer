(jvm! (ns im.y2k.chargetimer
        (:import [android.webkit WebView]
                 [android.app Activity NotificationChannel Notification NotificationManager]
                 [android.content Intent IntentFilter Context]
                 [android.media AudioManager RingtoneManager]
                 [java.util Objects])))

(js!
 (defn html []
   [:main
    [:h3 {:id "text_charge"} "..."]
    [:h3 {:id "text_job_status"} "..."]
    [:button {:id "btn_get_info"} "Get info"]
    [:button {:id "btn_start"} "Start"]
    [:button {:id "btn_stop"} "Stop"]
    [:button {:id "show_notification" :onclick "Android.dispatch(\"show_notification\", {})"} "Show test notification"]]))

(jvm!
 (defn- show_notification [env]
   (let [^Context context (:context env)
         nm (.getSystemService context (class NotificationManager))
         channelId "my_channel_id"
         channelName "My Channel"
         ch (NotificationChannel. channelId channelName NotificationManager/IMPORTANCE_DEFAULT)
         n (->
            (Notification.Builder. context channelId)
            (.setSmallIcon R.drawable.ic_launcher)
            (.setContentTitle "Простое уведомление")
            (.setContentText "Это текст уведомления")
            .build)]
     (.createNotificationChannel nm ch)
     (.notify! nm 1 n)))

 (defn dispatch [env event payload]
   (case event
     :show_notification (show_notification env)
     (println "[LOG] " event))))

;;  (defn show_battery_level [^WebView wv]
;;    (let [context (.getContext wv)
;;          r (.registerReceiver context null (IntentFilter. Intent/ACTION_BATTERY_CHANGED))
;;          level (.getIntExtra r "level" -1)]
;;      (.evaluateJavascript! wv (str "window.update_ui(\"#text_job_status\", " level ")") null)))

;;  (defn play_alarm [^Context context]
;;    (let [am (as (.getSystemService context Context/AUDIO_SERVICE) AudioManager)
;;          sound_stream_id 5
;;          max (.getStreamMaxVolume am sound_stream_id)]
;;      (.setStreamVolume am sound_stream_id max 0)
;;      (let [notification (.getDefaultUri RingtoneManager RingtoneManager/TYPE_ALARM)
;;            r (.getRingtone RingtoneManager context notification)]
;;        (.play! r))))

;;  (defn handle_ui_event [env]
;;    (let [^WebView wv (:webview env)]
;;      (println "FIXME: " event " / " payload)
;;     ;; (show_battery_level wv)
;;      (play_alarm (.getContext wv))))

;;  (defn handle_job_started [env]
;;    (FIXME))

;;  (defn dispatch [env event payload]
;;    (case event
;;      :job_started (handle_job_started env)
;;      (handle_ui_event)))

;; (defn start_job [env]
;;   (let [wv (:webview env)
;;         context (:context env)
;;         r (.registerReceiver context null (android.content.IntentFilter_. "android.intent.action.BATTERY_CHANGED"))
;;         level (.getIntExtra r "level" -1)]
;;     (.evaluateJavascript wv (str "window.update_ui(\"#text_job_status\", " (.toString level) ")") null)))

;; (defn job_scheduled [_]
;;   (let [result (checkNotNull (.registerReceiver self null (android.content.IntentFilter_. "android.intent.action.BATTERY_CHANGED")))
;;         level (.getIntExtra result "level" -1)]
;;     (if (> level 90)
;;       (play_alarm self)
;;       null)))
