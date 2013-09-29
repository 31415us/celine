(ns celine.core
  (:require [celine.geometry :as geom])
  (:require [celine.color :as color])
  (:require [celine.bitmap :as bitmap])
  (:require [celine.scenery :as scenery])
  (:require [celine.shader :as shader])
  (:require [celine.util :as util])
  (:gen-class :main :true)
)


(def some-color (color/make-r-color 0.2 0.3 0.5))

(def width 720)
(def height 405)

(def cam (scenery/make-cam (geom/make-vector -16.0 0.0 0.0) geom/e1 geom/-e2 geom/-e3))
(def screen (scenery/make-screen cam 16.0 16.0 9.0 width height))

(def sph1 (geom/make-sphere (geom/make-vector 5.0 0.0 0.0) 1.0))
(def elem1 (scenery/make-scene-element sph1 some-color))
(def sph2 (geom/make-sphere (geom/make-vector 5.0 2.0 2.0) 1.0))
(def elem2 (scenery/make-scene-element sph2 color/red))

(def scene (scenery/make-scene cam screen (list elem1 elem2) (list)))

(defn -main [& args] (shader/render-scene scene))

