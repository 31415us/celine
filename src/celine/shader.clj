
(ns celine.shader
  (:require [celine.bitmap :as bitmap])
  (:require [celine.color :as color])
  (:require [celine.geometry :as geom])
  (:require [celine.scenery :as scene])
  (:require [celine.util :as util])
)

(defn shade [ray scene]
  (if-let [intersection (geom/intersect scene ray)]
    (.base-color intersection)
    color/black
  )
)

(defn- next-px-in-row [px scene]
  (let [new-x (inc (.x px))
        new-y (.y px)
        ray (geom/make-ray 
              (.pos (.cam scene))
              (geom/sub 
                (scene/pixel-position (.screen scene) new-x new-y)
                (.pos (.cam scene))
              )
            )
       ]
    (bitmap/make-pixel new-x new-y (shade ray scene))
  )
)

(defn- is-last-px-in-row [px px-width-of-row]
  (<= (dec px-width-of-row) (.x px))
)

(defn- shade-row [row scene]
  (let [first-x 0
        first-y row
        first-ray (geom/make-ray
                    (.pos (.cam scene))
                    (geom/sub
                      (scene/pixel-position (.screen scene) first-x first-y)
                      (.pos (.cam scene))
                    )
                  )
        first-px (bitmap/make-pixel first-x first-y (shade first-ray scene))
       ]
    (util/unfold first-px #(next-px-in-row % scene) #(is-last-px-in-row % (.px-width (.screen scene))))
  )
)

(defn render-scene [scene]
  (let [px-list (flatten 
                  (concat 
                    (map 
                      #(shade-row % scene) 
                      (range 0 (.px-height (.screen scene)))
                    )
                  )
                )
       ]
    (bitmap/draw-png (.px-width (.screen scene)) (.px-height (.screen scene)) px-list "img.png")
  )
)

