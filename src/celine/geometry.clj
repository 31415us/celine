
(ns celine.geometry)


(deftype Vector3D [x y z]
  Object
  (toString [vec] (str "(" x "," y "," z ")"))
)
  

