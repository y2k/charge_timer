(defn- html []
  [:html {:lang "ru" :data-theme "dark"}
   [:head
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:link {:rel "stylesheet" :href "css/pico.classless.css"}]]
   [:body {:style "user-select: none;"}
    [:main
     [:h3 {:id "text1"} "..."]
     [:h3 {:id "text2"} "..."]
     [:button {:id "btn"} "Increment"]]
    [:script {:src "js/main.js"}]]])

(defn- charge_changed [message]
  (set!
   (.-innerHTML (.querySelector document "#text1"))
   (str "Battery: " (.-level (JSON/parse message)) "%")))

(defn- main []
  (let [count_ref (Array/of 0)]
    (.addEventListener (.querySelector document "#btn") "click"
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
