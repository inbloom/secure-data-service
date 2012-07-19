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


require_relative 'slc_fixer'

def run_fixer(tenant = nil)
  fixer = SLCFixer.new(@db, @log, tenant)
  begin
    fixer.start
  rescue Exception => e  
    @log.error "#{e}"
  end
end
def launch_fixer
  @tenants = Set.new
  #Get the tenants out.
  @db['tenant'].find({}) do |cur|
    cur.each { |tenant| @tenants.add tenant['body']['tenantId'] }
  end
  @tenants.each do |tenant|
    fork do
      run_fixer(tenant)
    end
  end
  run_fixer(nil) if @tenants.empty?
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
  @terminates = (ARGV[2].nil? ? false : true)
  database = (ARGV[1].nil? ? 'sli' : ARGV[1])
  hp = ARGV[0].split(":")
  connection = Mongo::Connection.new(hp[0], hp[1].to_i, :pool_size => 10, :pool_timeout => 25, :safe => {:wtimeout => 500})
  @log = Logger.new(STDOUT)
  @log.level = Logger::INFO
  @db = connection[database]
  if @terminates
    while true
      launch_fixer
    end
  else
    launch_fixer
  end
end
