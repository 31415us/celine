
(ns celine.shader
  (:require [celine.bitmap :as bitmap])
  (:require [celine.color :as color])
  (:require [celine.geometry :as geom])
  (:require [celine.scenery :as scenery])
  (:require [celine.util :as util])
)

(defn- cel-shading-modificator [cos-angle]
  (cond
    (> cos-angle 0.95) 1.1
    (> cos-angle 0.0) 1.0
    :else 0.5
  )
)

(defn shade [ray scene]
  (if-let [intersection (geom/intersect scene ray)]
    (let [modificator (apply * 
                             (map 
                               #(cel-shading-modificator
                                 (geom/dot 
                                  (geom/normalize (.normal (.vertex intersection)))
                                  (geom/normalize (scenery/light-dir % (.point (.vertex intersection))))
                                 ) 
                               )
                               (.lights scene)
                              )
                      )
         ]
      (color/change-brightness (.base-color intersection) modificator)
    )
    color/black
  )
)

(defn- next-px-in-row [px scene]
  (let [new-x (inc (.x px))
        new-y (.y px)
        ray (geom/make-ray 
              (.pos (.cam scene))
              (geom/sub 
                (scenery/pixel-position (.screen scene) new-x new-y)
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
                      (scenery/pixel-position (.screen scene) first-x first-y)
                      (.pos (.cam scene))
                    )
                  )
        first-px (bitmap/make-pixel first-x first-y (shade first-ray scene))
       ]
    (util/unfold first-px #(next-px-in-row % scene) #(is-last-px-in-row % (.px-width (.screen scene))))
  )
)

(defn render-scene [scene image-name]
  (let [px-list (flatten 
                  (concat 
                    (map 
                      #(shade-row % scene) 
                      (range 0 (.px-height (.screen scene)))
                    )
                  )
                )
       ]
    (bitmap/draw-png (.px-width (.screen scene)) (.px-height (.screen scene)) px-list image-name)
  )
)

