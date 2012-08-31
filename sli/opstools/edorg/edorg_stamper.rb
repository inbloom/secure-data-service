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
require File.dirname(__FILE__) + '/slc_fixer'

is_windows = (RbConfig::CONFIG['host_os'] =~ /mswin|mingw|cygwin/)
if is_windows
  module Process
    def fork
      yield
    end
  end
end
trap('HUP') {
  @pids.each do |pid|
    @log.warn "Killing process #{pid}"
    Process.kill("HUP", pid)
  end
} unless is_windows


def run_fixer(tenant = nil)
  connection = Mongo::Connection.new(@hp[0], @hp[1].to_i, :pool_size => 10, :pool_timeout => 25)
  db = connection[@database]
  @log.info "Creating a new stamper for #{tenant}"
  fixer = SLCFixer.new(db, @terminates, tenant)
  begin
    fixer.start
    connection.close
    @log.error "Finished stamping tenant \'#{tenant}\'."
  rescue Exception => e
    #KILL THE THREADS
    @tenants.delete tenant
    Process.kill("HUP", Process.pid)
    @log.error "#{e}"
    connection.close
  end
end
def launch_fixer
  connection = Mongo::Connection.new(@hp[0], @hp[1].to_i, :pool_size => 10, :pool_timeout => 25)
  @db = connection[@database]
  #Get the tenants out.
  @log.info "Searching tenant collecitons"
  @db['tenant'].find({}) do |cur|
    cur.each do |tenant| 
      if !@tenants.include? tenant['body']['tenantId']
        @log.info "Starting stamper for #{tenant['body']['tenantId']}"
        @tenants.add tenant['body']['tenantId'] 
        @pids << fork {run_fixer(tenant['body']['tenantId'])}
      end
    end
  end

  # If there is no tenant connection, we stamp stupidly
  @log.info "No tenant collection, repainting the bridge" if @tenants.empty?
  @pids << fork {run_fixer(nil)} if @tenants.empty?
  Process.waitall unless @terminates
  connection.close
end
if ARGV.count < 1
  puts "Usage: edorg_stamper <dbhost:port> <database> <terminates>"
  puts "\t dbhost - hostname for mongo"
  puts "\t port - port mongo is running on (27017 is common)"
  puts "\t database - the name of the database (Defaults to sli)"
  puts "\t terminates - if there is any parameter for this, it will run forever."
  puts "*** Note: These parameters must exist in the order they are presented ***"
  exit
else
  @pids = []
  @tenants = Set.new
  @terminates = (ARGV[2].nil? ? false : true)
  @database = (ARGV[1].nil? ? 'sli' : ARGV[1])
  @hp = ARGV[0].split(":")
  @log = Logger.new(STDOUT)
  @log.level = Logger::WARN
  backoff = 2
  begin
    launch_fixer
    sleep(backoff)
    backoff **= 2 if backoff < 600
  end while @terminates
end
