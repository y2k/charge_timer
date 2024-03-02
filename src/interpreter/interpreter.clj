(__unsafe_inject_code "package prelude")
(__unsafe_inject_code "fun plus(a: Any?, b: Any?) = (a as Int) + (b as Int)")
(__unsafe_inject_code "fun minus(a: Any?, b: Any?) = (a as Int) - (b as Int)")
(__unsafe_inject_code "fun getm(x: Any?, y: String): Any? = if (x is Map<*, *>) x.get(y) else error(\"require Map\")")
(__unsafe_inject_code "fun geta(x: Any?, y: Int): Any? = if (x is List<*>) x.get(y) else error(\"require List\")")
(__unsafe_inject_code "fun <T> getm(x: Map<Any?, T>, y: String): T? = x[y]")
(__unsafe_inject_code "fun <T> geta(x: List<T>, y: Int): T = x[y]")
(__unsafe_inject_code "data class Env(val bindings: Map<String, Any?>)")
(__unsafe_inject_code "typealias Sexp = Any")
(__unsafe_inject_code "val Sexp.type: String get() = if (this is List<*>) \"list\" else \"atom\"")
(__unsafe_inject_code "val Sexp.value: String get() = if (this is List<*>) { (this as List<Any>).get(0) as String } else (this as String)")
(__unsafe_inject_code "val Sexp.args: List<Any> get() = (this as List<Any>).subList(1, size)")

(defn ^Any? inter [^Env env ^Sexp node]
  (case node.type
    :list (case node.value

            "hash-map" (->
                        node.args
                        (.chunked 2)
                        (.map (fn [[k v]] (Pair. (inter env k) (inter env v))))
                        (.toMap))

            "let*" (let [[binding] node.args]
                     (defn ^Any? loop [^Env env ^"List<Sexp>" xs]
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
                       (defn ^Any? loop [^"List<Sexp>" xs]
                         (if (= 1 xs.size)
                           (inter env (get xs 0))
                           (do
                             (inter env (get xs 0))
                             (loop (.drop xs 1)))))
                       (loop body)))

            "fn*" (let [args_node (as (get node.args 0) "List<String>")
                        body (.subList node.args 1 node.args.size)]
                    (fn [^"List<Any?>" args]
                      (let [env (Env. (.plus env.bindings (.toMap (.zip args_node args))))]
                        (.fold body (as null "Any?") (fn [_ node] (inter env node))))))

            "if" (let [[cond then_ else_] node.args]
                   (if (= true (inter env cond)) (inter env then_) (inter env else_)))

            (cond
              (.startsWith node.value ".")
              (let [a (-> (as node "List<Any>") (.drop 2) (.map (fn [x] (inter env x))))
                    i (as (inter env (as (get node 1) Any)) Any)
                    m (.substring node.value 1)]
                (-> i.javaClass.methods
                    (.first (fn [x] (= x.name m)))
                    (.invoke i (spread (.toTypedArray a)))))

              :else (let [f (as (.getOrElse env.bindings node.value (fn [] (error node.value))) "(Any?) -> Any?")]
                      (f (.map node.args (fn [x] (inter env x)))))))
    (let [value node.value]
      (cond
        (.startsWith value "\"") (.substring value 1 (- value.length 1))
        :else (getm env.bindings node.value)))))
