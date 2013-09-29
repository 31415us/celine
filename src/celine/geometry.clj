
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

(defn triple [a b c]
  (dot a (cross b c))
)

(defn add [u v]
  "add vector u and v -> u + v"
  (Vector3D. (+ (.x u) (.x v)) (+ (.y u) (.y v)) (+ (.z u) (.z v)))
)

(defn sub [u v]
  "subtract v from u -> u - v"
  (Vector3D. (- (.x u) (.x v)) (- (.y u) (.y v)) (- (.z u) (.z v)))
)

(defn neg [u]
  (Vector3D. (- (.x u)) (- (.y u)) (- (.z u)))
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

(defn- ray-plane-intersection-point [pln ray]
  (let [dot-point-normal (dot (.point pln) (.normal pln))
        dot-origin-normal (dot (.origin ray) (.normal pln))
        dot-dir-normal (dot (.dir ray) (.normal pln))
       ]
    (if (= dot-dir-normal 0.0)
      nil
      (let [t (/ (- dot-point-normal dot-origin-normal) dot-dir-normal)]
        (if (>= t 0.0)
          (add (.origin ray) (scalar-mult (.dir ray) t))
          nil
        )
      )
    )
  )
)

(deftype Plane [point normal]
  GeomObject
  (intersect [pln ray]
    (if-let [p (ray-plane-intersection-point pln ray)]
      (Vertex. p normal)
      nil
    )
  )
  (displace [pln v] (Plane. (add point v) normal))
)

(defn make-plane [point normal]
  (Plane. point (normalize normal))
)

(defn- ray-triangle-intersection-point [tr ray]
  "implementation of the möller-trumbore algorithm as described in:
  http://www.scratchapixel.com/lessons/3d-basic-lessons/lesson-9-ray-triangle-intersection/m-ller-trumbore-algorithm/"
  (let [-D (neg (.dir ray))
        T (sub (.origin ray) (.a tr))
        E1 (sub (.b tr) (.a tr))
        E2 (sub (.c tr) (.a tr))
        det (triple -D E1 E2)
       ]
    (if (= 0.0 det)
      nil
      (let [v (/ (triple -D E1 T) det)]
        (if (or (> v 1.0) (< v 0.0))
          nil
          (let [u (/ (triple -D T E2) det)]
            (if (or (> u 1.0) (< v 0.0))
              nil
              (let [t (/ (triple T E1 E2) det)]
                (add (.origin ray) (scalar-mult (.dir ray) t))
              )
            )
          )
        )
      )
    )
  )
)

(deftype Triangle [a b c]
  GeomObject
  (intersect [tr ray]
    (if-let [p (ray-triangle-intersection-point tr ray)]
      (Vertex. p (cross (sub b a) (sub c a)))
      nil
    )
  )
  (displace [tr v] (Triangle. (add a v) (add b v) (add c v)))
)


