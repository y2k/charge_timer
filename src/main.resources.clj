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
     [:button {:id "btn3"} "Open Settings"]
     [:button {:id "btn2"} "Test sound"]]
    [:script {:src "js/main.js"}]]])

;; Infrastructure

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
