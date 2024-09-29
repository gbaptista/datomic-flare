import requests

response = requests.post(
    'http://localhost:3042/datomic/transact',
    headers={'Content-Type': 'application/json'},
    json={
        "data": """
        [{:db/ident       :book/title
          :db/valueType   :db.type/string
          :db/cardinality :db.cardinality/one
          :db/doc         "The title of the book."}

         {:db/ident       :book/genre
          :db/valueType   :db.type/string
          :db/cardinality :db.cardinality/one
          :db/doc         "The genre of the book."}]
        """
    }
)

print(response.json())

response = requests.post(
    'http://localhost:3042/datomic/transact',
    headers={'Content-Type': 'application/json'},
    json={
        "data": """
        [{:db/id      -1
          :book/title "The Tell-Tale Heart"
          :book/genre "Horror"}]
        """
    }
)

print(response.json())

response = requests.get(
    'http://localhost:3042/datomic/q',
    headers={'Content-Type': 'application/json'},
    json={
        "inputs": [
            {"database": {"latest": True}}
        ],
        "query": """
        [:find ?e ?title ?genre
         :where [?e :book/title ?title]
                [?e :book/genre ?genre]]
        """
    }
)

print(response.json())
