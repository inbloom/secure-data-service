#!/usr/bin/ruby

require 'optparse'
require 'mongo'

mongo = "localhost"
db = "sli"
table = "tenant"
tenant = nil
district = nil
server = nil
userName = nil
pathDir = nil
homeDir = "/home/"
desc = nil
remove = false

opts = OptionParser.new do |opts|
    opts.banner = "Usage: addTenant [-m mongo=#{mongo}] -t tenantId -d district -s server -u userName [-p path] [-D desc]"
    opts.on('-m mongo', '--mongo mongo', 'Mono Host server')                 { |m| mongo = m }
    opts.on('-t tenantId', '--tenant tenantId', 'Tenant Id')                 { |t| tenant = t }
    opts.on('-d district', '--district district', 'District Id')             { |d| district = d }
    opts.on('-s server', '--server server', 'Landing Zone Server')           { |s| server = s }
    opts.on('-u userName', '--userName userName', 'Administrator User Name') { |u| userName = u }
    opts.on('-P pathDir', '--path pathDir', 'Landing Zone Path')             { |p| pathDir = p }
    opts.on('-D desc', '--desc desc', 'Landing Zone Description')            { |d| desc = d }
    opts.on('-h', '--help', 'Display command line options') do
        puts opts
        exit
    end
    opts.parse!
    if tenant == nil || district == nil || server == nil || userName == nil then
        puts opts
        exit 
    end
    if pathDir == nil then
        pathDir = "/home/#{tenant}/#{district}/#{userName}"
        puts "Using path dir of #{pathDir}."
    end
    if desc == nil then
        desc = "Landing Zone for user #{userName} of tenant #{tenant} of education organization #{district}"
        puts "Using desc of #{desc}."
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

puts "Inserting landing zone for tenant #{tenant} / district #{district}"
puts " having server #{server}, userName #{userName}, path #{pathDir } and desc #{desc}"

coll.update({ "tenantId" => tenant}, { "$push" => { "landingZone" =>
        { "district" => district, "server" => server, "path" => pathDir , "userName" => userName, "desc" => desc }}})

doc = coll.find({ "tenantId" => tenant })

puts "Found doc: #{doc.to_a}"
