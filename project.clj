(defproject monte.server
  "0.0.1"
  :description "FIXME: write description"
  :ring {:handler monte.server.handler/app,
         :init monte.server.handler/init,
         :destroy monte.server.handler/destroy}
  :cljsbuild {:builds [{:source-paths ["src-cljs"],
                        :id "dev",
                        :compiler {:output-dir "resources/public/js/",
                                   :optimizations :none,
                                   :output-to "resources/public/js/app.js",
                                   :source-map true,
                                   :pretty-print true}}
                        {:source-paths ["src-cljs"],
                         :id "release",
                         :compiler {:closure-warnings {:non-standard-jsdoc :off},
                                    :optimizations :advanced,
                                    :output-to "resources/public/js/app.js",
                                    :output-wrapper false,
                                    :pretty-print false}}]}
  :plugins [[lein-ring "0.8.12"]
            [lein-environ "0.5.0"]
            [lein-ancient "0.5.5"]
            [lein-cljsbuild "1.0.3"]]
  :url "http://sernyak.com"
  :profiles {:uberjar {:aot :all},
             :production {:ring {:open-browser? false, :stacktraces? false, :auto-reload? false}},
             :dev {:dependencies [[ring-mock "0.1.5"]
                                  [ring/ring-devel "1.3.1"]
                                  [pjstadig/humane-test-output "0.6.0"]],
                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)],
                   :env {:dev true}}}
  :jvm-opts ["-server" "-Xms1024m", "-Xmx1024m", "-XX:PermSize=64M", "-XX:MaxPermSize=256M"]
  :dependencies [[lib-noir "0.9.1"]
                 [reagent-forms "0.2.4"]
                 [com.taoensso/tower "3.0.2"]
                 [org.clojure/clojurescript "0.0-2356"]
                 [prone "0.6.0"]
                 [noir-exception "0.2.2"]
                 [markdown-clj "0.9.54"]
                 [com.taoensso/timbre "3.3.1"]
                 [cljs-ajax "0.3.3"]
                 [selmer "0.7.1"]
                 [org.clojure/clojure "1.6.0"]
                 [environ "1.0.0"]
                 [ring-server "0.3.1"]
                 [secretary "1.2.1"]
                 [im.chit/cronj "1.4.2"]
                ;; 
                 [monte "0.0.2"]

                 ]
  :repl-options {:init-ns monte.server.repl}

  :min-lein-version "2.0.0")