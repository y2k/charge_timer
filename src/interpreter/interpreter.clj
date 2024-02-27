;; (defrecord Sexp [type value args])

(defn inter [node]
  (case (:type node)
    :list
    (case (:value node)
      "+" (let [[a b] (:args node)]
            (+ a b))
      ;; "if" (let [[cond then_ else_] node.args]
      ;;        (if (inter cond) (inter then_) (inter else_)))
      (FIXME node))
    (:value node)))
