(ns celine.core
  (:require [celine.geometry :as geometry])
  (:require [celine.color :as color])
  (:require [celine.bitmap :as bitmap])
  (:require [celine.util :as util])
  (:gen-class :main :true)
)


(def some-color (color/make-r-color 0.2 0.3 0.5))

(def width 720)
(def height 405)

(defn nxt-px [px]
  (let [condition (< (.x px) (dec width))
        new-x (if condition (inc (.x px)) 0)
        new-y (if condition (.y px) (inc (.y px)))
       ]
    (bitmap/make-pixel new-x new-y (.color px))
  )
)

(defn end-px [px]
  (and (>= (.x px) (dec width)) (>= (.y px) (dec height)))
)

(def px-list (util/unfold (bitmap/make-pixel 0 0 some-color) nxt-px end-px))


(defn -main [& args] (bitmap/draw-png width height px-list "img.png"))

