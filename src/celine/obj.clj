
(ns celine.obj
  (:require [clojure.string :as string])
  (:require [celine.geometry :as geom])
  (:require [celine.util :as util])
)

(def parser)

(defn parse-obj [path]
  "parses obj-file from given path and 
  returns the triangle-mesh described therein"
  (let [lines (util/lazy-file-lines path)]
    (parser lines [] [])
  )
)

(defn- parse-vector-line [ln]
  (let [x (. Double parseDouble (nth ln 0))
        y (. Double parseDouble (nth ln 1))
        z (. Double parseDouble (nth ln 2))
       ]
    (geom/make-vector x y z)
  )
)

(defn- parse-triangle-line [ln points]
  (let [a (nth points (dec (. Integer parseInt (nth ln 0))))
        b (nth points (dec (. Integer parseInt (nth ln 1))))
        c (nth points (dec (. Integer parseInt (nth ln 2))))
       ]
    (geom/make-triangle a b c)
  )
)

(defn- parser [lines vec-acc triangle-acc]
  "quick and dirty implementation for parsing .obj files
  only considers lines describing a vertex or a face
  faces are considered to be triangles"
  (if-let [line (first lines)]
    (let [ln (string/split line #"\s")
          l (filter #(not (= "" %)) ln)
         ]
      (cond 
        (= "v" (first l)) (parser (rest lines) (conj vec-acc (parse-vector-line (rest l))) triangle-acc)
        (= "f" (first l)) (parser (rest lines) vec-acc (conj triangle-acc (parse-triangle-line (rest l) vec-acc)))
        :else (parser (rest lines) vec-acc triangle-acc)
      )
    )
    (geom/make-triangle-mesh triangle-acc)
  )
)

