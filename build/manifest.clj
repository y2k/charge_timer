(ns _ (:require ["../vendor/xml/0.2.0/main" :as xml]))

(xml/to_string
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