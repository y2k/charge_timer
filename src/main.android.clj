(ns im.y2k.chargetimer (:import
                        [android.app Activity NotificationChannel Notification NotificationManager]
                        [android.app.job JobScheduler JobParameters JobInfo]
                        [android.content Intent Context IntentFilter BroadcastReceiver ComponentName]
                        [android.media AudioManager RingtoneManager]
                        [android.os Bundle]
                        [android.webkit WebView JavascriptInterface]
                        [dalvik.system DexClassLoader]
                        [java.io File]
                        [java.util List Objects]
                        [java.util.function BiFunction Consumer]
                        [java.util.stream Collectors]
                        [im.y2k.chargetimer Main_shared]))

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
  ;; (let [^Context context (:context env)
  ;;       f (File. (.getFilesDir context) "classes.dex")]
  ;;   (if (.exists f) null
  ;;       (throw (RuntimeException. (str
  ;;                                  "File not found: classes.dex | "
  ;;                                  (.getFilesDir context) " | "
  ;;                                  (.listFiles (.getFilesDir context))))))
  ;;   (->
  ;;    (DexClassLoader.
  ;;     (.getAbsolutePath f)
  ;;     null null (ClassLoader/getSystemClassLoader))
  ;;    (.loadClass "im.y2k.chargetimer.Main_shared")
  ;;    (.getMethod "dispatch" (class Object) (class Object) (class Object))
  ;;    (.invoke null env event payload)
  ;;    checked!))
  )

(defn- wv_dispatch [^WebViewJsListener self ^String event ^String payload]
  (let [[^Activity activity ^WebView wv] self.state]
    (.runOnUiThread!
     activity (fn! [] (dispatch {:context (.getContext wv) :webview wv} event payload)))))

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
