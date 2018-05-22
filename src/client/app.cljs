(ns client.app
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require
   [client.components.common :as comp]
   [secretary.core :as secretary]
   [goog.events :as events]
   [goog.history.EventType :as EventType]
   [reagent.core :as r]))

(def app-state (r/atom {}))
(defn start []
  (println "start called"))

(defn stop []
  (println "stop called"))

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
   [comp/header "home page"]
   [comp/nav-footer pages]])

(defn interests []
  [:div {:class "mainContent"}
   [comp/header "interests"]
   [comp/nav-footer pages]])

(defn projects []
  [:div {:class "mainContent"}
   [comp/header "projects"]
   [comp/nav-footer pages]])

(defn blog []
  [:div {:class "mainContent"}
   [comp/header "blog"]
   [comp/nav-footer pages]])

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
