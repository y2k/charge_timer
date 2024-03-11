[[:play_alarm
  (fn [context]
    (let [am (.getSystemService context (.loadClass (.getClassLoader (.getClass "")) "android.media.AudioManager"))
          sound_stream_id 5
          max (.getStreamMaxVolume am sound_stream_id)]
      (.setStreamVolume am sound_stream_id max 0)
      (let [notification (.getDefaultUri android.media.RingtoneManager 4)
            r (.getRingtone android.media.RingtoneManager context notification)]
        (.play r))))]
 [:job_scheduled
  (fn [_]
    (let [result (checkNotNull (.registerReceiver self null (IntentFilter. "android.intent.action.BATTERY_CHANGED")))
          level (.getIntExtra result "level" -1)]
      (if (> level 90)
        (play_alarm self)
        null)))]]
