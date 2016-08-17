(ns bouncy-ball.ball
  (:require [ball.vector :as vec]))

(defn make-ball [x y]
  {:location (vec/make-vector x y)
   :velocity (vec/make-vector 1 1)
   :acceleration (vec/make-vector 0 0)})

(defn- apply-forces [ball forces]
  (assoc ball
         :acceleration (vec/add
                        (:acceleration ball)
                        (vec/add-all forces))))

(defn update-location [ball]
  (let [new-velocity (->
                      (:velocity ball)
                      (vec/add (:acceleration ball))
                      (vec/limit 0.05))
        new-location (->
                      (:location ball)
                      (vec/add new-velocity))]
    (assoc ball
           :location new-location
           :velocity new-velocity
           :acceleration [0 0])))


(defn tick [w h r]
  (fn [ball other-balls]
    (update-location ball)))
