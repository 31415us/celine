
(ns celine.scenery
  (:require [celine.color :as color])
  (:require [celine.geometry :as geom])
)

(defprotocol Light
  (light-dir [light point])
)

(deftype Colored-Vertex [vertex base-color])

(deftype Scene-Element [geom-obj base-color]
  geom/GeomObject
  (intersect [elem ray]
    (if-let [p (geom/intersect geom-obj ray)]
      (Colored-Vertex. p base-color)
      nil
    )
  )
  (displace [elem v]
    (Scene-Element. (geom/displace geom-obj v) base-color)
  )
)

(defn make-scene-element [obj color]
  (Scene-Element. obj color)
)

(deftype Camera [pos forward right down])

(defn make-cam [pos fwd rgt dwn]
  (Camera. pos (geom/normalize fwd) (geom/normalize rgt) (geom/normalize dwn))
)

(deftype Screen [top-left-corner right down px-width px-height])

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
       ]
    (Screen. top-left
             (geom/scalar-mult right (/ width px-width)) 
             (geom/scalar-mult down (/ height px-height)) 
             px-width 
             px-height
    )
  )
)

(defn- comp-nearer-to-ray-origin [ray c-vert1 c-vert2]
  (let [d1 (geom/dist (.origin ray) (.point (.vertex c-vert1)))
        d2 (geom/dist (.origin ray) (.point (.vertex c-vert2)))
       ]
    (< d1 d2)
  )
)

(deftype Scene [cam screen elements lights]
  geom/GeomObject
  (intersect [scene ray]
    (let [intersections (filter identity (map #(geom/intersect % ray) (.elements scene)))
          nb-intersections (count intersections)
         ]
      (case nb-intersections
        0 nil
        1 (first intersections)
        (first (sort (comp #(comp-nearer-to-ray-origin ray %1 %2)) intersections))
      )
    )
  )
  (displace [scene v]
    (let [-v (geom/sub geom/origin v)
          new-cam (Camera. (geom/add -v (.pos cam)) (.forward cam) (.right cam) (.down cam))
          new-screen (Screen. (geom/add -v (.top-left-corner screen))
                              (.right screen) (.down screen)
                              (.px-width screen) 
                              (.px-height screen)
                     )
         ]
      (Scene. new-cam new-screen elements lights)
    )
  )
)

(defn make-scene [cam screen elements lights]
  (Scene. cam screen elements lights)
)

(defn pixel-position [screen i j]
  "return the position of pixel (i,j) on this screen"
  (geom/add (geom/add 
              (.top-left-corner screen) 
              (geom/scalar-mult (.right screen) i)) 
            (geom/scalar-mult (.down screen) j)
  )
)

(deftype Positional-Light [pos]
  Light
  (light-dir [light point] (geom/normalize (geom/sub pos point)))
)

(defn make-positional-light-source [position]
  (Positional-Light. position)
)

(deftype Directional-Light [dir]
  Light
  (light-dir [light point] dir)
)

(defn make-directional-light-source [dir]
  (Directional-Light. (geom/normalize dir))
)

