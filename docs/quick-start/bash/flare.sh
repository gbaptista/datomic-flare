echo '
[{:db/ident       :book/title
  :db/valueType   :db.type/string
  :db/cardinality :db.cardinality/one
  :db/doc         "The title of the book."}

 {:db/ident       :book/genre
  :db/valueType   :db.type/string
  :db/cardinality :db.cardinality/one
  :db/doc         "The genre of the book."}]
' \
| bb -e '(pr-str (edn/read-string (slurp *in*)))' \
| curl -s http://localhost:3042/datomic/transact \
  -X POST \
  -H "Content-Type: application/json" \
  --data-binary @- <<JSON \
| jq
{
  "data": $(cat)
}
JSON

echo '
[{:db/id      -1
  :book/title "The Tell-Tale Heart"
  :book/genre "Horror"}]
' \
| bb -e '(pr-str (edn/read-string (slurp *in*)))' \
| curl -s http://localhost:3042/datomic/transact \
  -X POST \
  -H "Content-Type: application/json" \
  --data-binary @- <<JSON \
| jq
{
  "data": $(cat)
}
JSON

echo '
[:find ?e ?title ?genre
 :where [?e :book/title ?title]
        [?e :book/genre ?genre]]
' \
| bb -e '(pr-str (edn/read-string (slurp *in*)))' \
| curl -s http://localhost:3042/datomic/q \
  -X GET \
  -H "Content-Type: application/json" \
  --data-binary @- <<JSON \
| jq
{
  "inputs": [
    {
      "database": {
        "latest": true
      }
    }
  ],
  "query": $(cat)
}
JSON
