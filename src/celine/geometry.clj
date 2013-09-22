
(ns celine.geometry)


(deftype Vector3D [x y z]
  Object
  (toString [v] (str "(" x "," y "," z ")"))
)

(defn create-vector [x y z]
  "proxy function to create vectors
  so we dont have to import the vector type"
  (Vector3D. x y z)
)

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

