By the hammer of odin!

Run these commands:
$ rvm get head && rvm reload
$ rvm -v

If the version of rvm is 1.11.X or greater, you do NOT need 
to prepend 'bundle exec' before the following commands. Bundler integrated with rvm:
https://rvm.io/integration/bundler/

Prepare your environment (same as 'bundle install')
$ bundle

Generate data for a scenario:
$ bundle exec ruby driver.rb <scenario name>

Output is created in:
  generated/

Run unit tests
> bundle exec rake
