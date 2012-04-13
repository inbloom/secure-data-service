#!/usr/bin/ruby

require 'optparse'
require 'mongo'

mongo = "localhost"
db = "sli"
table = "tenant"
tenant = nil
country = nil
state = nil
remove = false

opts = OptionParser.new do |opts|
    opts.banner = "Usage: addTenant [-m mongo=#{mongo}] -t tenantId -c country -s state [-R]"
    opts.on('-m mongo', '--mongo mongo', 'Mono Host server')     { |m| mongo = m }
    opts.on('-t tenantId', '--tenant tenantId', 'Tenant Id')     { |t| tenant = t }
    opts.on('-c country', '--country country', 'Country Code')   { |c| country = c }
    opts.on('-s state', '--state state', 'State Code')           { |s| state = s }
    opts.on('-R', '--remove', 'Remove the collection at start') { |r| remove = true }
    opts.on('-h', '--help', 'Display command line options') do
        puts opts
        exit
    end
    opts.parse!
    if tenant == nil || country == nil || state == nil then
        puts opts
        exit 
    end
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

if remove then
    puts "Removing collection"
    r = coll.remove
    if r then
        puts "Collection removed"
    else
        puts "Collection removal failed; exiting."
        exit
    end
end

puts "Inserting tenant #{tenant} with country #{country} and state #{state}"

coll.update({ "tenantId" => tenant}, { "$set" =>
    { "tenantId" => tenant,
      "geographicLocation" => {
          "country" => country,
          "region" => state
       },
      "landingZone" => []
    }}, {:upsert => true})

doc = coll.find({ "tenantId" => tenant })

puts "Found doc: #{doc.to_a}"
