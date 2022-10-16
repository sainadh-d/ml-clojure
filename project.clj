(defproject ml-clojure "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [nrepl "1.1.0-alpha1"]
                 [org.clojars.davidpham87/plotly-pyclj "0.1.8"]
                 [org.tribuo/tribuo-all "4.2.1" :extension "pom" :scope "provided"]]
  :main ^:skip-aot ml-clojure.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
