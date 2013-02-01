#!/usr/bin/env ruby

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

=begin
  The application collection should be exported prior to running this script.
    mongoexport --host <HOST> --db sli --out <OUTPUT_FILE> --collection application 

  To revert, run these commands, where INPUT_FILE is the respective OUTPUT_FILE above.
    mongoimport --drop --host <HOST> --db sli --file <INPUT_FILE> --collection application 
=end

require 'mongo'

if (ARGV.size < 3)
  puts " How to use... "
  puts " ruby application_migration.rb <SERVER>:<MONGO_PORT> <PROD_APP_DEVELOPER_EMAIL> <USER_SANDBOX_TENANCY> [DEBUG]" 
  puts " example: "
  puts "   ruby application_migration.rb localhost:27017 app_developer01@slcedu.org developers@slcedu.org"
  puts "   ruby application_migration.rb localhost:27017 app_developer01@slcedu.org developers@slcedu.org true"
  exit
end

def user_confirmed? (message)
  begin 
    puts "#{message} (y/n)"
    answer = STDIN.gets.chomp
  end while(answer != "y" && answer != "n") 
  answer == "y"
end

def display_app (app_cursor) 
  app_cursor.each { |app|
    if @debug
      puts app
    else
      puts "App Name: [#{app["body"]["name"]}] -- By: [#{app["body"]["created_by"]}] -- Vendor: [#{app["body"]["vendor"]}] -- Sandbox Tenant: [#{app["body"]["author_sandbox_tenant"]}]" 
    end
  }
  app_cursor.rewind!
  puts "\n"
end

dbName = "sli"
host, port = ARGV[0].split(':')
created_by = ARGV[1]
sandbox_tenant = ARGV[2]
@debug = !!ARGV[3]

conn = Mongo::Connection.new(host, port)

begin
  @db = conn.db(dbName)

  appCollection = @db.collection('application')
  
  query = {"body.created_by" => created_by}
  
  apps = appCollection.find(query)
  if (apps.count == 0) 
    puts "Could not locate any applications created by: #{created_by}" 
    exit 
  end 

  display_app(apps)

  if user_confirmed? "Do you want to update those selected applications?"
    puts "Adding sandbox tenancy: #{sandbox_tenant} to selected application now..."
    apps.each { |app|
      app["body"]["author_sandbox_tenant"] = sandbox_tenant
      appCollection.save app 
    }
    puts "Applications have been updated to: "
    display_app(appCollection.find(query))
  else
    puts "Quit per user's choice..."
  end  

rescue Exception => e
  $stderr.print "Migration failed: #{e}\n"
  raise

ensure
  puts "close mongo connection"
  conn.close
end

