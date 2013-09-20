
(ns celine.util)

(defn unfold [seed step end-predicate]
  "creates a sequence of values from a seed value 
  by repeatedly applying the step function to it
  until it satisfies the end-predicate"
  (if (end-predicate seed)
    (list seed)
    (cons seed (unfold (step seed) step end-predicate))
  )
)

