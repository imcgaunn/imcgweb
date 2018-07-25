(defproject imcgweb "0.1.0-SNAPSHOT"
  :description "clojurescript code for my personal website"
  :url "http://github.com/imcgaunn/imcgweb"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.7.1"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.238"]
                 [org.clojure/core.async  "0.4.474"]
                 [org.clojure/tools.reader "1.3.0"]
                 [reagent "0.8.1"]
                 [secretary "1.2.3"]
                 [cljs-http "0.1.45"]]
  :source-paths ["src"]
  :aliases {"fig" ["trampoline" "run" "-m" "figwheel.main"]
            "fig:build" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
            "fig:min"   ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "dev"]}

  :profiles {:dev {:dependencies [[com.bhauman/figwheel-main "0.1.4"]
                                  [com.bhauman/rebel-readline-cljs "0.1.4"]]
                   :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl] :port 4001 }
                   :resource-paths ["resources" "target"]
                   ;; need to add the compliled assets to the :clean-targets
                   :clean-targets ^{:protect false} ["target/public"
                                                     :target-path]}})
