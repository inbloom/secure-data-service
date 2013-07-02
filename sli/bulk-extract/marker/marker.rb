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

require 'rbconfig'
require File.dirname(__FILE__) + '/lea_marker'

class PKFactory
  def create_pk(doc)
    return doc if doc[:_id]
    doc.delete(:_id)      # in case it exists but the value is nil
    doc['_id'] ||= doc['sliId']
    doc
  end
end

def run_marker(tenant, lea)
  connection = Mongo::MongoClient.new(@hp[0], @hp[1].to_i, :pool_size => 10, :pool_timeout => 25)
  db = connection[tenant]
  writeDb = connection.db("m_" + Digest::SHA1.hexdigest("#{tenant}_#{lea}"), :pk => PKFactory.new)
  @log.info "Creating a new stamper for #{lea}"
  fixer = LEAMarker.new(db, writeDb, tenant, lea, @log)
  begin
    fixer.start
    connection.close
    @log.error "Finished stamping tenant \'#{tenant}\'."
  rescue Exception => e
    #KILL THE THREADS
    @log.error "#{e}"
    @log.error "#{e.backtrace}"
    Process.kill("HUP", Process.pid)
    connection.close
  end
end
def launch_marker
  connection = Mongo::Connection.new(@hp[0], @hp[1].to_i, :pool_size => 10, :pool_timeout => 25, :w => 0)
  @db = connection[@database]
  #Get BE Apps
  appIds = []
  @db['application'].find({}) do |cur|
    cur.each do |app|
      if app['body'].include? 'isBulkExtract' and app['body']['isBulkExtract'] == true
        if app['body']['registration']['status'] == 'APPROVED'
          appIds.push app['_id']
        end
      end
    end
  end
  return if appIds.empty?
  @log.info "Searching for #{appIds}"
  #Get the tenants out.
  @log.info "Searching tenant collecitons"
  @db['tenant'].find({}) do |cur|
    cur.each do |tenant| 
      dbName = tenant['body']['dbName']
      tenantDB = connection[dbName]
      tenantDB['applicationAuthorization'].find({'body.applicationId' => {'$in' => appIds}}) do |cur|
        cur.each {|auth| auth['body']['edorgs'].each {|lea| @pids << run_marker(dbName, lea) }}
      end
    end
  end
  connection.close
end
if ARGV.count < 1
  puts "Usage: edorg_stamper <dbhost:port> <database> <terminates>"
  puts "\t dbhost - hostname for mongo"
  puts "\t port - port mongo is running on (27017 is common)"
  puts "\t database - the name of the system database (Defaults to sli)"
  puts "*** Note: These parameters must exist in the order they are presented ***"
  exit
else
  @pids = []
  @tenants = Set.new
  @terminates = (ARGV[2].nil? ? false : true)
  @database = (ARGV[1].nil? ? 'sli' : ARGV[1])
  @hp = ARGV[0].split(":")
  @log = Logger.new(STDOUT)
  @log.level = Logger::INFO
  backoff = 2
  begin
    launch_marker
    sleep(backoff)
    backoff **= 2 if backoff < 600
  end while @terminates
end
