(ns client.components.interests
  (:require [client.components.common :as common]))

(def jensen-kimmit-duns-brocoli "https://www.youtube.com/embed/9HTZBMpgGq4")

(defn youtube-frame [url width height]
  [:iframe {:width width
            :height height
            :src url
            :frameborder "0"
            :allow "encrypted-media"} "loading..."])

(defn professional-yoyo []
  [:div
   [:h3 "best yoyo video"]
   [youtube-frame
    jensen-kimmit-duns-brocoli "600" "500"]])
