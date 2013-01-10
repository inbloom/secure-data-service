#!/usr/bin/env ruby

require 'digest/sha1'
require 'mongo'

tenantId = ARGV[0]

if tenantId == nil
  cyan = "\e[1;36m"
  reset = "\e[0m"
  Mongo::Connection.new('localhost').db('sli').collection('tenant').find.each do |row|
    body = row['body']
    puts "Tenant: #{cyan}#{body['tenantId']}#{reset} DBName: #{cyan}#{body['dbName']}#{reset}"
  end
else
  dbName = Digest::SHA1.hexdigest tenantId 

  puts "connecting to mongo database for tenant: #{tenantId}"
  system("mongo #{dbName}")
end

