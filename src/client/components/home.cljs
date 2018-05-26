(ns client.components.home)

(defn my-philosophy []
  [:div {:style {:text-align "center"
                 :font-family ""}}             
   [:p "there is no simpler or stronger expression of my personal philosophy than what the vanilla bard once whispered:"]
   [:blockquote "\"anything less than your best is a felony\" - vanilla ice"]])

(defn interests-list []
  [:ul
   [:li "modern yoyoing"]
   [:li "go/python/kotlin/(clojure|java)script programming"]
   [:li "cryptography"]
   [:li "others?"]])

(defn main []
  [:div {:style {:padding-top "0.2rem"
                 :padding-left "0.3rem"}}
   [my-philosophy]
   [:hr]
   [:p "if you are interested in any of the following, you may like my website:"]
   [interests-list]
   [:p "you can also visit any of the links below to look at my software projects, my blog or some more
    of my personal interests."]])
