(ns client.app
  (:require-macros [secretary.core :refer [defroute]]
                   [cljs.core.async.macros :refer [go]])
  (:import goog.History)
  (:require
   [client.dateutil :as dateutil]
   [client.components.common :as comp]
   [client.components.home :as homecomps]
   [client.components.interests :as interestcomps]
   [client.components.projects :as projcomps]
   [client.components.blog :as blogcomps]
   [secretary.core :as secretary]
   [goog.events :as events]
   [goog.history.EventType :as EventType]
   [reagent.core :as r]
   [cljs.core.async :refer [<!]]))

(enable-console-print!)
(println "it would be nice if I saw this!")

;; APP STATE
;; this comment will be updated as the program
;; grows.
;; {:projects ["loading...;"]
;;  :blog-posts [{:title "", :date "", :content ""}]
;;  :current-post {:title, :date, :content}))
;;  :page :<pagename>}

(defonce app-state (r/atom {:projects ["loading... ;)"]}))

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
   [:div {:class "mainContent"}
    [interestcomps/professional-yoyo]]
   [comp/nav-footer pages]])

(def GH-USERNAME "imcgaunn")

(defn update-with-current-projects! []
  (go
    (let [proj-data
          (<! (projcomps/fetch-github-projects GH-USERNAME))]
      (swap! app-state assoc :projects proj-data))))

(defn set-page-view! [page]
  (swap! app-state
         assoc :page
         page))

(defn projects [plist]
  [:div
   [comp/header "projects"]
   [:div {:class "mainContent"}
    [projcomps/projects-showcase plist]]
   [comp/nav-footer pages]])

(defn blog-index [posts]
  [:div
   [comp/header "blog"]
   [:div {:class "mainContent"}
    [blogcomps/blog-index-page posts]]
   [comp/nav-footer pages]])

(defn blog-post [post]
  [:div
   [comp/header (:title post)]
   [:div {:class "mainContent"}
    [blogcomps/blog-post post]]
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
    (set-page-view! :home))
  (defroute "/home" []
    (set-page-view! :home))
  (defroute "/projects" []
    (set-page-view! :projects))
  (defroute "/interests" []
    (set-page-view! :interests))
  (defroute "/blog" []
    (set-page-view! :blog-index))
  (defroute "/blog/post/:title" {:as params}
    (let [title (:title params)]
     (blogcomps/set-current-post! app-state title)
     (set-page-view! :blog-post)))
  (hook-browser-navigation!))

(defmulti curr-page #(@app-state :page))
(defmethod curr-page :home []
  [home])
(defmethod curr-page :interests []
  [interests])
(defmethod curr-page :projects []
  ;; load default page and dispatch action to get real content.
  (update-with-current-projects!)
  [projects (:projects @app-state)])

(defmethod curr-page :blog-index []
  [blog-index (:blog-posts @app-state)])
(defmethod curr-page :blog-post []
  [blog-post (:current-post @app-state)])

;; MAIN

(app-routes)
(r/render [curr-page]
          (js/document.getElementById "app"))
