(defn- html []
  [:html {:lang "ru" :data-theme "dark"}
   [:head
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:link {:rel "stylesheet" :href "css/pico.classless.css"}]]
   [:body {:style "user-select: none;"}
    [:main
     [:h3 {:id "text1"} "..."]
     [:h3 {:id "text2"} "..."]
     [:button {:id "btn"} "Increment"]
     [:button {:id "btn2"} "Test sound"]]
    [:script {:src "js/main.js"}]]])

(def ALARM_PERCENT 90)
(def percent_ref (Array/of 0))

(defn- charge_changed [message]
  (if-let [battery_percent (.-level (JSON/parse message))]
    (do
      (set!
       (.-innerHTML (.querySelector document "#text1"))
       (str "Battery: " battery_percent " / " ALARM_PERCENT))

      (if-let [_ (< (first percent_ref) ALARM_PERCENT)]
        (do
          (.pop percent_ref)
          (.push percent_ref battery_percent)

          (if (>= battery_percent ALARM_PERCENT)
            (.play_alarm Android 4)
            null))
        null))
    null))

(defn- main []
  (.addEventListener
   (.querySelector document "#btn2") "click"
   (fn []
     (set! (.-innerHTML (.querySelector document "#text2")) "BEFORE")
     (.play_alarm Android 4)
     (set! (.-innerHTML (.querySelector document "#text2")) "AFTER")))

  (let [count_ref (Array/of 0)]
    (.addEventListener
     (.querySelector document "#btn") "click"
     (let [count_ref (Array/of 0)]
       (fn []
         (.push count_ref (+ 1 (.pop count_ref)))
         (set! (.-innerHTML (.querySelector document "#text2")) (.at count_ref 0))))))

  (.registerBroadcast Android :charge_changed "android.intent.action.BATTERY_CHANGED"))

;; Infrastructure

(if globalThis.navigator
  (main)
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
    (println (html_to_string (html)))))
