(ns client.app
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require
   [secretary.core :as secretary]
   [goog.events :as events]
   [goog.history.EventType :as EventType]
   [reagent.core :as r]))

(def active-page (atom [:p "welcome to my website"]))
(def app-state (r/atom {}))
(defn start []
  (println "start called"))

(defn stop []
  (println "stop called"))

(defn header [title]
  [:header {:class "mainHeader"}
   [:h2 title]])

(def pages ["home"
            "interests"
            "projects"
            "blog"])

(defn nav-footer [footerpages]
  [:footer
   (for [p footerpages]
      ^{:key p}
      [:li
       [:a {:href (str "/" p ".html")} p]])])

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn home []
  [:div {:class "mainContent"}
   [header "home page"]
   [nav-footer pages]])

(defn interests []
  [:div [header "interests"]
        [nav-footer pages]])
   
(defn app-routes []
  (secretary/set-config! :prefix "#")
  (defroute "/" []
    (swap! app-state assoc :page :home))
  (defroute "/interests" []
    (swap! app-state assoc :page :interests))
  (hook-browser-navigation!))

(defmulti curr-page #(@app-state :page))
(defmethod curr-page :home []
  [home])
(defmethod curr-page :interests []
  [interests])                  

(app-routes)
(r/render [curr-page]
  (js/document.getElementById "app"))
