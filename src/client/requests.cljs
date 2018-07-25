(ns client.requests
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(defn http-get [u]
  (http/get u {:with-credentials? false}))
