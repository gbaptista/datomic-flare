<?php

require 'vendor/autoload.php';

use GuzzleHttp\Client;

$client = new Client([
  'base_uri' => 'http://localhost:3042',
  'headers'  => ['Content-Type' => 'application/json'],
]);

$response = $client->post('/datomic/transact', [
  'json' => [
    'data' => '
      [{:db/ident       :book/title
        :db/valueType   :db.type/string
        :db/cardinality :db.cardinality/one
        :db/doc         "The title of the book."}

       {:db/ident       :book/genre
        :db/valueType   :db.type/string
        :db/cardinality :db.cardinality/one
        :db/doc         "The genre of the book."}]
    '
  ]
]);

echo $response->getBody();

$response = $client->post('/datomic/transact', [
  'json' => [
    'data' => '
      [{:db/id      -1
        :book/title "The Tell-Tale Heart"
        :book/genre "Horror"}]
    '
  ]
]);

echo $response->getBody();

$response = $client->get('/datomic/q', [
  'json' => [
    'inputs' => [
      ['database' => ['latest' => true]]
    ],
    'query' => '
      [:find ?e ?title ?genre
       :where [?e :book/title ?title]
              [?e :book/genre ?genre]]
    '
  ]
]);

echo $response->getBody();
