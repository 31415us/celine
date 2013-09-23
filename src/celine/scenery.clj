
(ns celine.scenery
  (:require [celine.color :as color])
  (:require [celine.geometry :as geom])
)

(defprotocol Light
  (light-dir [light point])
)

(deftype Scene-Element [geom-obj base-color])

(defn make-scene-element [obj color]
  (Scene-Element. obj color)
)

(deftype Scene [cam screen elements lights])

(deftype Camera [pos forward right down])

(defn make-cam [pos fwd rgt dwn]
  (Camera. pos (geom/normalize fwd) (geom/normalize rgt) (geom/normalize dwn))
)

(deftype Screen [top-left-corner right down])

(defn make-screen [cam dist-to-cam width height px-width px-height]
  (let [fwd (.forward cam)
        right (.right cam)
        down (.down cam)
        left (geom/sub geom/origin right)
        up (geom/sub geom/origin down)
        screen-center (geom/add (.pos cam) (geom/scalar-mult fwd dist-to-cam))
        top-left (geom/add 
                   (geom/add screen-center (geom/scalar-mult up (/ height 2.0))) 
                   (geom/scalar-mult left (/ width 2.0))
                 )
       ])
  (Screen. top-left (geom/scalar-mult right (/ width px-width)) (geom/scalar-mult down (/ height px-height)))
)

(defn pixel-position [screen i j]
  "return the position of pixel (i,j) on this screen"
  (geom/add (geom/add (.top-left-corner screen) (geom/scalar-mult right i)) (geom/scalar-mult down j))
)

(deftype Positional-Light [pos]
  Light
  (light-dir [light point] (geom/normalize (geom/sub pos point)))
)

(deftype Directional-Light [dir]
  Light
  (light-dir [light point] dir)
)

(defn make-directional-light-source [dir]
  (Directional-Light. (geom/normalize dir))
)

