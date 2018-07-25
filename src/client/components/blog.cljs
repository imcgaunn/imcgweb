(ns client.components.blog
  (:require [client.requests :as req]
            [cljs.core.async :refer [<!]]
            [cljs.core.async :refer-macros [go]]))

(def backend-uri "https://6fjd9so41d.execute-api.us-east-1.amazonaws.com/dev")

(defn transform-index
  "builds a map that looks kind of like this:
  {1 {:title 't' :created-time #<time> ... }
   2 {:title 't' :created-tme #<time> ... }
  "
  [raw-idx]
  (let [almost-entries (map
                         (fn [e]
                           {:id (:id e)
                            :vals {:title (:id e)  ; TODO: this should be title, but that's not in the backend index
                                   :created-time (:created-time e)
                                   :post-s3-loc (:post-s3-loc e)
                                   :post-meta-s3-loc (:post-meta-s3-loc e)}}) raw-idx)]
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
          post-idx-entry (get idx (js/parseInt post-id 10))]  ;; get post metadata from our index
      (if post-idx-entry
        {:content (:body post-content)
         :date (:created-time post-idx-entry)
         :title (:id post-idx-entry)
         :id post-id}
        (throw (str "no index entry for post: " post-id))))))

(defn set-current-post! [app post]
  (swap! app
         assoc :current-post
         post))

(defn title->link [title]
  title) ;; TODO for sure this will fail at times.

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
               ;; NOTE: the title will not always be linkable
               :href  (str "#/blog/post/" post-id)} (:title entry)]
          [:p {:class "blogIndexEntryDate"}
           (.toDateString
             (:created-time entry))]]]))]])
