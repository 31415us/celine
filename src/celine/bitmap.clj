
(ns celine.bitmap
  (import java.io.File)
  (import java.awt.image.BufferedImage)
  (import javax.imageio.ImageIO)
  (:require [celine.color :as color])
)

(deftype Pixel [x y color])

(defn make-pixel [x y color]
  "proxy function for pixel creation
   so we dont have to import the pixel type
   in the other modules"
  (Pixel. x y color)
)

(defn draw-png [width height pix-list img-name]
  "creates a png image from the given sequence of pixels"
  {:pre [(= (count pix-list) (* width height))]}
  (let [img (BufferedImage. width height BufferedImage/TYPE_INT_RGB)]
    (doseq [px pix-list]
      (.setRGB img (.x px) (.y px) (color/pack-as-sRGB (.color px)))
    )
    (ImageIO/write img "png" (File. img-name))
  )
)

