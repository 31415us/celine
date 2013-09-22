
(ns celine.shader
  (:require [celine.geometry :as geom])
  (:require [celine.color :as color])
)

(defprotocol Light
  (light-dir [light point])
)

(deftype Scene-Element [geom-obj base-color])

(deftype Positional-Light [pos]
  Light
  (light-dir [light point] (geom/normalize (geom/sub pos point)))
)

(deftype Directional-Light [dir]
  Light
  (light-dir [light point] dir)
)

