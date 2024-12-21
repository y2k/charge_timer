(ns _ (:require [shared :as app]
                [tools :as t]))

(defn- html []
  [:html {:lang "ru" :data-theme "dark"}
   [:head
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:link {:rel "stylesheet" :href "css/pico.classless.css"}]]
   [:body {:style "user-select: none;" :onload ""}
    (app/html)
    [:script {:type :module} "import { main } from './web/web.js'; main()"]]])

(defn- manifest []
  [:manifest {:xmlns:android "http://schemas.android.com/apk/res/android"}
   [:uses-permission {:android:name "android.permission.ACCESS_NOTIFICATION_POLICY"}]
   [:uses-permission {:android:name "android.permission.POST_NOTIFICATIONS"}]
   [:application {:android:icon "@drawable/ic_launcher"
                  :android:label "@string/app_name"
                  :android:roundIcon "@drawable/ic_launcher"
                  :android:theme "@style/Theme.ChargeTimer"}
    [:service {:android:name "app.main$ChargeJobService"
               :android:permission "android.permission.BIND_JOB_SERVICE"}]
    [:activity {:android:name "app.main$MainActivity"
                :android:configChanges "orientation|screenSize"
                :android:exported "true"
                :android:theme "@style/Theme.ChargeTimer"}
     [:intent-filter
      [:action {:android:name "android.intent.action.MAIN"}]
      [:category {:android:name "android.intent.category.LAUNCHER"}]]]]])

(case (get process.argv 2)
  :html (println (t/html_to_string (html)))
  :manifest (println (t/html_to_string (manifest)))
  null)
