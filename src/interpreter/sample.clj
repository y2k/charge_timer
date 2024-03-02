{:play_alarm
 (fn [context]
   (let [am (.getSystemService context (.loadClass context.javaClass.classLoader "android.app.AlarmManager"))
         sound_stream_id 5
         max (.getStreamMaxVolume am sound_stream_id)]
    ;;  (println "Hello World")
     (.setStreamVolume am sound_stream_id max 0)
     (let [notification (.getDefaultUri RingtoneManager 4)
           r (.getRingtone RingtoneManager context notification)]
       (.play r))))}
