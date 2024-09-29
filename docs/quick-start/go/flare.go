package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
)

type QueryRequestBody struct {
	Inputs []interface{} `json:"inputs"`
	Query  string        `json:"query,omitempty"`
	Data   string        `json:"data,omitempty"`
}

type DatabaseInput struct {
	Database struct {
		Latest bool `json:"latest"`
	} `json:"database"`
}

func makeRequest(method string, url string, body QueryRequestBody) string {
	jsonBody, _ := json.Marshal(body)

	client := &http.Client{}
	request, _ := http.NewRequest(method, url, bytes.NewBuffer(jsonBody))
	request.Header.Set("Content-Type", "application/json")

	response, _ := client.Do(request)
	defer response.Body.Close()

	responseBody, _ := ioutil.ReadAll(response.Body)

	return string(responseBody)
}

func main() {
	responseBody := makeRequest("POST", "http://localhost:3042/datomic/transact", QueryRequestBody{
		Data: `
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
			  :db/doc         "The year the book was published."}]
		`,
	})

	fmt.Println(responseBody)

	responseBody = makeRequest("POST", "http://localhost:3042/datomic/transact", QueryRequestBody{
		Data: `
			[{:db/id      -1
			  :book/title "The Tell-Tale Heart"
			  :book/genre "Horror"
			  :book/published_at_year 1843}]
		`,
	})

	fmt.Println(responseBody)

	responseBody = makeRequest("GET", "http://localhost:3042/datomic/q", QueryRequestBody{
		Inputs: []interface{}{
			DatabaseInput{Database: struct {
				Latest bool `json:"latest"`
			}{Latest: true}},
			"The Tell-Tale Heart",
		},
		Query: `
			[:find ?e ?title ?genre ?year
			 :in $ ?title
			 :where [?e :book/title ?title]
			        [?e :book/genre ?genre]
			        [?e :book/published_at_year ?year]]
		`,
	})

	fmt.Println(responseBody)
}
