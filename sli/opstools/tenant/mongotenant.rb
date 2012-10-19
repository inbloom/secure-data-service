#!/usr/bin/env ruby

require 'digest/sha1'

tenantId = ARGV[0]
dbName = Digest::SHA1.hexdigest tenantId 

puts "connecting to mongo database for tenant: #{tenantId}"
system("mongo #{dbName}")
