
(ns celine.color)

(defprotocol Color
  (pack-as-sRGB [color])
  (pack-as-RGB [color])
  (change-brightness [color factor])
)

(deftype r-color [r g b]
  Color
  (pack-as-sRGB [r-c] nil)
  (pack-as-RGB [r-c] nil)
  (change-brightness [r-c k] nil)

  Object
  (toString [r-c] (str "real-valued rgb color: <" r "," g "," b ">"))
)
