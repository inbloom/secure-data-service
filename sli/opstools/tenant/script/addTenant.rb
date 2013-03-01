#!/usr/bin/ruby

require 'optparse'
require 'mongo'
require 'SecureRandom'

mongo = "localhost"
db = "sli"
table = "tenant"
tenant = nil
remove = false

opts = OptionParser.new do |opts|
    opts.banner = "Usage: addTenant [-m mongo=#{mongo}] -t tenantId [-R]"
    opts.on('-m mongo', '--mongo mongo', 'Mono Host server')     { |m| mongo = m }
    opts.on('-t tenantId', '--tenant tenantId', 'Tenant Id')     { |t| tenant = t }
    opts.on('-R', '--remove', 'Remove the collection at start') { |r| remove = true }
    opts.on('-h', '--help', 'Display command line options') do
        puts opts
        exit
    end
    opts.parse!
    if tenant == nil then
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

puts "Inserting tenant #{tenant}"

doc = { "_id" => "2012aT-" + SecureRandom.uuid,
        "type" => "teanant",
    "body" => { "tenantId" => tenant, "landingZone" => [] },
        "metadata" => {
          "tenantId" => tenant,
      "createdBy" => tenant,
      "isOrphaned" => "true"
      }
      }
coll.insert(doc)

doc = coll.find({ "body.tenantId" => tenant })

puts "Found doc: #{doc.to_a}"
