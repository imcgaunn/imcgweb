(ns client.components.common)

(defn header [title]
  [:header {:class "mainHeader"}
   [:h2 title]])

(defn nav-footer [footerpages]
  [:footer {:id "navigation"}
   (for [p footerpages]
     ^{:key p}
     [:li
      [:a {:href (str "#/" p)} p]])])
