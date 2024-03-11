(defn html []
  [:html {:lang "ru" :data-theme "dark"}
   [:head
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:link {:rel "stylesheet" :href "css/pico.classless.css"}]]
   [:body {:style "user-select: none;" :onload ""}
    [:main
     [:h3 {:id "text_charge"} "..."]
     [:h3 {:id "text_job_status"} "..."]
     [:button {:id "btn_get_info"} "Get info"]
     [:button {:id "btn_start"} "Start"]
     [:button {:id "btn_stop"} "Stop"]]
    [:script {:type :module} "import { main } from './js/main.js'; main()"]]])

(defn main []
  (set! (.-update_ui window)
        (fn [query text]
          (set! (.-innerHTML (.querySelector document query)) text)))

  (defn- attach_event_dispatch [query event]
    (.addEventListener
     (.querySelector document query) "click"
     (fn [] (.dispatch Android event (JSON/stringify {})))))

  (attach_event_dispatch "#btn_get_info" :play_alarm)
  (attach_event_dispatch "#btn_start" :start_job)
  (attach_event_dispatch "#btn_stop" :stop_job))
