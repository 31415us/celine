
(ns celine.scenery
  (:require [celine.color :as color])
  (:require [celine.geometry :as geom])
)

(defprotocol Light
  (light-dir [light point])
)

(deftype Scene-Element [geom-obj base-color])

(deftype Scene [elements lights])

(deftype Positional-Light [pos]
  Light
  (light-dir [light point] (geom/normalize (geom/sub pos point)))
)

(deftype Directional-Light [dir]
  Light
  (light-dir [light point] dir)
)

