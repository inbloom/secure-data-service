
=begin

Copyright 2012 Shared Learning Collaborative, LLC

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

require File.dirname(__FILE__) + '/slc_fixer'
# require_relative 'slc_fixer'
require 'rbconfig'

is_windows = (RbConfig::CONFIG['host_os'] =~ /mswin|mingw|cygwin/)

trap('HUP') {} unless is_windows

if ARGV.count < 1
  puts "Usage: teacher_stamper <dbhost:port> <database> <terminates>"
  puts "\t dbhost - hostname for mongo"
  puts "\t port - port mongo is running on (27017 is common)"
  puts "\t database - the name of the database (Defaults to sli)"
  puts "\t terminates - if there is any parameter for this, it will run forever."
  puts "*** Note: These parameters must exist in the order they are presented ***"
  exit
else
  terminates = (ARGV[2].nil? ? false : true)
  database = (ARGV[1].nil? ? 'sli' : ARGV[1])
  hp = ARGV[0].split(":")
  log = Logger.new(STDOUT)
  log.level = Logger::WARN
  if terminates
    while true
      begin
        connection = Mongo::Connection.new(hp[0], hp[1].to_i, :pool_size => 10, :pool_timeout => 25, :safe => {:wtimeout => 500})
        db = connection[database]
        fixer = SLCFixer.new(db, log)
        fixer.start
        connection.close
      rescue Exception => e  
        log.error "EXCEPTION #{e}"
        connection.close
        sleep 5
      end
    end
  else
    connection = Mongo::Connection.new(hp[0], hp[1].to_i, :pool_size => 10, :pool_timeout => 25, :safe => {:wtimeout => 500})
    db = connection[database]
    fixer = SLCFixer.new(db, log)
    fixer.start
    connection.close
  end
end

