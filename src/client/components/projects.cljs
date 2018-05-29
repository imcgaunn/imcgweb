(ns client.components.projects
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [client.dateutil :as dateutil]))

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

(defn projects-showcase [plist]
  [:ul
   (let [sorted-projects
         (take 5
               (reverse
                (sort-by #(dateutil/parse-ts (:last-updated %)) plist)))]
     (for [p sorted-projects]
       ^{:key (:url p)}
       [:li
        [:div
         [:p {:class "projectName"} (:name p)]
         [:p {:class "projectDescription"} (:description p)]
         [:p {:class "projectLastUpdated"} (:last-updated p)]
         [:a {:href (:url p)} (:url p)]]]))])
