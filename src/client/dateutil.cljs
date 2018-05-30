(ns client.dateutil)

(defn parse-ts [ts]
  (js/Date.parse ts))
