(require '[crux.api :as crux])

(def crux
  (crux/start-node {}))

;; Chapter 3 Mercury

(defn easy-ingest
  "Uses Crux put transaction to add a vector of documents to a specified
  node"
  [node docs]
  (crux/submit-tx node
                  (vec (for [doc docs]
                         [:crux.tx/put doc]))))

(def data
  [{:crux.db/id :commodity/Pu
    :common-name "Plutonium"
    :type :element/metal
    :density 19.816
    :radioactive true}

   {:crux.db/id :commodity/N
    :common-name "Nitrogen"
    :type :element/gas
    :density 1.2506
    :radioactive false}

   {:crux.db/id :commodity/CH4
    :common-name "Methane"
    :type :molecule/gas
    :density 0.717
    :radioactive false}

   {:crux.db/id :commodity/Au
    :common-name "Gold"
    :type :element/metal
    :density 19.300
    :radioactive false}

   {:crux.db/id :commodity/C
    :common-name "Carbon"
    :type :element/non-metal
    :density 2.267
    :radioactive false}

   {:crux.db/id :commodity/borax
    :common-name "Borax"
    :IUPAC-name "Sodium tetraborate decahydrate"
    :other-names ["Borax decahydrate" "sodium borate" "sodium tetraborate" "disodium tetraborate"]
    :type :mineral/solid
    :appearance "white solid"
    :density 1.73
    :radioactive false}])


(easy-ingest crux data)

(crux/q (crux/db crux)
        '{:find [element]
          :where [[element :type :element/metal]]})

(=
 (crux/q (crux/db crux)
         '{:find [element]
           :where [[element :type :element/metal]]})

 (crux/q (crux/db crux)
         {:find '[element]
          :where '[[element :type :element/metal]]})

 (crux/q (crux/db crux)
         (quote
          {:find [element]
           :where [[element :type :element/metal]]})))

(crux/q (crux/db crux)
        '{:find [name]
          :where [[e :type :element/metal]
                  [e :common-name name]]})

(crux/q (crux/db crux)
        '{:find [name rho]
          :where [[e :density rho]
                  [e :common-name name]]})

(crux/q (crux/db crux)
        '{:find [name]
          :where [[e :type t]
                  [e :common-name name]]
          :args [{t :element/metal}]})

(defn filter-type
  [type]
  (crux/q (crux/db crux)
          {:find '[name]
           :where '[[e :type t]
                    [e :common-name name]]
           :args [{'t type}]}))

(defn filter-appearance
  [description]
  (crux/q (crux/db crux)
          {:find '[name IUPAC]
           :where '[[e :common-name name]
                    [e :IUPAC-name IUPAC]
                    [e :appearance ?appearance]]
           :args [{'?appearance description}]}))

(filter-type :element/metal)

(filter-appearance "white solid")

(crux/submit-tx
 crux
 [[:crux.tx/put
   {:crux.db/id :manifest
    :pilot-name "Johanna"
    :id/rocket "SB002-sol"
    :id/employee "22910x2"
    :badges ["SETUP" "PUT" "DATALOG-QUERIES"]
    :cargo ["stereo" "gold fish" "slippers" "secret note"]}]])