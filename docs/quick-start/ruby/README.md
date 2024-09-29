### Ruby

You might want to check out the [Datomic Flare](https://github.com/gbaptista/ruby-datomic-flare) Ruby Gem.

Create a `Gemfile` file:

```ruby
source 'https://rubygems.org'

gem 'faraday', '~> 2.12'
gem 'faraday-typhoeus', '~> 1.1'
gem 'typhoeus', '~> 1.4', '>= 1.4.1'
```

```bash
bundle

bundle exec ruby flare.rb
```
