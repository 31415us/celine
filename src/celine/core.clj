(ns celine.core
  (:require [celine.geometry :as geometry])
  (:require [celine.color :as color])
  (:require [celine.bitmap :as bitmap])
  (:import (celine.geometry Vector3D))
  (:gen-class :main :true)
)


(def origin (Vector3D. 0.0 0.0 0.0))
(def some-color (color/make-r-color 0.2 0.3 0.5))

(def px-list (list (bitmap/create-pixel 0 0 some-color) (bitmap/create-pixel 1 0 some-color) (bitmap/create-pixel 0 1 some-color) (bitmap/create-pixel 1 1 some-color)))

(defn -main [& args] (bitmap/draw-png 2 2 px-list "img.png"))

