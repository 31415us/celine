(ns celine.core
  (:require [celine.geometry :as geometry])
  (:require [celine.color :as color])
  (:import (celine.geometry Vector3D))
  (:gen-class :main :true)
)


(def origin (Vector3D. 0.0 0.0 0.0))
(def some-color (color/make-r-color 0.2 0.3 0.5))

(defn -main [& args] (println origin some-color))

