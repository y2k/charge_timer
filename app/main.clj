(ns _ (:import
       [android.app Activity]
       [android.app.job JobParameters]
       [android.os Bundle]
       [android.webkit JavascriptInterface WebView])
    (:require ["./shared" :as ms]))

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
    (.loadUrl webview "file:///android_asset/index.html")
    unit))

(gen-class
 :name WebViewJsListener
 :extends Object
 :constructors {[Activity WebView] []}
 :prefix "wv_"
 :methods [[^JavascriptInterface dispatch [String String] void]])

(defn dispatch [env event payload]
  (ms/dispatch env event payload))

(defn- wv_dispatch [^WebViewJsListener self ^String event ^String payload]
  (let [[^Activity activity ^WebView wv] self.state]
    (.runOnUiThread
     activity (runnable
               (fn! [] (dispatch {:context (.getContext wv) :webview wv} event payload))))
    unit))

(gen-class
 :name ChargeJobService
 :extends android.app.job.JobService
 :constructors {[] []}
 :prefix "cj_"
 :methods [[onStartJob [JobParameters] boolean]
           [onStopJob [JobParameters] boolean]])

(defn cj_onStartJob [^ChargeJobService self ^JobParameters p]
  (dispatch {:context self} :job_started nil)
  false)

(defn cj_onStopJob [^ChargeJobService self ^JobParameters p]
  false)
