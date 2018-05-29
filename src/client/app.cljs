(ns client.app
  (:require-macros [secretary.core :refer [defroute]]
                   [cljs.core.async.macros :refer [go]])
  (:import goog.History)
  (:require
   [client.components.common :as comp]
   [client.components.home :as homecomps]
   [client.components.projects :as projcomps]
   [secretary.core :as secretary]
   [goog.events :as events]
   [goog.history.EventType :as EventType]
   [reagent.core :as r]
   [cljs.core.async :refer [<!]]))

(def app-state (r/atom {:projects ["loading."]}))
(defn start []
  (println "start called"))
(defn stop []
  (println "stop called"))

;; PAGES
(def pages ["home"
            "interests"
            "projects"
            "blog"])

(defn home []
  [:div
   [comp/header "home"]
   [:div {:class "mainContent"}
    [homecomps/main]]
   [comp/nav-footer pages]])

(defn interests []
  [:div
   [comp/header "interests"]
   [:div {:class "mainContent"}]
   [comp/nav-footer pages]])

(defn projects []
  [:div
   [comp/header "projects"]
   [:div {:class "mainContent"}]
   [comp/nav-footer pages]])

(defn blog []
  [:div
   [comp/header "blog"]
   [:div {:class "mainContent"}]
   [comp/nav-footer pages]])

;; ROUTES

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

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
  ;; load default page and dispatch action to get real content.
  (go
    (let [proj-data
          (<! (projcomps/fetch-github-projects "imcgaunn"))]
      (swap! app-state assoc :projects
             (for [proj proj-data]
               (:name proj)))))
  [:div
   (for [p (:projects @app-state)]
     ^{:key p} [:p p])])

(defmethod curr-page :blog []
  [blog])
;; MAIN

(app-routes)
(r/render [curr-page]
          (js/document.getElementById "app"))
