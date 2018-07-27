(ns client.components.blog
  (:require [client.requests :as req]
            [cljs.core.async :refer [<!]]
            [cljs.core.async :refer-macros [go]]))

(def backend-uri "https://6fjd9so41d.execute-api.us-east-1.amazonaws.com/dev")

(defn transform-index-entry [e]
  {:id (:id e)
   :vals e})

(defn transform-index
  [raw-idx]
  (let [almost-entries (map transform-index-entry raw-idx)]
      (reduce
         (fn [acc m]
           (assoc acc (:id m) (:vals m)))
         {}
         almost-entries)))

(defn fetch-post-idx! [app]
  (let [index-uri (str backend-uri "/blog/index")]
    (go
      (swap!
        app
        assoc
        :post-index (let [raw-idx (:body (<! (req/http-get index-uri)))]
                      (transform-index raw-idx))))))

(defn add-post! [app post]
  (swap!
    app
    assoc-in
    [:blog-posts (:id post)]
    post))

(defn fetch-blog-post! [app post-id]
  (go
    (let [post-backend-uri (str backend-uri "/blog/?id=" post-id)
          post-content (<! (req/http-get post-backend-uri))
          idx (:post-index @app)
          post-idx-entry (get idx (js/parseInt post-id 10))]
      (if post-idx-entry
        {:content (:body post-content)
         :date (:created-time post-idx-entry)
         :title (:title post-idx-entry)
         :id post-id}
        (throw (str "no index entry for post: " post-id))))))

(defn set-current-post! [app post]
  (swap! app
         assoc :current-post
         post))

(defn blog-post [post]
  [:div {:class "blogPost"}
   [:p (:content post)]
   [:p "posted at..."]
   (if (:date post)
     [:p (.toDateString (:date post))])
   [:a {:href "#/blog"} "<back to index>"]])

(defn blog-index-page [index]
  [:div {:class "blogIndex"}
   [:ul
    (for [e (into [] index)]
      (let [[post-id entry] e]
        ^{:key post-id}
        [:li {:class "blogIndexEntry"}
         [:div
          [:a {:class "blogIndexEntryTitle"
               :href  (str "#/blog/post/" post-id)} (:title entry)]
          [:p {:class "blogIndexEntryDate"}
           (.toDateString
            (:created-time entry))]]]))]])
