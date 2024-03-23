(ns im.y2k.chargetimer
  (:import [android.app Activity NotificationChannel Notification NotificationManager]
           [android.webkit WebView JavascriptInterface]
           [android.content Intent Context IntentFilter BroadcastReceiver ComponentName]
           [android.media AudioManager RingtoneManager]
           [android.os Bundle]
           [android.app.job JobScheduler JobParameters JobInfo]
           [java.util List Objects]
           [java.util.function BiFunction Consumer]
           [java.util.stream Collectors]
           [java.io File]
           [dalvik.system DexClassLoader]))

(gen-class
 :name MainActivity
 :extends Activity
 :constructors {[] []}
 :prefix "a_"
 :methods [[^Override onCreate [Bundle] void]])

(defn a_onCreate [^MainActivity self ^Bundle bundle]
  (let [webview (WebView. self)
        webSettings (.getSettings webview)]
    (.setContentView self webview)
    (.setJavaScriptEnabled webSettings true)
    (.setAllowUniversalAccessFromFileURLs webSettings true)
    (.addJavascriptInterface webview (WebViewJsListener. self webview) "Android")
    (.loadUrl! webview "file:///android_asset/index.html")))

(gen-class
 :name WebViewJsListener
 :extends Object
 :constructors {[Activity WebView] []}
 :prefix "wv_"
 :methods [[^JavascriptInterface dispatch [String String] void]])

(defn dispatch [env event payload]
  (Main_shared/dispatch env event payload)
  ;; (let [^Context context (:context env)]
  ;;   (->
  ;;    (DexClassLoader.
  ;;     (.getAbsolutePath (File. (.getFilesDir context) "classes.dex"))
  ;;     null null (ClassLoader/getSystemClassLoader))
  ;;    (.loadClass "im.y2k.chargetimer.Main_shared")
  ;;    (.getMethod "dispatch" (class Object) (class Object) (class Object))
  ;;    (.invoke null env event payload)
  ;;    checked!))
  )

(defn- wv_dispatch [^WebViewJsListener self ^String event ^String payload]
  (let [[^Activity activity ^WebView wv] self.state]
    (.runOnUiThread!
     activity (fn! []
                   (dispatch {:context (.getContext wv) :webview wv} event payload)))))

(gen-class
 :name ChargeJobService
 :extends android.app.job.JobService
 :constructors {[] []}
 :prefix "cj_"
 :methods [[onStartJob [JobParameters] boolean]
           [onStopJob [JobParameters] boolean]])

(defn cj_onStartJob [^ChargeJobService self ^JobParameters p]
  (dispatch {:context self} :job_started null)
  false)

(defn cj_onStopJob [^ChargeJobService self ^JobParameters p]
  false)

;; (defn play_alarm [^Context context]
;;   (let [am (as (.getSystemService context Context/AUDIO_SERVICE) AudioManager)
;;         sound_stream_id 5
;;         max (.getStreamMaxVolume am sound_stream_id)]
;;     (.setStreamVolume am sound_stream_id max 0)
;;     (let [notification (.getDefaultUri RingtoneManager RingtoneManager/TYPE_ALARM)
;;           r (.getRingtone RingtoneManager context notification)]
;;       (.play! r))))

;; (defn ^"BiFunction<String,Object,Object>" make_dispatch [^Activity activity ^WebView webView]
;;   (fn [^String event ^Object payload]
;;     (case event

;;       :start_job
;;       (let [job_info (->
;;                       (JobInfo.Builder. 123 (ComponentName. activity "im.y2k.chargetimer.ChargeJobService"))
;;                       (.setPeriodic 300000)
;;                       (.setRequiresCharging true)
;;                       .build)
;;             job_scheduler (as (.getSystemService activity Context.JOB_SCHEDULER_SERVICE) JobScheduler)]
;;         (.schedule job_scheduler job_info))

;;       :stop_job
;;       (.cancel!
;;        (.getSystemService activity (class JobScheduler))
;;        123)

;;       :get_job_info
;;       (let [callback "(FIXME)"
;;             service (as (.getSystemService activity Context.JOB_SCHEDULER_SERVICE) JobScheduler)
;;             m (android.app.job.JobInfo/getMinPeriodMillis)
;;             reason (.getPendingJob service 123)]
;;         (.evaluateJavascript! webView (str callback "('" m " / " reason "')") null))

;;       null)))

;; (gen-class
;;  :name BatteryBroadcastReceiver
;;  :extends BroadcastReceiver
;;  :constructors {["Consumer<String>"] []}
;;  :prefix "br_"
;;  :methods [[^Override onReceive [Context Intent] void]])

;; (defn- br_onReceive [^BatteryBroadcastReceiver self ^Context context ^Intent intent]
;;   (let [extras (.getExtras intent)
;;         ^"Consumer<String>" callback (get self.state 0)
;;         allValues (->
;;                    extras
;;                    .keySet
;;                    .stream
;;                    (.collect (Collectors.toMap
;;                               (fn [key] key)
;;                               (fn [key] (.get extras key)))))]
;;     (.accept! callback (.toJson (com.google.gson.Gson.) allValues))))

;; (defn do_register_receiver [^Context context ^String action ^"Consumer<String>" callback]
;;   (.registerReceiver
;;    context
;;    (BatteryBroadcastReceiver. callback)
;;    (IntentFilter. action)))
