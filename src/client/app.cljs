(ns client.app
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:import goog.History)
  (:require
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

;; APP STATE
;; this comment will be updated as the program
;; grows.
; {:projects ["loading...;"]
;  :blog-posts [{:title "", :date "", :content ""}]
;  :current-post {:title, :date, :content}
;  :post-index {<id> {:title "" :post-s3-loc "" ...}}
;  :page :<pagename>}

(defonce app-state (r/atom {:projects ["loading... ;)"]
                            :current-post {}}))

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

(defn blog-index [index]
  [:div
   [comp/header "blog"]
   [:div {:class "mainContent"}
    [blogcomps/blog-index-page index]]
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

(defn fetch-post->set-page-view [id]
  (go
    (let [respchan (blogcomps/fetch-blog-post! app-state (js/parseInt id 10))
          post (<! respchan)]
      (blogcomps/set-current-post! app-state post)
      (set-page-view! :blog-post))))

(defn app-routes []
  (secretary/set-config! :prefix "#")
  (secretary/defroute "/" []
    (set-page-view! :home))
  (secretary/defroute "/home" []
    (set-page-view! :home))
  (secretary/defroute "/projects" []
    (projcomps/update-saved-projects! app-state GH-USERNAME)
    (set-page-view! :projects))
  (secretary/defroute "/interests" []
    (set-page-view! :interests))
  (secretary/defroute "/blog" []
    (blogcomps/fetch-post-idx! app-state)
    (set-page-view! :blog-index))
  (secretary/defroute "/blog/post/:id" {id :id}
    (fetch-post->set-page-view id))
  (hook-browser-navigation!))

(defmulti curr-page #(@app-state :page))
(defmethod curr-page :home []
  [home])
(defmethod curr-page :interests []
  [interests])
(defmethod curr-page :projects []
  [projects (:projects @app-state)])
(defmethod curr-page :blog-index []
  [blog-index (:post-index @app-state)])
(defmethod curr-page :blog-post []
  [blog-post (:current-post @app-state)])

;; MAIN

(app-routes)
(r/render [curr-page]
          (js/document.getElementById "app"))
