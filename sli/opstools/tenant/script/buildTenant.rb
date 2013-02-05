=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


#!/usr/bin/ruby

require 'optparse'
require 'mongo'
#require 'active_support'

mongo = "localhost"
db = "sli"
tableIn = "educationOrganization"
tableOut = "tenant"

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

coll = mdb.collection(tableIn)
if coll == nil then
    puts "Unable to find collection #{tableIn}"
    exit
end

elemRoot = "body"
elemIn = "organizationCategories"
elemSOI = "stateOrganizationId"
elemADS = "address"
elemASA = "stateAbbreviation"

org = "State Education Agency"
puts "### #{org} ###"
puts "# Run the following commands to create Tenant entities"
puts "# Run 'addTenant.rb -h' for other options."
r = coll.find({ "#{elemRoot}.#{elemIn}" => "#{org}" },
              { :fields => ["#{elemRoot}.#{elemSOI}",
                            "#{elemRoot}.#{elemADS}.#{elemASA}"] })
states = []
r.each { |row|
  #puts row
  soi = row["#{elemRoot}"]["#{elemSOI}"]
  asa = row["#{elemRoot}"]["#{elemADS}"][0]["#{elemASA}"]
  states = { "#{asa}" => "#{soi}" }
  puts "addTenant.rb -m #{mongo} -t #{soi} -c USA -s #{asa}"
}

org = "Local Education Agency"
puts "### #{org} ###"
puts "# Create an LDAP account for each #{org} admin then"
puts "# Run the following commands to create Landing Zones for them"
puts "# You will want to fix the '-u' option."
puts "# Run 'addLZ.rb -h' for other options."
r = coll.find({ "#{elemRoot}.#{elemIn}" => "#{org}" },
              { :fields => ["#{elemRoot}.#{elemSOI}",
                            "#{elemRoot}.#{elemADS}.#{elemASA}"] })

r.each { |row|
  #puts row
  soi = row["#{elemRoot}"]["#{elemSOI}"]
  asa = row["#{elemRoot}"]["#{elemADS}"][0]["#{elemASA}"]
  tenant = states["#{asa}"]
  puts "addLZ.rb -m #{mongo} -t #{tenant} -d #{soi} -s #{asa}.lz.slidev.org -u admin-#{soi}"
}
