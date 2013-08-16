#Usage: ruby updateStaffEdOrgAssocEndDate.rb
#Written by Chris Kelly
#This script will update any users in the SSDS who cannot login because the end date exists as 2013-08-13
require 'rubygems'
require 'mongo'
require "highline/import"

def yesno(prompt = 'Continue?', default = true)
  a = ''
  s = default ? '[Y/n]' : '[y/N]'
  d = default ? 'y' : 'n'
  until %w[y n].include? a
    a = ask("#{prompt} #{s} ") { |q| q.limit = 1; q.case = :downcase }
    a = d if a.length == 0
  end
  a == 'y'
end

puts "This script will update documents in the staffEducationOrganizationAssociation collection in each tenant db"
puts "where users are set to expire on 2013-08-13"
if yesno("Do you wish to proceed?", false) == false
  exit(1)
end
conn = Mongo::Connection.new()
databases = conn.database_names
databases.each do |dbName|
  db = conn.db(dbName)
  coll = db.collection('staffEducationOrganizationAssociation')

  puts("Updating db #{dbName}")
  result = coll.update({'body.endDate' => '2013-08-13'}, {"$set" => {"body.endDate" => '2032-08-13'}}, {:multi => true})

  puts "Updated #{result['n']}/#{coll.count}"

end
