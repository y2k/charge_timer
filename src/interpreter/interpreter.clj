(__unsafe_inject_code "package prelude")
(__unsafe_inject_code "fun plus(a: Any?, b: Any?): Int = (a as Int) + (b as Int)")
(__unsafe_inject_code "fun getm(x: Any?, y: String): Any? = if (x is Map<*, *>) x.get(y) else error(\"require Map\")")
(__unsafe_inject_code "fun geta(x: Any?, y: Int): Any? = if (x is List<*>) x.get(y) else error(\"require List\")")
(__unsafe_inject_code "fun <T> getm(x: Map<Any?, T>, y: String): T? = x[y]")
(__unsafe_inject_code "fun <T> geta(x: List<T>, y: Int): T = x[y]")
(__unsafe_inject_code "data class Sexp(val type: String, val value: String, val args: List<Sexp>)")
(__unsafe_inject_code "data class Env(val methods: Map<String, (List<Any?>) -> Any?>)")

(defn inter [^Env env ^Sexp node]
  (case node.type
    :list (case node.value

            "module" (.fold node.args (fn [acc n] FIXME))

            "defn" (let [[name] node.args]
                     FIXME)

            "+" (let [[a b] node.args] (+ a b))

            "if" (let [[cond then_ else_] node.args]
                   (if (= true (inter env cond)) (inter env then_) (inter env else_)))

            (cond
              (.startsWith node.value ".")
              (FIXME "Reflection")

              :else (FIXME)))
    node.value))
