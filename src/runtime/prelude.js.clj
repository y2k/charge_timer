(defmacro do [& body] (concat (list 'let (vector)) body))

(defmacro println [& args] (concat (list 'console/info) args))

(defmacro FIXME [& args]
  (list 'throw
        (list 'Error.
              (concat
               (list
                'str
                (str "FIXME " __FILENAME__ ":" __LINE__ ":" (- __POSITION__ 1) " - "))
               args))))

(defmacro str [& args] (concat (list '+ "") args))

(defmacro jvm! [& body] (list 'comment body))

(defmacro js! [& body] (concat (list 'module) body))
