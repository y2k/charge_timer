(ns resources (:require [main.shared :as app]))

(defn- html []
  [:html {:lang "ru" :data-theme "dark"}
   [:head
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:link {:rel "stylesheet" :href "css/pico.classless.css"}]]
   [:body {:style "user-select: none;" :onload ""}
    (app/html)
    [:script {:type :module} "import { main } from './js/main.js'; main()"]]])

(defn- manifest []
  [:manifest {:xmlns:android "http://schemas.android.com/apk/res/android"}
   [:uses-permission {:android:name "android.permission.ACCESS_NOTIFICATION_POLICY"}]
   [:uses-permission {:android:name "android.permission.POST_NOTIFICATIONS"}]
   [:application {:android:icon "@drawable/ic_launcher"
                  :android:label "@string/app_name"
                  :android:roundIcon "@drawable/ic_launcher"
                  :android:theme "@style/Theme.ChargeTimer"}
    [:service {:android:name ".Main_android$ChargeJobService"
               :android:permission "android.permission.BIND_JOB_SERVICE"}]
    [:activity {:android:name ".Main_android$MainActivity"
                :android:configChanges "orientation|screenSize"
                :android:exported "true"
                :android:theme "@style/Theme.ChargeTimer"}
     [:intent-filter
      [:action {:android:name "android.intent.action.MAIN"}]
      [:category {:android:name "android.intent.category.LAUNCHER"}]]]]])

(defn- html_to_string [node]
  (let [tag (.at node 0)
        attrs (.at node 1)
        has_attrs (and (> node.length 1) (= (type (.at node 1)) "object") (not (Array.isArray (.at node 1))))]
    (if (= (type node) :string)
      node
      (str "<" tag " "
           (if (not has_attrs) ""
               (->
                (Object.entries attrs)
                (.reduce (fn [a x] (str a " " (.at x 0) "='" (.at x 1) "'")) ""))) ">"
           (->
            (.slice node (if has_attrs 2 1))
            (.map html_to_string)
            (.reduce (fn [a x] (str a x)) ""))
           "</" tag ">"))))

(case (get process.argv 2)
  :html (println (html_to_string (html)))
  :manifest (println (html_to_string (manifest)))
  null)
