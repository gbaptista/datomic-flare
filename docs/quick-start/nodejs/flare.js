const axios = require('axios');

async function main() {
  let response;

  response = await axios.post('http://localhost:3042/datomic/transact',
    { data: `
        [{:db/ident       :book/title
          :db/valueType   :db.type/string
          :db/cardinality :db.cardinality/one
          :db/doc         "The title of the book."}

         {:db/ident       :book/genre
          :db/valueType   :db.type/string
          :db/cardinality :db.cardinality/one
          :db/doc         "The genre of the book."}]
      `
    },
    { headers: { 'Content-Type': 'application/json' } }
  )


  console.log(response.data);

  response = await axios.post('http://localhost:3042/datomic/transact',
    { data: `
        [{:db/id      -1
          :book/title "The Tell-Tale Heart"
          :book/genre "Horror"}]
      `
    },
    { headers: { 'Content-Type': 'application/json' } }
  );

  console.log(response.data);

  response = await axios.get('http://localhost:3042/datomic/q', {
    headers: { 'Content-Type': 'application/json' },
    data: {
      inputs: [
        { database: { latest: true } }
      ],
      query: `
        [:find ?e ?title ?genre
         :where [?e :book/title ?title]
                [?e :book/genre ?genre]]
      `
    }
  });

  console.log(response.data);
}

main();
