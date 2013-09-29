
(ns celine.geometry
  (:require [celine.util :as util])
)

(defprotocol GeomObject
  (intersect [obj ray])
  (displace [obj v])
)

(deftype Vector3D [x y z]
  Object
  (toString [v] (str "(" x "," y "," z ")"))
)

(deftype Vertex [point normal]
  Object
  (toString [vtx] 
    (str "Vertex at: " point " with normal vector: " normal)
  )
)

(defn make-vector [x y z]
  "proxy function to create vectors
  so we dont have to import the vector type"
  (Vector3D. x y z)
)

(deftype Ray [origin dir]
  Object
  (toString [ray]
    (str "Ray with origin: " origin " in direction: " dir)
  )
)


(defn make-ray [o dir]
  "create a ray"
  (Ray. o dir)
)

(def e1 (Vector3D. 1.0 0.0 0.0))
(def e2 (Vector3D. 0.0 1.0 0.0))
(def e3 (Vector3D. 0.0 0.0 1.0))
(def -e1 (Vector3D. -1.0 0.0 0.0))
(def -e2 (Vector3D. 0.0 -1.0 0.0))
(def -e3 (Vector3D. 0.0 0.0 -1.0))
(def origin (Vector3D. 0.0 0.0 0.0))

(defn dot [u v]
  "compute the dot product of two vectors"
  (+ (* (.x u) (.x v)) (* (.y u) (.y v)) (* (.z u) (.z v)))
)

(defn length [v]
  "compute the length of given vector"
  (Math/sqrt (dot v v))
)

(defn normalize [v]
  "compute unit vector with same direction as input vector"
  (let [l (length v)]
    (Vector3D. (/ (.x v) l) (/ (.y v) l) (/ (.z v) l))
  )
)

(defn cross [u v]
  "computes the cross product u x v"
  (let [cx (- (* (.y u) (.z v)) (* (.z u) (.y v)))
        cy (- (* (.z u) (.x v)) (* (.x u) (.z v)))
        cz (- (* (.x u) (.y v)) (* (.y u) (.x v)))
       ]
    (Vector3D. cx cy cz)
  )
)

(defn add [u v]
  "add vector u and v -> u + v"
  (Vector3D. (+ (.x u) (.x v)) (+ (.y u) (.y v)) (+ (.z u) (.z v)))
)

(defn sub [u v]
  "subtract v from u -> u - v"
  (Vector3D. (- (.x u) (.x v)) (- (.y u) (.y v)) (- (.z u) (.z v)))
)

(defn dist [u v]
  "distance between point defined by u and v"
  (length (sub u v))
)

(defn scalar-mult [v k]
  "multiply vector v by scalar value k"
  (Vector3D. (* k (.x v)) (* k (.y v)) (* k (.z v)))
)

(def ray-sphere-intersection-vertex)

(deftype Sphere [center radius]
  GeomObject
  (intersect [sph ray] (ray-sphere-intersection-vertex sph ray))
  (displace [sph v] (Sphere. (add center v) radius))

  Object
  (toString [sph] (str "sphere at: " center " with radius: " radius))
)

(defn make-sphere [center r]
  (Sphere. center r)
)

(defn- ray-sphere-intersection-point [sph ray]
  "returns the intersection point bnetween 
   ray and sphere or nil if they dont intersect"
  (let [d (.dir ray)
        OC (sub (.origin ray) (.center sph))
        r (.radius sph)
        a (dot d d)
        b (* 2.0 (dot OC d))
        c (- (dot OC OC) (* r r))]
    (if-let [sol (util/solve-quadratic a b c)]
      (let [fst (first sol)
            snd (second sol)
            t0 (min fst snd)
            t1 (max fst snd) 
           ]
        (cond
          (> 0.0 t1) nil
          (> 0.0 t0) (add (.origin ray) (scalar-mult d t1))
          :else (add (.origin ray) (scalar-mult d t0))
        )
      )
      nil
    )
  )
)

(defn- ray-sphere-intersection-vertex [sph ray]
  "returns the vertex of intersection between 
   ray and sphere or nil if they dont intersect"
  (if-let [inter (ray-sphere-intersection-point sph ray)]
    (let [norm (normalize (sub inter (.center sph)))] 
      (Vertex. inter norm)
    )
    nil
  )
)


