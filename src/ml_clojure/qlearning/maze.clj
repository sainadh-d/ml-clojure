(ns ml-clojure.qlearning.maze
  (:require 
    [ml-clojure.utils.plot :as plot]
    [ml-clojure.qlearning.qlearning :as ql]
    [clojure.pprint :as pprint]))


(defn create-maze [size]
  (let [agent [(rand-int size) (rand-int size)]
        goal  [(rand-int size) (rand-int size)]]
    {:agent     agent
     :obstacles (into #{}
                  (for [x (range size)
                        y (range size)
                        :when (and (not= [x y] goal) (not= [x y] agent))
                        :when (< (rand) 0.33)]
                    [x y]))
     :size      size
     :goal      goal}))
   
(defn get-loc [size point]
  (let [[x y] point]
    (+ (* x size) y)))

(defn get-coords 
  "returns x0 y0 x1 y1 for the point"
  [size point]
  (let [loc (get-loc size point)
        row (int (/ loc size))
        col (mod loc size)]
    {:x0 (+ col 0.1)
     :y0 (+ row 0.1)
     :x1 (- (+ col 1) 0.1)
     :y1 (- (+ row 1) 0.1)}))

(defn get-plotly-shape [type size loc]
  (merge
    (get-coords size loc)
    {:type      "rect"
     :line      {:width 0}
     :fillcolor (case type
                  :agent    "rgba(55, 128, 191, 0.6)"
                  :obstacle "rgba(255, 105, 0, 0.6)"
                  :goal     "rgba(144, 238, 144, 0.6)")}))


(defn visualize-maze [maze]
  (let [{:keys [size agent obstacles goal]} maze
        agent-shape     (get-plotly-shape :agent size agent) 
        goal-shape      (get-plotly-shape :goal size goal)
        obstacle-shapes (for [obstacle obstacles]
                          (get-plotly-shape :obstacle size obstacle))
        shapes          (apply conj [agent-shape goal-shape] obstacle-shapes)]
    (plot/plot! {:data [{:x [] :y []}]
                 :type :scatter
                 :layout {:xaxis  {:range [0 (:size maze)]
                                   :tickmode "linear"
                                   :tick0    0
                                   :dtick    1}
                          :yaxis  {:range [(:size maze) 0]
                                   :tickmode "linear"
                                   :tick0    0
                                   :dtick    1}
                          :width  800
                          :height 800
                          :shapes shapes}})))

(defn won? [maze]
  (= (:agent maze) (:goal maze)))

(defn moves [loc]
  (let [[x y] loc]
    {:down  [(+ x 1) y]
     :up    [(- x 1) y]
     :right [x (+ y 1)]
     :left  [x (- y 1)]}))

; We get [maze [dir move]] as args. We are ignoring direction
(defn valid-move? [maze [_ move]]
  (let [[x y]     move
        size      (:size maze)
        obstacles (:obstacles maze)]
    (and
      (>= x 0)
      (< x size)
      (>= y 0)
      (< y size)
      (not (contains? obstacles move)))))

(defn get-best-move [agent-loc ^"[[F" q st]
  (let [all_moves        (moves agent-loc)
        ; best direction is the one which has max weightage
        best-dir-id      (first (apply max-key second (map-indexed vector (aget q st)))) 
        best-dir         (case (int best-dir-id)
                           0 :up
                           1 :down
                           2 :right
                           3 :left)]
    [best-dir (get all_moves best-dir)]))


(defn learn [start-maze]
  (let [size        (:size start-maze)
        q           (ql/qmatrix (* size size) 4)]
    (doseq [i    (range 500)
            :let [n-steps (atom 0)]]
      (loop [maze start-maze]
        (when (not (won? maze))
          (let [agent-loc        (:agent maze)
                st               (get-loc size agent-loc)
                [dir next-move]  (if (or (> (rand) 0.5) (< i 100))
                                   ; find next random valid move (explore)
                                   (->> 
                                     (moves agent-loc)              ; get all moves
                                     (filter #(valid-move? maze %)) ; filter valid-moves
                                     (rand-nth))                    ; take random move
                                   ; else 
                                   ; use best move (exploit)
                                   (get-best-move agent-loc q st))
                at               (case dir
                                   :up    0
                                   :down  1
                                   :right 2
                                   :left  3)
                st-1             (get-loc size next-move)
                rt               (if (= (:goal maze) next-move) 10 -0.1)]
            ;; update q-matrix
            (ql/update-qmatrix q st at rt st-1)
            ;; update steps
            (swap! n-steps inc)
            ;; recur with new move
            (recur (assoc maze :agent next-move)))))
      (println "Finished iteration: " i "in steps: " @n-steps))
    q))
        

(defn get-reachable-maze [size]
  (let [keep-looking (atom true)
        maze         (atom nil)]
    (while @keep-looking
      (let [generated-maze  (create-maze size)
            _               (visualize-maze generated-maze)
            input           (read-line)]
        (when (= input "y")
          (reset! maze generated-maze)
          (reset! keep-looking false))))
    @maze))
     

(defn -main []
  (let [size        16
        start-maze  (get-reachable-maze size)
        q           (learn start-maze)]
    (println "Printing Q matrix")
    (doseq [row q]
      (pprint/pprint row))
    ;; Now lets visualize the path using Q matrix
    (loop [maze start-maze]
      (if (not (won? maze))
        (do
          (Thread/sleep 200)
          (visualize-maze maze)
          (let [agent-loc        (:agent maze)
                st               (get-loc size agent-loc)
                [_ next-move]    (get-best-move agent-loc q st)]
            ; recur with new move
            (recur (assoc maze :agent next-move))))
        ; else visualise maze and exit
        (do
          (Thread/sleep 200)
          (visualize-maze maze))))))

(comment
  (do
    (set! *warn-on-reflection* true)
    (require 'ml-clojure.qlearning :reload))
  (plot/init!)

  (contains? #{3 5} 0)
  (-main)
  (+ 1 1)
  ,)
  
   
  