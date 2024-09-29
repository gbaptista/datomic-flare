require 'json'

require 'faraday'
require 'faraday/typhoeus'

Faraday.default_adapter = :typhoeus

response = Faraday.post('http://localhost:3042/datomic/transact') do |request|
  request.headers['Content-Type'] = 'application/json'
  request.body = {
    data: <<~EDN
      [{:db/ident       :book/title
        :db/valueType   :db.type/string
        :db/cardinality :db.cardinality/one
        :db/doc         "The title of the book."}

       {:db/ident       :book/genre
        :db/valueType   :db.type/string
        :db/cardinality :db.cardinality/one
        :db/doc         "The genre of the book."}]
    EDN
  }.to_json
end

puts response.body

response = Faraday.post('http://localhost:3042/datomic/transact') do |request|
  request.headers['Content-Type'] = 'application/json'
  request.body = {
    data: <<~EDN
      [{:db/id      -1
        :book/title "The Tell-Tale Heart"
        :book/genre "Horror"}]
    EDN
  }.to_json
end

puts response.body

response = Faraday.get('http://localhost:3042/datomic/q') do |request|
  request.headers['Content-Type'] = 'application/json'
  request.body = {
    inputs: [
      { database: { latest: true } }
    ],
    query: <<~EDN
      [:find ?e ?title ?genre
       :where [?e :book/title ?title]
              [?e :book/genre ?genre]]
    EDN
  }.to_json
end

puts response.body
