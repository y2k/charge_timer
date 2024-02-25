(defn- html []
  [:manifest {:xmlns:android "http://schemas.android.com/apk/res/android"}
   [:application {:android:allowBackup "true"
                  :android:dataExtractionRules "@xml/data_extraction_rules"
                  :android:fullBackupContent "@xml/backup_rules"
                  :android:icon "@drawable/ic_launcher"
                  :android:label "@string/app_name"
                  :android:roundIcon "@drawable/ic_launcher"
                  :android:supportsRtl "true"
                  :android:theme "@style/Theme.ChargeTimer"}
    [:service {:android:name ".MyJobService"
               :android:permission "android.permission.BIND_JOB_SERVICE"}]
    [:activity {:android:name ".MainActivity"
                :android:configChanges "orientation|screenSize"
                :android:exported "true"
                :android:theme "@style/Theme.ChargeTimer"}
     [:intent-filter
      [:action {:android:name "android.intent.action.MAIN"}]
      [:category {:android:name "android.intent.category.LAUNCHER"}]]]]])

(do
  (defn- html_to_string [node]
    (let [tag (.at node 0)
          attrs (.at node 1)
          has_attrs (and (> node.length 1) (= (type (.at node 1)) "object") (not (Array/isArray (.at node 1))))]
      (if (= (type node) :string)
        node
        (str "<" tag " "
             (if (not has_attrs) ""
                 (->
                  (Object/entries attrs)
                  (.reduce (fn [a x] (str a " " (.at x 0) "='" (.at x 1) "'")) ""))) ">"
             (->
              (.slice node (if has_attrs 2 1))
              (.map html_to_string)
              (.reduce (fn [a x] (str a x)) ""))
             "</" tag ">"))))
  (println (html_to_string (html))))
