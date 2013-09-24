
(ns celine.shader
  (:require [celine.bitmap :as bitmap])
  (:require [celine.color :as color])
  (:require [celine.geometry :as geom])
  (:require [celine.scenery :as scene])
  (:require [celine.util :as util])
)

(defn shade [ray scene]
  (if-let [intersection (intersect scene ray)]
    (.base-color intersection)
    (color/black)
  )
)

(defn render-scene [scene])

