
(ns celine.color)

(defprotocol Color
  "protocol specifying the basic operations a color should support"
  (pack-as-sRGB [color])
  (pack-as-RGB [color])
  (change-brightness [color factor])
)

(def pack-r-color-with-function)
(def gamma-correct)
(def redistribute-rgb)

(deftype r-color [r g b]
  Color
  (pack-as-sRGB [r-c] (pack-r-color-with-function r-c gamma-correct))
  (pack-as-RGB [r-c] (pack-r-color-with-function r-c identity))
  (change-brightness [r-c k] (redistribute-rgb (* r k) (* g k) (* b k)))

  Object
  (toString [r-c] (str "real-valued rgb color: <" r "," g "," b ">"))
)

(defn make-r-color [r g b]
  "proxy constructor for r-color checking input parameters"
  {:pre [(<= r 1.0) (>= r 0.0)
         (<= g 1.0) (>= g 0.0)
         (<= b 1.0) (>= b 0.0)
        ]
  }
  (r-color. r g b)
)

(defn- scale-to-max [value max-value]
  "scales real value [0,1] up to integer [0,max-value]"
  {:pre [(<= value 1.0) (>= value 0.0)]}
   (int (Math/floor (* value max-value)))
)

(defn- pack-r-color-with-function [r-c func]
  "packs a r-color struct to 32bit rgb color applying func to each channel; no alpha channel"
  (let [new-r (func (.r r-c))
        new-g (func (.g r-c))
        new-b (func (.b r-c))
        scaled-r (scale-to-max new-r 255)
        scaled-g (scale-to-max new-g 255)
        scaled-b (scale-to-max new-b 255)
       ]
    (bit-or (bit-shift-left scaled-r 16) (bit-shift-left scaled-g 8) scaled-b)
  )
)

(defn- gamma-correct [channel]
  "applies srgb gamma correction to single color channel as seen here:
  https://en.wikipedia.org/wiki/Srgb"
  {:pre [(<= channel 1.0) (>= channel 0.0)]}
  (if (< channel 0.0031308)
    (* 12.92 channel)
    (- (* 1.055 (Math/pow channel (/ 1.0 2.4))) 0.055)
  )
)

(def white)
(defn- redistribute-rgb [r g b]
  "tries to evenly redistribute overflow of a color channel:
  http://stackoverflow.com/questions/141855/programmatically-lighten-a-color"
  (let [sum (+ r g b)
        m (max r g b)
        overflow (/ (- 3.0 sum) (- (* 3.0 m) sum))
        gray-value (- 1.0 (* m overflow))
       ]
    (cond
      (< m 1.0) (r-color. r g b)
      (>= sum 3.0) white
      :else (r-color.
              (+ gray-value (* r overflow))
              (+ gray-value (* g overflow))
              (+ gray-value (* b overflow))
            )
    )
  )
)

(def red (r-color. 1.0 0.0 0.0))
(def green (r-color. 0.0 1.0 0.0))
(def blue (r-color. 0.0 0.0 1.0))
(def white (r-color. 1.0 1.0 1.0))
(def black (r-color. 0.0 0.0 0.0))

