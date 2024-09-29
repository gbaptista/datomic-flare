# Datomic Flare

A web server that offers an HTTP/JSON API for interacting with [Datomic](https://www.datomic.com) databases.

![The image features a logo with curved lines forming a tesseract, suggesting distortion and movement like space-time.](https://media.githubusercontent.com/media/gbaptista/assets/refs/heads/main/datomic-flare/datomic-flare-canvas.png)

_This is not an official Datomic project or documentation and it is not affiliated with Datomic in any way._

## TL;DR and Quick Start

Ensure you have [Java](https://clojure.org/guides/install_clojure#java) and [Clojure](https://clojure.org/guides/install_clojure) installed.

```bash
cp .env.example .env
```

```bash
clj -M:run
```

```text
[main] INFO flare.components.server - Starting server on http://0.0.0.0:3042 as peer
```

Ensure you have [curl](https://github.com/curl/curl), [bb](https://github.com/babashka/babashka), and [jq](https://github.com/jqlang/jq) installed.

Transact a Schema:

```ruby:runnable/to-request
client.dsl.transact_schema!(
  {
    book: {
      title: { type: :string, doc: 'The title of the book.' },
      genre: { type: :string, doc: 'The genre of the book.' },
    }
  },
  debug: state[:debug]
)
```

```bash:placeholder/to-curl
```

Assert a Fact:

```ruby:runnable/to-request
client.api.transact!(
  { data: <<~EDN
    [{:db/id      -1
      :book/title "The Tell-Tale Heart"
      :book/genre "Horror"}]
  EDN
  },
  debug: state[:debug]
)
```

```bash:placeholder/to-curl
```

Read the Data by Querying:

```ruby:runnable/to-request
client.api.q(
  { inputs: [{ database: { latest: true } }],
    query: <<~EDN
    [:find ?e ?title ?genre
     :where [?e :book/title ?title]
            [?e :book/genre ?genre]]
  EDN
  },
  debug: state[:debug]
)
```

```bash:placeholder/to-curl
```

```json:placeholder/to-json
```

```ruby:state
client.delete_database!({ name: state[:database_name] })
client.create_database!({ name: state[:database_name] })
```

{{ index }}

## Setup

### Running Datomic

You can set up Datomic by following its official documentation: [Pro Setup](https://docs.datomic.com/setup/pro-setup.html).

Alternatively, if you have Docker, you can leverage [datomic-pro-docker](https://github.com/gbaptista/datomic-pro-docker).

Clone the repository and copy the Docker Compose template:

```bash
git clone https://github.com/gbaptista/datomic-pro-docker.git

cd datomic-pro-docker

cp compose/postgresql.yml docker-compose.yml
```

Start PostgreSQL as Datomic's storage service:

```bash
docker compose up -d datomic-storage

docker compose logs -f datomic-storage
```

Create the table for Datomic databases:

```bash
docker compose run datomic-tools psql \
  -f bin/sql/postgres-table.sql \
  -h datomic-storage \
  -U datomic-user \
  -d my-datomic-storage
```

You will be prompted for a password, which is `unsafe`.

Start the Datomic Transactor:

```bash
docker compose up -d datomic-transactor

docker compose logs -f datomic-transactor
```

Create your database named `my-datomic-database`:

```bash
docker compose run datomic-tools clojure -M -e "$(cat <<'CLOJURE'
  (require '[datomic.api :as d])
  (def db-uri "datomic:sql://my-datomic-database?jdbc:postgresql://datomic-storage:5432/my-datomic-storage?user=datomic-user&password=unsafe")
  (d/create-database db-uri)
  (System/exit 0)
CLOJURE
)"
```

Your Datomic database is ready to start using Flare in _Peer Mode_:

```mermaid
graph RL
    Transactor --- Storage[(PostgreSQL)]
    Flare("Flare (Peer)") -.- Storage
    Flare --- Transactor
    Application([Application]) --- Flare
```

If you want to use _Client Mode_, start a Datomic Peer Server:

```bash
docker compose up -d datomic-peer-server

docker compose logs -f datomic-peer-server
```

Your Datomic database is ready to start using Flare in _Client Mode_:

```mermaid
graph RL
    Transactor --- Storage[(PostgreSQL)]
    PeerServer --- Transactor
    PeerServer -.- Storage
    Flare(Flare) --- PeerServer[Peer Server]
    Application([Application]) --- Flare
```

### Running Flare

With [Datomic running](#running-datomic), you are ready to run Flare:

```bash
git clone https://github.com/gbaptista/datomic-flare.git

cd datomic-flare
```

The server can operate in _Peer Mode_, embedding `com.datomic/peer` to establish a _Peer_ directly within the server, or in _Client Mode_, using `com.datomic/client-pro` to connect to a Datomic Peer Server.

Copy the `.env.example` file and fill it with the appropriate information.

```bash
cp .env.example .env
```

If you want _Peer Mode_:
```bash
FLARE_PORT=3042
FLARE_BIND=0.0.0.0

FLARE_MODE=peer

FLARE_PEER_CONNECTION_URI="datomic:sql://my-datomic-database?jdbc:postgresql://localhost:5432/my-datomic-storage?user=datomic-user&password=unsafe"
```

If you want _Client Mode_:
```bash
FLARE_PORT=3042
FLARE_BIND=0.0.0.0

FLARE_MODE=client

FLARE_CLIENT_ENDPOINT=localhost:8998
FLARE_CLIENT_SECRET=unsafe-secret
FLARE_CLIENT_ACCESS_KEY=unsafe-key
FLARE_CLIENT_DATABASE_NAME=my-datomic-database
```

Ensure you have [Java](https://clojure.org/guides/install_clojure#java) and [Clojure](https://clojure.org/guides/install_clojure) installed.

Run the server:

```bash
clj -M:run
```

```text
[main] INFO flare.components.server - Starting server on http://0.0.0.0:3042 as peer
```

Ensure you have [curl](https://github.com/curl/curl), [bb](https://github.com/babashka/babashka), and [jq](https://github.com/jqlang/jq) installed, and you should be able to start firing requests to the server:

```ruby:runnable/to-request
client.meta(debug: state[:debug])
```

```bash:placeholder/to-curl
```

```json:placeholder/to-json
```

## Quick Start

### Clojure

If you are using Clojure, you already have native access to [Datomic APIs](https://docs.datomic.com/clojure/index.html) and probably should not be using Flare.

### Java

If you are using Java, you already have native access to [Datomic APIs](https://docs.datomic.com/javadoc/datomic/package-summary.html) and probably should not be using Flare.

{{ quick_starts }}

## Usage

Ensure you have [curl](https://github.com/curl/curl), [bb](https://github.com/babashka/babashka), and [jq](https://github.com/jqlang/jq) installed.

### Meta

```ruby:runnable/to-request
client.meta(debug: state[:debug])
```

```bash:placeholder/to-curl
```

```json:placeholder/to-json
```

### Creating a Database

_In Client Mode, this operation is not supported as the Peer Server does not have it available._

```ruby:runnable/to-request
client.api.create_database!({ name: 'moonlight' }, debug: state[:debug])
```

```bash:placeholder/to-curl
```

```json:placeholder/to-json
```

### Deleting a Database

_In Client Mode, this operation is not supported as the Peer Server does not have it available._

```ruby:runnable/to-request
client.api.delete_database!({ name: 'moonlight' }, debug: state[:debug])
```

```bash:placeholder/to-curl
```

```json:placeholder/to-json
```

### Listing Databases

Flare on _Peer Mode_:
```bash
curl -s http://localhost:3042/datomic/get-database-names \
  -X GET \
  -H "Content-Type: application/json"  \
| jq
```

Flare on _Client Mode_:
```bash
curl -s http://localhost:3042/datomic/list-databases \
  -X GET \
  -H "Content-Type: application/json"  \
| jq
```

```json
{
  "data": [
    "my-datomic-database"
  ]
}
```

### Transacting Schema

```ruby:runnable/to-request
client.api.transact!(
  { data: <<~EDN
    [{:db/ident       :book/title
      :db/valueType   :db.type/string
      :db/cardinality :db.cardinality/one
      :db/doc         "The title of the book."}

     {:db/ident       :book/genre
      :db/valueType   :db.type/string
      :db/cardinality :db.cardinality/one
      :db/doc         "The genre of the book."}

     {:db/ident       :book/published_at_year
      :db/valueType   :db.type/long
      :db/cardinality :db.cardinality/one
      :db/doc         "The year the book was first published."}]
  EDN
  },
  debug: state[:debug]
)
```

```bash:placeholder/to-curl
```

```json:placeholder/to-json
```

### Asserting Facts

```ruby:runnable/to-request
client.api.transact!(
  { data: <<~EDN
    [{:db/id      -1
      :book/title "Pride and Prejudice"
      :book/genre "Romance"
      :book/published_at_year 1813}]
  EDN
  },
  debug: state[:debug]
)
```

```bash:placeholder/to-curl
```

```json:placeholder/to-json
```

```ruby:runnable/to-request

client.api.transact!(
  { data: <<~EDN
    [{:db/id      -1
      :book/title "Near to the Wild Heart"
      :book/genre "Novel"
      :book/published_at_year 1943}
     {:db/id      -2
      :book/title "A Study in Scarlet"
      :book/genre "Detective"
      :book/published_at_year 1887}
     {:db/id      -3
      :book/title "The Tell-Tale Heart"
      :book/genre "Horror"
      :book/published_at_year 1843}]
  EDN
  },
  debug: state[:debug]
)
```

```ruby:state
state[:wild_heart_entity_id] = result['data']['tempids']['-1']
state[:scarlet_entity_id] = result['data']['tempids']['-2']
```

```bash:placeholder/to-curl
```

```json:placeholder/to-json
```

### Reading Data by Entity

_In Client Mode, this operation is not supported as the Peer Server does not have it available._

```ruby:runnable/render->to-request
client.api.entity(
  { database: { latest: true },
    id: {{ state.wild_heart_entity_id }} },
  debug: state[:debug]
)
```

```bash:placeholder/to-curl
```

```json:placeholder/to-json
```

### Reading Data by Querying

```ruby:runnable/render->to-request
client.api.q(
  {
    inputs: [{ database: { latest: true } }],
    query: <<~EDN
      [:find ?e ?title ?genre ?year
       :where [?e :book/title ?title]
              [?e :book/genre ?genre]
              [?e :book/published_at_year ?year]]
    EDN
  },
  debug: state[:debug]
)
```

```bash:placeholder/to-curl
```

```json:placeholder/to-json
```

```ruby:runnable/render->to-request
client.api.q(
  {
    inputs: [
      { database: { latest: true } },
      'The Tell-Tale Heart'
    ],
    query: <<~EDN
      [:find ?e ?title ?genre ?year
       :in $ ?title
       :where [?e :book/title ?title]
              [?e :book/genre ?genre]
              [?e :book/published_at_year ?year]]
    EDN
  },
  debug: state[:debug]
)
```

```bash:placeholder/to-curl
```

```json:placeholder/to-json
```

### Accumulating Facts

```ruby:runnable/render->to-request
client.api.transact!(
  { data: <<~EDN
    [{:db/id 4611681620380877806 :book/genre "Gothic"}]
  EDN
  },
  debug: state[:debug]
)
```

```bash:placeholder/to-curl
```

```json:placeholder/to-json
```

### Retracting Facts

Retract the value of an attribute:

```ruby:runnable/render->to-request
client.api.transact!(
  { data: <<~EDN
    [[:db/retract 4611681620380877806 :book/genre "Gothic"]]
  EDN
  },
  debug: state[:debug]
)
```

```bash:placeholder/to-curl
```

```json:placeholder/to-json
```

Retract an attribute:

```ruby:runnable/render->to-request
client.api.transact!(
  { data: <<~EDN
    [[:db/retract 4611681620380877804 :book/genre]]
  EDN
  },
  debug: state[:debug]
)
```

```bash:placeholder/to-curl
```

```json:placeholder/to-json
```

Retract an entity:

```ruby:runnable/render->to-request
client.api.transact!(
  { data: <<~EDN
    [[:db/retractEntity 4611681620380877805]]
  EDN
  },
  debug: state[:debug]
)
```

```bash:placeholder/to-curl
```

```json:placeholder/to-json
```

### Retrieving Datoms from an Index

```ruby:runnable/render->to-request
client.api.datoms(
  { database: { latest: true },
    index: :eavt
  },
  debug: state[:debug]
)
```

```bash:placeholder/to-curl
```

```json
{
  "data": [
    [0, 10, "db", 0, true],
    [0, 11, 0, 54, true],
    [0, 11, 3, 0, true],
    [0, 11, 4, 0, true],
    // ...
  ]
}
```

## Architecture

Here are samples of how you may deploy Flare in your infrastructure.

### Embedded Peers
```mermaid
graph RL
    Transactor --- Storage[(Storage)]
    Flare("Flare (Peer)") -.- Storage
    Flare --- Transactor
    Application([Application]) --- Flare
```

```mermaid
graph RL
    Transactor --- Storage[(Storage)]
    FlareA("Flare (Peer)") -.- Storage
    FlareB("Flare (Peer)") -.- Storage
    FlareA --- Transactor
    FlareB --- Transactor
    ApplicationA([Application]) --- FlareA
    ApplicationB([Application]) --- FlareA
    ApplicationC([Application]) --- FlareB
    ApplicationD([Application]) --- FlareB
```

### Peer Servers
```mermaid
graph RL
    Transactor --- Storage[(Storage)]
    PeerServer --- Transactor
    PeerServer -.- Storage
    Flare(Flare) --- PeerServer[Peer Server]
    Application([Application]) --- Flare
```

```mermaid
graph RL
    Transactor --- Storage[(Storage)]
    PeerServer --- Transactor
    PeerServer -.- Storage
    FlareA(Flare) --- PeerServer[Peer Server]
    FlareB(Flare) --- PeerServer
    ApplicationA([Application]) --- FlareA
    ApplicationB([Application]) --- FlareA
    ApplicationC([Application]) --- FlareB
    ApplicationD([Application]) --- FlareB
```

```mermaid
graph RL
    Transactor --- Storage[(Storage)]
    PeerServerA[Peer Server] --- Transactor
    PeerServerA -.- Storage
    PeerServerB[Peer Server] --- Transactor
    PeerServerB -.- Storage
    FlareA(Flare) --- PeerServerA
    FlareB(Flare) --- PeerServerA
    FlareC(Flare) --- PeerServerB
    FlareD(Flare) --- PeerServerB
    ApplicationA([Application]) --- FlareA
    ApplicationB([Application]) --- FlareA
    ApplicationC([Application]) --- FlareB
    ApplicationD([Application]) --- FlareB
    ApplicationE([Application]) --- FlareC
    ApplicationF([Application]) --- FlareC
    ApplicationG([Application]) --- FlareD
    ApplicationH([Application]) --- FlareD
```

## About

### Characteristics

- Languages that play well with HTTP and JSON can interact with Datomic right away;

- Plug and play into any of the many flavors of Datomic's flexible infrastructure architecture;

- Minimal and transparent layer, not a DSL or framework, just straightforward access to Datomic;

- Despite JSON, both queries and transactions are done in [edn](https://github.com/edn-format/edn), enabling, e.g., powerful [Datalog](https://www.learndatalogtoday.org) queries.

### Trade-offs

- Languages have different data types, so edn -> JSON -> [Your Language] and vice-versa: something will be lost in translation and expressiveness;

- An extra layer in the architecture adds a new hop to requests, potentially increasing latency;

- Being one step away from Clojure reduces our power to fully leverage its types, data structures, immutability, and other desired properties;

- Some tricks that would be easy to do in Clojure + Datomic become more cumbersome: transaction functions, advanced Datalog datasources, lazy loading, etc.

### GET vs. POST

This API offers `GET` endpoints that accept a JSON body, an approach used by projects like [Elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html).

If you encounter issues, switch from `GET` to `POST`. The server will mirror the behavior of the `GET` requests.

Semantically, `GET` should [not have bodies](https://www.rfc-editor.org/rfc/rfc7231#section-4.3.1). Semantically, `POST` implies side effects, which is not the case here. So, we are aware that both are not ideal.

We considered using query strings with `GET`, but parsing was cumbersome and length limits were a risk, so we use `GET` with a body and `POST` as a fallback.

Future HTTP evolutions may address this use case, such as the proposed HTTP [`QUERY`](https://datatracker.ietf.org/doc/draft-ietf-httpbis-safe-method-w-body/) method.

## Development

```bash
clj -M:run
```

```bash
clj -M:repl
```

```bash
clj -M:format
clj -M:lint
```

```bash
cljfmt fix deps.edn src/
clj-kondo --lint deps.edn src/
```
