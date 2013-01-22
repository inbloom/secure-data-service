#!/usr/bin/env ruby

# TODO: this is not portable! Tested on Mac, but would probably fail in Windows.

{   8080 => "API",
#    8087 => "MockZIS",
    8000 => "Ingestion",
    8888 => "Dashboard",
    8082 => "SimpleIDP",
    3000 => "Databrowser",
    3001 => "Admin Tools",
    8081 => "Sample App",
    10024 => "Search Indexer"
#    1337 => "SIF Agent"
}.each do |key, value|
  processId = `lsof -P -i:#{key} -sTCP:LISTEN -t`
  unless processId.empty?
    puts "killing #{value}"
    Process.kill(9, processId.to_i)
  end
end
