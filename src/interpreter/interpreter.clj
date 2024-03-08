(ns prelude)

(__unsafe_inject_code "fun plus(a: Any?, b: Any?) = (a as Int) + (b as Int)")
(__unsafe_inject_code "fun minus(a: Any?, b: Any?) = (a as Int) - (b as Int)")
(__unsafe_inject_code "fun getm(x: Any?, y: String): Any? = if (x is Map<*, *>) x.get(y) else error(\"require Map\")")
(__unsafe_inject_code "fun <T> geta(x: List<T>, y: Int): T = x[y]")
(__unsafe_inject_code "data class Env(val bindings: Map<String, Any?>)")

(defn ^Any? inter [^Env env ^Any? node]
  (if (is node List<*>)
    (let [args (.subList node 1 node.size)]
      (case (get node 0)
        "hash-map" (->
                    args
                    (.chunked 2)
                    (.map (fn [[k v]] (Pair. (inter env k) (inter env v))))
                    (.toMap))

        "vector" (.map args (fn [x] (inter env x)))

        "let*" (let [[binding] args]
                 (defn ^Any? loop [^Env env ^"List<Any>" xs]
                   (if (= 0 xs.size)
                     env
                     (let [[kn vn] xs
                           k (str kn)
                           v (inter env vn)]
                       (loop
                        (__unsafe_inject_code "env.copy(bindings = env.bindings.plus(k to v))")
                         (.drop xs 2)))))
                 (let [env (as (loop env (as binding "List<Any>")) Env)
                       body (.drop args 1)]
                   (defn ^Any? loop [^"List<Any?>" xs]
                     (if (= 1 xs.size)
                       (inter env (get xs 0))
                       (do
                         (inter env (get xs 0))
                         (loop (.drop xs 1)))))
                   (loop body)))

        "fn*" (let [args_node (as (get args 0) "List<String>")
                    body (.subList args 1 args.size)]
                (fn [^"List<Any?>" args]
                  (let [env (Env. (.plus env.bindings (.toMap (.zip args_node args))))]
                    (.fold body (as null "Any?") (fn [_ node] (inter env node))))))

        "if" (let [[cond then_ else_] args]
               (if (= true (inter env cond)) (inter env then_) (inter env else_)))

        "." (let [[source method] args]
              (let [args (-> args (.drop 2) (.map (fn [x] (inter env x))))
                    i (inter env source)
                    cls (if (not= null i)
                          i.javaClass
                          (Class/forName (as source String)))]
                (-> cls.methods
                    (.first (fn [x] (and
                                     (or (not= i null) (java.lang.reflect.Modifier/isStatic (.getModifiers x)))
                                     (= x.name method))))
                    (.invoke i (spread (.toTypedArray args))))))

        (let [f (as (.getOrElse env.bindings (as (get node 0) String) (fn [] (error node))) "(Any?) -> Any?")]
          (f (.map args (fn [x] (inter env x)))))))

    (let [value (as node String)]
      (cond
        (.startsWith value "\"") (.substring value 1 (- value.length 1))
        (not= null (.toIntOrNull value)) (.toInt value)
        :else (getm env.bindings value)))))
