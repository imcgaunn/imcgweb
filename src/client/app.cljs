(ns client.app
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require
   [secretary.core :as secretary]
   [goog.events :as events]
   [goog.history.EventType :as EventType]
   [reagent.core :as r]))

(def app-state (r/atom {}))
(defn start []
  (println "start called"))

(defn stop []
  (println "stop called"))

(defn header [title]
  [:header {:class "mainHeader"}
   [:h2 title]])


(defn nav-footer [footerpages]
  [:footer
   (for [p footerpages]
      ^{:key p}
      [:li
       [:a {:href (str "#/" p)} p]])])

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; PAGES
(def pages ["home"
            "interests"
            "projects"
            "blog"])

(defn home []
  [:div {:class "mainContent"}
   [header "home page"]
   [nav-footer pages]])

(defn interests []
  [:div {:class "mainContent"}
   [header "interests"]
   [nav-footer pages]])

(defn projects []
  [:div {:class "mainContent"}
   [header "projects"]
   [nav-footer pages]])

(defn blog []
  [:div {:class "mainContent"}
   [header "blog"]
   [nav-footer pages]])

;; ROUTES

(defn app-routes []
  (secretary/set-config! :prefix "#")
  (defroute "/" []
    (swap! app-state assoc :page :home))
  (defroute "/home" []
    (swap! app-state assoc :page :home))
  (defroute "/projects" []
    (swap! app-state assoc :page :projects))        
  (defroute "/interests" []
    (swap! app-state assoc :page :interests))
  (defroute "/blog" []
    (swap! app-state assoc :page :blog))
  (hook-browser-navigation!))

(defmulti curr-page #(@app-state :page))
(defmethod curr-page :home []
  [home])
(defmethod curr-page :interests []
  [interests])
(defmethod curr-page :projects []
  [projects])
(defmethod curr-page :blog []
  [blog])

;; MAIN

(app-routes)
(r/render [curr-page]
  (js/document.getElementById "app"))
