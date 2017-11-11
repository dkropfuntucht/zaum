# zaum

A Clojure library designed to ... well, that part is up to you.

## Usage
;; - connection details
{:host
 :port
 :username
 :scheme
 :Options
 :Password}

;;TODO: open problems: joins and subqueries

;; - get with a tablename and specific connection
{:operation  :get
 :entity     :tablename
 :connection {}}

;; -> get results:
{:operation :get
 :entity    :tablename}
;;==>
{:result  :get
 :command {:operation :get, :entity :tablename}
 :status  :ok ; - or error
 :data    [] ; - or exception on error
 :count   n
 :time    ms}

;; - get all of the documents or records from a table
{:operation :get
 :entity    :tablename}

;; - get a specific item or items
{:operation  :get
 :entity     :tablename
 :identifier {}}

{:operation  :get
 :entity     :tablename
 :identifier {}
 :sort-by    :key
 :sort-order :ascending :descending fn}

;; - update a given item
{:operation  :update
 :entity     :tablename
 :identifier {:key valuematch}
 :data       {}}

;; - delete a given item
{:operation  :delete
 :entity     :tablename
 :identifier []}

;; - create an item
{:operation :create
 :entity    :tablename
 :data      {}}

;; - create a table
{:operation :create
 :level     :entity
 :entity    :tablename}

;; - delete a table
{:operation :delete
 :level     :entity
 :entity    :tablename}

;; - migrations
{:migration :name
 :up   []        ; <- any transformation is possible
 :down []}

;; - we would use migrations to create views in couch

;; - perform a system-specific operation
{:operation :native
 :command   "" or some doc'd keyword}

;; - a thing for compacting a Couch DB
{:operation  :native
 :commend    :compact
 :entity     :tablename
 :connection {}}

;; - Front End API is merely:
(execute query)
(validate query) ;; <- use spec
(migrate-up [migrations])
(migrate-down [migrations] steps)
(schema) ;; - generate a schema - a lot to do here
(make-connection command) <- create a reusable connection model, maybe?


FIXME

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
