#!/usr/bin/ruby

require 'optparse'
require 'mongo'

mongo = "localhost"
db = "sli"
table = "tenant"

opts = OptionParser.new do |opts|
    opts.banner = "Usage: dropTenant [-m mongo=#{mongo}]"
    opts.on('-m mongo', '--mongo mongo', 'Mono Host server')     { |m| mongo = m }
    opts.on('-h', '--help', 'Display command line options') do
        puts opts
        exit
    end
    opts.parse!
end

     
mdb = Mongo::Connection.new(mongo).db(db)

if mdb == nil then
    puts "Unable to connect to #{mongo} (db #{db})"
    exit
end

coll = mdb.collection(table)

if coll == nil then
    puts "Unable to find collection #{table}"
    exit
end

puts "Dumping collection"
r = coll.find.each { |row| puts row.inspect }
