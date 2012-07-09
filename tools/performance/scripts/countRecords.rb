require File.expand_path("../databaseUtils.rb", __FILE__)
require 'pp'

host = ARGV[0]
if (host == nil)
  host = 'localhost'
end

database = ARGV[1]
if (database == nil)
  database = 'sli'
end

collectionCounts = countEach(host, database)
pp collectionCounts

totalCount = countRecords(host, database)
puts "Total: #{totalCount}"

#recCount = count(host, database, 'section')
#puts "Section: #{recCount}"

#count(host, database, 'student')
#puts "Student: #{recCount}"

