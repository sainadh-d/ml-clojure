(ns ml-clojure.tribuo.tutorials.classification
  (:import 
    [java.nio.file Paths]
    [org.tribuo.evaluation TrainTestSplitter]
    [org.tribuo.data.csv CSVLoader]
    [org.tribuo MutableDataset]
    [org.tribuo.classification.evaluation LabelEvaluator]
    [org.tribuo.classification.sgd.linear LogisticRegressionTrainer]
    [org.tribuo.classification LabelFactory]))


(defn get-data-source [file]
  (let [csv-loader   (CSVLoader. (LabelFactory.))
        iris-headers (into-array ["sepal-length" "sepal-width" "petal-length" "petal-width", "species"])]
    (.loadDataSource 
      csv-loader 
      (Paths/get "resources" (into-array [file]))
      "species"
      iris-headers)))


(defn get-train-test-data [data-source]
  (let [iris-splitter (TrainTestSplitter. data-source 0.7 (Long. 1))]
    [(MutableDataset. (.getTrain iris-splitter)) (MutableDataset. (.getTest iris-splitter))]))

  
(defn train-model [train-data]
  (let [trainer (LogisticRegressionTrainer.)
        model   (.train trainer train-data)]   
    model))

;; evaluator
(defn evaluate "Returns evaluator" [model test-data]
  (let [evaluator (LabelEvaluator.)]
    (.evaluate evaluator model test-data)))   


(defn -main []
  (let [data-source            (get-data-source "bezdekiris.data")
        [train-data test-data] (get-train-test-data data-source)
        model                  (train-model train-data)]
    [model (evaluate model test-data)]))


(let [[model evaluator] (-main)]
  (def e evaluator)
  (def m model))

(comment
  (println (.toString e))
  (println (.getFeatureIDMap m))
  ,)

  
    