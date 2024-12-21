(def ^int LIMIT_CHARGE 80)

(defn html []
  (defn- button [title event]
    [:button {:onclick (str "Android.dispatch(\"" event "\", {})")} title])
  [:main
   [:h3 {:id "text_charge"} "..."]
   [:h3 {:id "text_job_status"} "..."]
   (button "Get info" "get_info_pressed")
   (button "Start" "start_pressed")
   (button "Stop" "stop_pressed")
   (button "Show test notification" "show_notification")
   (button "Play alarm" "play_alarm_pressed")])
