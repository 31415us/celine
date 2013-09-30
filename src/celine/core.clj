(ns celine.core
  (:require [celine.geometry :as geom])
  (:require [celine.color :as color])
  (:require [celine.bitmap :as bitmap])
  (:require [celine.scenery :as scenery])
  (:require [celine.shader :as shader])
  (:require [celine.obj :as obj])
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
(def pln1 (geom/make-plane (geom/make-vector 0.0 0.0 -4.5) geom/e3))
(def elem3 (scenery/make-scene-element pln1 color/green))
(def pln2 (geom/make-plane (geom/make-vector 10.0 0.0 0.0) geom/-e1))
(def elem4 (scenery/make-scene-element pln2 color/blue))

(def tr1 (geom/make-triangle (geom/make-vector 5.0 -1.0 -1.0) (geom/make-vector 5.0 1.0 -1.0) (geom/make-vector 5.0 -1.0 1.0)))
(def elem5 (scenery/make-scene-element tr1 (color/make-r-color 0.1 0.4 0.2)))

(def icosahedron (scenery/make-scene-element 
                   (geom/displace (obj/parse-obj "./obj/icosahedron.obj") (geom/make-vector 1.0 0.0 0.0)) 
                   some-color
                 )
)

(def teapot (scenery/make-scene-element
              (geom/displace (obj/parse-obj "./obj/teapot.obj") (geom/make-vector 5.0 0.0 0.0))
              (color/make-r-color 0.5 0.2 0.1)
            )
)

(def light1 (scenery/make-positional-light-source (geom/make-vector 0.0 5.0 10.0)))
(def light2 (scenery/make-positional-light-source (geom/make-vector 5.0 -5.0 10.0)))

(def scene (scenery/make-scene cam screen (list teapot) (list light1)))

(defn -main [& args] (shader/render-scene scene "img.png"))


