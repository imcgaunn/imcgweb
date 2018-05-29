(ns client.components.projects
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(def github-api-url "https://api.github.com")

(defn fetch-github-projects [user]
  (go
    (let [resp (<! (http/get
                    (str github-api-url "/users/" user "/repos")
                    {:with-credentials? false}))]
      (map (fn [repo]
             {:url (:html_url repo)
              :description (:description repo)
              :name (:name repo)
              :last-updated (:pushed_at repo)})
           (:body resp)))))
