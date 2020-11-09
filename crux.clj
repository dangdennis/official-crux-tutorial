(require '[crux.api :as crux])

(def crux
  (crux/start-node {}))

;; Chapter 4 Neptune
;; Querying with valid time

(crux/submit-tx
 crux
 [[:crux.tx/put
   {:crux.db/id :consumer/RJ29sUU
    :consumer-id :RJ29sUU
    :first-name "Jay"
    :last-name "Rose"
    :cover? true
    :cover-type :Full}
   #inst "2114-12-03"]])

(crux/submit-tx
 crux
 [[:crux.tx/put	;; (1) 
   {:crux.db/id :consumer/RJ29sUU
    :consumer-id :RJ29sUU
    :first-name "Jay"
    :last-name "Rose"
    :cover? true
    :cover-type :Full}
   #inst "2113-12-03" ;; Valid time start
   #inst "2114-12-03"] ;; Valid time end

  [:crux.tx/put  ;; (2)
   {:crux.db/id :consumer/RJ29sUU
    :consumer-id :RJ29sUU
    :first-name "Jay"
    :last-name "Rose"
    :cover? true
    :cover-type :Full}
   #inst "2112-12-03"
   #inst "2113-12-03"]

  [:crux.tx/put	;; (3)
   {:crux.db/id :consumer/RJ29sUU
    :consumer-id :RJ29sUU
    :first-name "Jay"
    :last-name "Rose"
    :cover? false}
   #inst "2112-06-03"
   #inst "2112-12-02"]

  [:crux.tx/put ;; (4)
   {:crux.db/id :consumer/RJ29sUU
    :consumer-id :RJ29sUU
    :first-name "Jay"
    :last-name "Rose"
    :cover? true
    :cover-type :Promotional}
   #inst "2111-06-03"
   #inst "2112-06-03"]])

(crux/q (crux/db crux #inst "2115-07-03")
        '{:find [consumer_id cover type]
          :where [[e :consumer-id consumer_id]
                  [e :cover? cover]
                  [e :cover-type type]]
          :args [{:consumer_id :RJ29sUU}]})

(crux/q (crux/db crux #inst "2111-07-03")
        '{:find [cover type]
          :where [[e :consumer-id :RJ29sUU]
                  [e :cover? cover]
                  [e :cover-type type]]})

(crux/q (crux/db crux #inst "2112-07-03")
        '{:find [cover type]
          :where [[e :consumer-id :RJ29sUU]
                  [e :cover? cover]
                  [e :cover-type type]]})

(crux/submit-tx
 crux
 [[:crux.tx/put
   {:crux.db/id :manifest
    :pilot-name "Johanna"
    :id/rocket "SB002-sol"
    :id/employee "22910x2"
    :badges ["SETUP" "PUT" "DATALOG-QUERIES" "BITEMP"]
    :cargo ["stereo" "gold fish" "slippers" "secret note"]}]])