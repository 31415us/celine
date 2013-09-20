
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

