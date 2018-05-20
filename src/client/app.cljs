(ns client.app
  (:require
   [reagent.core :as r]))

(def active-page (atom [:p "welcome to my website"]))
    
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

(defn main-view [content] 
  [:div {:class "mainContent"}
   content])

(defn nav-footer [footerpages]
  [:footer
   (for [p footerpages]
      ^{:key p}
      [:li
       [:a {:href (str "/" p ".html")} p]])])

(defn app-container []
  [:div
   [header "ian's website"]
   [main-view @active-page]
   [nav-footer pages]])
    
(r/render [app-container]
  (js/document.getElementById "app"))
