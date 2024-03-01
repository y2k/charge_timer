(__unsafe_inject_code "package prelude")
(__unsafe_inject_code "fun plus(a: Any?, b: Any?): Int = (a as Int) + (b as Int)")
(__unsafe_inject_code "fun getm(x: Any?, y: String): Any? = if (x is Map<*, *>) x.get(y) else error(\"require Map\")")
(__unsafe_inject_code "fun geta(x: Any?, y: Int): Any? = if (x is List<*>) x.get(y) else error(\"require List\")")
(__unsafe_inject_code "fun <T> getm(x: Map<Any?, T>, y: String): T? = x[y]")
(__unsafe_inject_code "fun <T> geta(x: List<T>, y: Int): T = x[y]")
(__unsafe_inject_code "data class Sexp(val type: String, val value: String, val args: List<Sexp>)")
(__unsafe_inject_code "data class Env(val bindings: Map<String, Any?>)")

;; (let* [N 42
;;   add (fn* [a b] (+ a b N))
;;   main (fn* [] (add 1 2))]
;; (main))

;; (let*
;;  [play_alarm (fn* [^Context context]
;;                   (let [am (as (.getSystemService context Context/AUDIO_SERVICE) AudioManager)
;;                         sound_stream_id 5
;;                         max (.getStreamMaxVolume am sound_stream_id)]
;;                     (.setStreamVolume am sound_stream_id max 0)
;;                     (let [notification (.getDefaultUri RingtoneManager RingtoneManager/TYPE_ALARM)
;;                           r (.getRingtone RingtoneManager context notification)]
;;                       package)))
;;   main (fn* [] (play_alarm FIXME))]
;;  main)

(defn inter [^Env env ^Sexp node]
  (case node.type
    :list (case node.value

            "let*" (let [[binding] node.args]
                     (defn loop [^Env env ^"List<Sexp>" xs]
                       (if (= 0 xs.size)
                         env
                         (let [[kn vn] xs
                               k (str (inter env kn))
                               v (inter env vn)]
                           (loop
                            (__unsafe_inject_code "env.copy(bindings = env.bindings.plus(k to v))")
                             (.drop xs 2)))))
                     (let [env (as (loop env binding.args) Env)
                           body (.drop node.args 1)]
                       (defn loop [^"List<Sexp>" xs]
                         (if (= 1 xs.size)
                           (inter env (get xs 0))
                           (do
                             (inter env (get xs 0))
                             (loop (.drop xs 1)))))
                       (loop body)))

            "fn*" (let [[args_node] node.args]
                    (FIXME))

            "if" (let [[cond then_ else_] node.args]
                   (if (= true (inter env cond)) (inter env then_) (inter env else_)))

            (cond
              (.startsWith node.value ".") (FIXME "Reflection")
              :else (let [f (.get env.bindings node.value)]
                      (FIXME))))
    node.value))
