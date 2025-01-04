(ns _ (:require ["../vendor/build/0.1.0/main" :as b]))

(str
 (b/default)
 (b/build-files
  {:target "repl"
   :rules [{:src "build/html.clj"
            :target ".build/android/app/src/main/assets/index.html"}
           {:src "build/manifest.clj"
            :target ".build/android/app/src/main/AndroidManifest.xml"}]})
;;  (b/build-files
;;   {:target "js"
;;    :rules [{:src "web/domain.clj"
;;             :target ".github/android/app/src/main/assets/domain.js"}]})
;;  (b/build-files
;;   {:target "java"
;;    :root-ns "interpreter"
;;    :rules [{:src "vendor/interpreter/java/0.1.0/interpreter.clj"
;;             :target ".github/android/app/src/main/java/interpreter/interpreter.java"}]})
 (b/build-java-package
  {:root-ns "app"
   :target-dir ".build/android/app/src/main/java"
   :items ["main"
           "shared"]}))
