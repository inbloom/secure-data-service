#!/usr/bin/ruby

require 'optparse'
require 'mongo'

mongo = "localhost"
db = "sli"
table = "tenant"
tenant = nil
edOrg = nil
ingestionServer = nil
pathDir = nil
homeDir = "/home/"
desc = nil
remove = false

opts = OptionParser.new do |opts|
    opts.banner = "Usage: addTenant [-m mongo=#{mongo}] -t tenantId -e edOrg -i ingestionServer [-p path] [-d desc]"
    opts.on('-m mongo', '--mongo mongo', 'Mono Host server')                 { |m| mongo = m }
    opts.on('-t tenantId', '--tenant tenantId', 'Tenant Id')                 { |t| tenant = t }
    opts.on('-e edOrg', '--edOrg edOrg', 'Education Organization')             { |e| edOrg = e }
    opts.on('-i ingestionServer', '--ingestionServer ingestionServer', 'Ingestion Server')  { |i| ingestionServer = i }
    opts.on('-p pathDir', '--path pathDir', 'Landing Zone Path')             { |p| pathDir = p }
    opts.on('-d desc', '--desc desc', 'Landing Zone Description')            { |d| desc = d }
    opts.on('-h', '--help', 'Display command line options') do
        puts opts
        exit
    end
    opts.parse!
    if tenant == nil || edOrg == nil || ingestionServer == nil then
        puts opts
        exit 
    end
    if pathDir == nil then
        pathDir = "/home/ingestion/lz/inbound/#{tenant}/#{edOrg}"
        puts "Using path dir of #{pathDir}."
    else 
        pathDir = pathDir.sub(/C:/,"")
        pathDir = pathDir.sub(/Program Files \(x86\)\//,"")
        pathDir = pathDir.sub(/Git\//,"")
    end
    if desc == nil then
        desc = "Landing Zone for ingestion server #{ingestionServer}, tenant #{tenant}, education organization #{edOrg}"
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

puts "Inserting landing zone for tenant #{tenant} / edOrg #{edOrg}"
puts " having ingestionServer #{ingestionServer}, path #{pathDir } and desc #{desc}"

coll.update({ "body.tenantId" => tenant}, { "$push" => { "body.landingZone" =>
      { "educationOrganization" => edOrg, "ingestionServer" => ingestionServer, "path" => pathDir , "desc" => desc }}})

doc = coll.find({ "body.tenantId" => tenant })

puts "Found doc: #{doc.to_a}"
