(ns client.components.blog)

(defn add-post! [app post]
  (let [cur-posts (:blog-posts @app)]
   (swap! 
    app assoc 
    :blog-posts
    (conj cur-posts post))))

(defn find-post [title posts]
  (first (filter #(= title (:title %)) posts)))

(defn set-current-post! [app title]
  (let [posts (:blog-posts @app)]
   (swap! app
          assoc :current-post
          (find-post title posts))))

(defn post-from-components [title date content]
 {:title title
  :date date
  :content content})

(defn title->link [title]
  title) ;; TODO for sure this will fail at times.

(defn blog-post [post]
 [:div {:class "blogPost"}
  [:p (:content post)]
  [:p "posted at..."]
  [:p (:date post)]
  [:a {:href "#/blog"} "<back to index>"]])

(defn blog-index-page [blog-posts]
 [:div {:class "blogIndex"}
  [:ul
   (for [p blog-posts]
    ^{:key (:title p)}
    [:li {:class "blogIndexEntry"}
     [:div
      [:a {:class "blogIndexEntryTitle"
           :href (str "#/blog/post/"
                      (title->link (:title p)))} (:title p)]
      [:p {:class "blogIndexEntryDate"}
       (:date p)]]])]])
