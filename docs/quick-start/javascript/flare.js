async function main() {
  let response;

  response = await fetch('http://localhost:3042/datomic/transact', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      data: `
        [{:db/ident       :book/title
          :db/valueType   :db.type/string
          :db/cardinality :db.cardinality/one
          :db/doc         "The title of the book."}

         {:db/ident       :book/genre
          :db/valueType   :db.type/string
          :db/cardinality :db.cardinality/one
          :db/doc         "The genre of the book."}]
      `
    })
  });

  console.log(await response.json());

  response = await fetch('http://localhost:3042/datomic/transact', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      data: `
        [{:db/id      -1
          :book/title "The Tell-Tale Heart"
          :book/genre "Horror"}]
      `
    })
  });

  console.log(await response.json());

  response = await fetch('http://localhost:3042/datomic/q', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      inputs: [
        { database: { latest: true } }
      ],
      query: `
        [:find ?e ?title ?genre
         :where [?e :book/title ?title]
                [?e :book/genre ?genre]]
      `
    })
  });

  console.log(await response.json());
}

main();
