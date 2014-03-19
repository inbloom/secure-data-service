#!/usr/bin/env ruby
# Kill SLI processes
{
  8080 => 'API',
  8000 => 'Ingestion',
  8888 => 'Dashboard',
  8082 => 'SimpleIDP',
  3000 => 'Databrowser',
  3001 => 'Admin Tools',
  8081 => 'Sample App',
  10024 => 'Search Indexer'
}.each do |port, component|
  pid = `lsof -P -i:#{port} -sTCP:LISTEN -t`
  unless pid.empty?
    puts "killing #{component}"
    Process.kill(9, pid.to_i)
  end
end
