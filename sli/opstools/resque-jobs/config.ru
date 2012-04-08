require ::File.expand_path('../config/environment',  __FILE__)

require 'resque/server'
resque = Resque::Server.new

run Rack::URLMap.new \
  "/"       => resque,
  "/resque" => resque
