(require '[crux.api :as crux])

(def crux
  (crux/start-node {}))

;; Chapter 7 Jupiter
;; 
;; "Currently there are only four transaction operations in Crux: put, delete, match and evict.
;; 		Transaction 	(Description)
;;     put    		(Writes a version of a document)
;;     delete    (Deletes a version of a document)
;;     match     (Stops a transaction if the precondition is not met.)
;;     evict    	(Removes an document entirely)

;; delete:
;; Deletes a document at a given valid time. Historical version of the document will still be available.

;; The delete operation takes a valid eid with the option to include a start and end valid-time.

;; The document will be deleted as of the transaction time, or beteen the start and end valid-times if provided. Historical versions of the document that fall outside of the valid-time window will be preserved.

;; A complete delete transaction has the form:
;; [:crux.tx/delete eid valid-time-start valid-time-end]"

(crux/submit-tx crux
                [[:crux.tx/put {:crux.db/id :kaarlang/clients
                                :clients [:encompass-trade]}
                  #inst "2110-01-01T09"
                  #inst "2111-01-01T09"]

                 [:crux.tx/put {:crux.db/id :kaarlang/clients
                                :clients [:encompass-trade :blue-energy]}
                  #inst "2111-01-01T09"
                  #inst "2113-01-01T09"]

                 [:crux.tx/put {:crux.db/id :kaarlang/clients
                                :clients [:blue-energy]}
                  #inst "2113-01-01T09"
                  #inst "2114-01-01T09"]

                 [:crux.tx/put {:crux.db/id :kaarlang/clients
                                :clients [:blue-energy :gold-harmony :tombaugh-resources]}
                  #inst "2114-01-01T09"
                  #inst "2115-01-01T09"]])

(crux/entity-history
 (crux/db crux #inst "2116-01-01T09")
 :kaarlang/clients
 :desc
 {:with-docs? true})

(crux/submit-tx
 crux
 [[:crux.tx/delete :kaarlang/clients #inst "2110-01-01" #inst "2116-01-01"]])

(crux/entity-history
 (crux/db crux #inst "2116-01-01T09")
 :kaarlang/clients
 :desc
 {:with-docs? true})