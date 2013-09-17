(ns celine.core
  (:require [celine.geometry :as geometry])
  (:import (celine.geometry Vector3D))
  (:gen-class :main :true)
)


(def origin (Vector3D. 0.0 0.0 0.0))

(defn -main [& args] (println origin))

