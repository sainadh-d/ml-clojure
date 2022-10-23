(ns ml-clojure.qlearning.qlearning)
  
    
(def q-params 
  {:alpha 0.1
   :gamma 0.99})

(defn qmatrix [n-states n-actions]
  (make-array Float/TYPE n-states n-actions))

(defn update-qmatrix [^"[[F" q st at rt st-1]
  (let [current   (aget q st at)
        alpha     (:alpha q-params)
        gamma     (:gamma q-params)
        max-st-1  (apply max (aget q st-1))
        new-val   (+ current (* alpha (+ rt (* -1 current) (* gamma max-st-1))))]
    (aset-float q st at new-val)))

  
   
  