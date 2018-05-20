(ns server.main
  (:require
   [cljs.nodejs :as nodejs]))

(nodejs/enable-util-print!)

(defonce express (nodejs/require "express"))
(defonce serve-static (nodejs/require "serve-static"))
(defonce http (nodejs/require "http"))

;; app gets redefined on reload
(def app (express))

;; routes get redefined on each reload
(. app (get "/hello" 
        (fn [req res] (. res (send "Hello world")))))

(. app (use (serve-static "resources/public" #js {:index "index.html"})))

(def main!
  (fn []
    (doto (.createServer http #(app %1 %2))
      (.listen 3000))
    (println "server started on port 3000")))
