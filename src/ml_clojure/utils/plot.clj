(ns ml-clojure.utils.plot
  (:require 
    [plotly-pyclj.core :as pc]))
 

(defn init! []
  (pc/start!))

(defn plot! [config]
  (pc/plot config))


(defn- draw-item [size loc]
  (let [row (int (/ loc size))
        col (mod loc size)]
    (pc/plot {:data   [{:x [] :y []}]
              :type   :scatter
              :layout {:xaxis  {:range [0 size]}
                       :yaxis  {:range [size 0]}
                       :width  500
                       :height 500
                       :shapes [{:type      "rect"
                                 :x0        col        
                                 :y0        row
                                 :x1        (+ col 1)
                                 :y1        (+ row 1)
                                 :fillcolor "rgba(55, 128, 191, 0.6)"}]}})))
(comment
  (doseq [i (range 25)]
    (Thread/sleep 100)
    (draw-item 5 i)))
  