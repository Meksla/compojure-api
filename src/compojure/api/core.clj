(ns compojure.api.core
  (:require [compojure.api.meta :as meta]
            [compojure.api.routes :as routes]
            [compojure.api.middleware :as mw]
            [compojure.core :as compojure]
            [clojure.tools.macro :as macro]))

(defn- handle [handlers request]
  (some #(% request) handlers))

(defn routes
  "Create a Ring handler by combining several handlers into one."
  [& handlers]
  (let [handlers (seq (keep identity handlers))]
    (routes/create nil nil {} (vec handlers) (partial handle handlers))))

(defn wrap-routes
  "Apply a middleware function to routes after they have been matched."
  ([handler middleware]
   (let [x-handler (compojure/wrap-routes handler middleware)]
     ;; use original handler for docs and wrapped handler for implementation
     (routes/create nil nil {} [handler] x-handler)))
  ([handler middleware & args]
   (wrap-routes handler #(apply middleware % args))))

(defmacro defroutes
  "Define a Ring handler function from a sequence of routes.
  The name may optionally be followed by a doc-string and metadata map."
  {:style/indent 1}
  [name & routes]
  (let [[name routes] (macro/name-with-attributes name routes)]
    `(def ~name (routes ~@routes))))

(defmacro let-routes
  "Takes a vector of bindings and a body of routes.

  Equivalent to: `(let [...] (routes ...))`"
  {:style/indent 1}
  [bindings & body]
  `(let ~bindings (routes ~@body)))

(defn undocumented
  "Routes without route-documentation. Can be used to wrap routes,
  not satisfying compojure.api.routes/Routing -protocol."
  [& handlers]
  (let [handlers (keep identity handlers)]
    (routes/create nil nil {} nil (partial handle handlers))))

(defn middleware
  "Wraps routes with given middlewares using thread-first macro.

  Note that middlewares will be executed even if routes in body
  do not match the request uri. Be careful with middlewares that
  have side-effects.

  Check wrap-routes for version that only runs the middleware
  after route is matched."
  {:style/indent 1}
  [middleware & body]
  (let [body (apply routes body)
        wrap-mw (mw/compose-middleware middleware)]
    (routes/create nil nil {} [body] (wrap-mw body))))

(defmacro context {:style/indent 2} [& args] (meta/restructure nil      args {:context? true}))

(defmacro GET     {:style/indent 2} [& args] (meta/restructure :get     args nil))
(defmacro ANY     {:style/indent 2} [& args] (meta/restructure nil      args nil))
(defmacro HEAD    {:style/indent 2} [& args] (meta/restructure :head    args nil))
(defmacro PATCH   {:style/indent 2} [& args] (meta/restructure :patch   args nil))
(defmacro DELETE  {:style/indent 2} [& args] (meta/restructure :delete  args nil))
(defmacro OPTIONS {:style/indent 2} [& args] (meta/restructure :options args nil))
(defmacro POST    {:style/indent 2} [& args] (meta/restructure :post    args nil))
(defmacro PUT     {:style/indent 2} [& args] (meta/restructure :put     args nil))
