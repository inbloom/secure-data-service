#Usage: ruby updateStaffEdOrgAssocEndDate.rb
#Written by Chris Kelly
#This script will update any users in the SSDS who cannot login because the end date has passed
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
puts "where the users end date has already passed - Their new end date will be 1 year from today"
if yesno("Do you wish to proceed?", false) == false
  exit(1)
end
time = Time.new
conn = Mongo::Connection.new()
databases = conn.database_names
databases.each do |dbName|
  db = conn.db(dbName)
  coll = db.collection('staffEducationOrganizationAssociation')

  puts("Updating db #{dbName}")
  result = coll.update({'body.endDate' => {"$lt" => time.strftime("%Y-%m-%d")}}, {"$set" => {"body.endDate" => (time.year + 1).to_s + time.strftime("-%m-%d")}}, {:multi => true})

  puts "Updated #{result['n']}/#{coll.count}"

end
