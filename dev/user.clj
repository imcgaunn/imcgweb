(ns user
  (:require
   [figwheel-sidecar.repl-api :as f]))

(defn fig-start
  "This starts the figwheel server and watch based auto-compiler."
  []
  (f/start-figwheel!))

(defn fig-stop
  "Stop the figwheel server and watch based auto-compiler."
  []
  (f/stop-figwheel!))

(defn cljs-repl
  "Launch a ClojureScript REPL that is connected to your build and host environment."
  []
  (f/cljs-repl))

(def test-blog-post-1 {:title "nicetitle", :date "Mon Jul 09 2018", :content "this is a great post"})
(def test-blog-post-2 {:title "nice titler again",
                       :date "Mon Jul 09 2018",
                       :content "this is a great post and I think it's even nicer than some other posts that I have seen in the store\n"})
(def test-blog-post-3 {:title "nice titles are my middle name",
                       :date "Mon Jul 09 2018",
                       :content "this is a great post and I think it's even nicer than some other posts that I have seen in the store\n"})
(def all-test-posts [test-blog-post-1
                     test-blog-post-2
                     test-blog-post-3])