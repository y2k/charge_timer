(ns _ (:require ["../vendor/xml/0.2.0/main" :as xml]))

(defn- button [title event]
  [:button {:onclick (str "Android.dispatch(\"" event "\", {})")} title])

(xml/to_string
 [:html {:lang "ru" :data-theme "dark"}
  [:head
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
   [:link {:rel "stylesheet" :href "css/pico.classless.css"}]]
  [:body {:style "user-select: none;" :onload ""}
   [:main
    [:h3 {:id "text_charge"} "..."]
    [:h3 {:id "text_job_status"} "..."]
    (button "Get info" "get_info_pressed")
    (button "Start" "start_pressed")
    (button "Stop" "stop_pressed")
    (button "Show test notification" "show_notification")
    (button "Play alarm" "play_alarm_pressed")]
   [:script {:type :module} "import { main } from './web/web.js'; main()"]]])