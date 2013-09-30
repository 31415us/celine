
(ns celine.util)

(defn unfold [_seed _step _end-predicate]
  "creates a sequence of values from a seed value 
  by repeatedly applying the step function to it
  until it satisfies the end-predicate"
  (loop [seed _seed
         step _step
         end _end-predicate
         acc ()
        ]
    (if (end seed)
      (cons seed acc)
      (recur (step seed) step end (cons seed acc))
    )
  )
)

(defn- get-q [b discr]
  "helper function for solve-quadratic
   for numerical stability as proposed in:
   http://wiki.cgsociety.org/index.php/Ray_Sphere_Intersection"
  (let [root (Math/sqrt discr)]
    (if (> 0.0 b)
      (/ (- root b) 2.0)
      (/ (- (- root) b) 2.0)
    )
  )
)

(defn solve-quadratic [a b c]
  "solve quadratic equation of the form:
   a*x^2 + b*x + c = 0"
  (let [discr (- (* b b) (* 4.0 a c))]
    (if (> 0.0 discr)
      nil
      (let [q (get-q b discr)] 
        [(/ q a) (/ c q)]
      )
    )
  )
)

(defn lazy-file-lines [path]
  "transforms a file into a lazy
  sequence of lines as seen here:
  http://stackoverflow.com/questions/4118123/read-a-very-large-text-file-into-a-list-in-clojure"
  (letfn [(helper [rdr]
            (lazy-seq
              (if-let [line (.readLine rdr)]
                (cons line (helper rdr))
                (do (.close rdr) nil)
              )
            )
          )]
    (helper  (clojure.java.io/reader path))
  )
)

