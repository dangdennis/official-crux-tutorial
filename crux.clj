(require '[crux.api :as crux])

(def crux
  (crux/start-node {}))

;; Chapter 6 Saturn
;; 
;; "Currently there are only four transaction operations in Crux: put, delete, match and evict.
;; 		Transaction 	(Description)
;;     put    		(Writes a version of a document)
;;     delete    (Deletes a version of a document)
;;     match     (Stops a transaction if the precondition is not met.)
;;     evict    	(Removes an document entirely)

;; match:

;; match checks the current state of an entity - if the entity doesn’t match the provided doc, the transaction will not continue. You can also pass nil to check that the entity doesn’t exist prior to your transaction.

;; A match transaction takes the entity id, along with an expected document. Optionally you can provide a valid time.

;; Time in Crux is denoted #inst 'yyyy-MM-ddThh:mm:ss'. For example, 9:30 pm on January 2nd 1999 would be written: #inst \"1999-01-02T21:30:00\".

;; A complete match transaction has the form:

;;     [:crux.tx/match entity-id expected-doc valid-time]

;; Note that if there is no old-doc in the system, you can provide `nil` in its place."

(defn easy-ingest
  "Uses Crux put transaction to add a vector of 
  documents to a specified node"
  [node docs]
  (crux/submit-tx node
                  (vec (for [doc docs]
                         [:crux.tx/put doc]))))

(def data
  [{:crux.db/id :gold-harmony
    :company-name "Gold Harmony"
    :seller? true
    :buyer? false
    :units/Au 10211
    :credits 51}

   {:crux.db/id :tombaugh-resources
    :company-name "Tombaugh Resources Ltd."
    :seller? true
    :buyer? false
    :units/Pu 50
    :units/N 3
    :units/CH4 92
    :credits 51}

   {:crux.db/id :encompass-trade
    :company-name "Encompass Trade"
    :seller? true
    :buyer? true
    :units/Au 10
    :units/Pu 5
    :units/CH4 211
    :credits 1002}

   {:crux.db/id :blue-energy
    :seller? false
    :buyer? true
    :company-name "Blue Energy"
    :credits 1000}])

(easy-ingest crux data)

(defn stock-check
  [company-id item]
  {:result (crux/q (crux/db crux)
                   {:find '[name funds stock]
                    :where ['[e :company-name name]
                            '[e :credits funds]
                            ['e item 'stock]]
                    :args [{'e company-id}]})
   :item item})

(defn format-stock-check
  [{:keys [result item] :as stock-check}]
  (for [[name funds commod] result]
    (str "Name: " name ", Funds: " funds ", " item " " commod)))

(crux/submit-tx
 crux
 [[:crux.tx/match
   :blue-energy
   {:crux.db/id :blue-energy
    :seller? false
    :buyer? true
    :company-name "Blue Energy"
    :credits 1000}]
  [:crux.tx/put
   {:crux.db/id :blue-energy
    :seller? false
    :buyer? true
    :company-name "Blue Energy"
    :credits 900
    :units/CH4 10}]

  [:crux.tx/match
   :tombaugh-resources
   {:crux.db/id :tombaugh-resources
    :company-name "Tombaugh Resources Ltd."
    :seller? true
    :buyer? false
    :units/Pu 50
    :units/N 3
    :units/CH4 92
    :credits 51}]
  [:crux.tx/put
   {:crux.db/id :tombaugh-resources
    :company-name "Tombaugh Resources Ltd."
    :seller? true
    :buyer? false
    :units/Pu 50
    :units/N 3
    :units/CH4 82
    :credits 151}]])

(format-stock-check (stock-check :tombaugh-resources :units/CH4))
(format-stock-check (stock-check :blue-energy :units/CH4))

(crux/submit-tx
 crux
 [[:crux.tx/match
   :gold-harmony
   {:crux.db/id :gold-harmony
    :company-name "Gold Harmony"
    :seller? true
    :buyer? false
    :units/Au 10211
    :credits 51}]
  [:crux.tx/put
   {:crux.db/id :gold-harmony
    :company-name "Gold Harmony"
    :seller? true
    :buyer? false
    :units/Au 211
    :credits 51}]

  [:crux.tx/match
   :encompass-trade
   {:crux.db/id :encompass-trade
    :company-name "Encompass Trade"
    :seller? true
    :buyer? true
    :units/Au 10
    :units/Pu 5
    :units/CH4 211
    :credits 100002}]
  [:crux.tx/put
   {:crux.db/id :encompass-trade
    :company-name "Encompass Trade"
    :seller? true
    :buyer? true
    :units/Au 10010
    :units/Pu 5
    :units/CH4 211
    :credits 1002}]])

(format-stock-check (stock-check :gold-harmony :units/Au))
(format-stock-check (stock-check :encompass-trade :units/Au))