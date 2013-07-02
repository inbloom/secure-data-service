=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the 'License');
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an 'AS IS' BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end

require 'mongo'

# Handles deletion of all data in tenant dbs, excluding the "applicationAuthorization" collection.
# Also handles reindexing of data after dropping entity collections
class Migration
  def initialize params
    @mongo_host = params[:mongo_host] || 'localhost'
    @mongo_port = params[:mongo_port] || 27017
    @indexes = IndexReader.from_file(params[:index_file])
  end

  def start
    puts "Starting removal of all tenant data, excluding the applicationAuthorization, customRole, and adminDelegation collections"
    tenants = get_tenant_dbs
    tenants.each do |tenant|
      puts "Removing data from tenant #{tenant.name}"
      drop_collections_with_retry tenant, ["applicationAuthorization", "customRole", "adminDelegation"]

      puts "Reindexing tenant #{tenant.name}"
      reindex tenant
    end
  end

  private 
    def client
      @mongo_client ||= Mongo::MongoClient.new(@mongo_host, @mongo_port)
    end

    def get_tenant_dbs
      sli = client.db 'sli'
      sli.collection('tenant').find({}).map do |tenant|
        hash = tenant.fetch('body').fetch('dbName')
        name = tenant.fetch('body').fetch('tenantId')
        Tenant.new(hash, name)
      end
    end

    def reindex tenant
      db = client.db tenant.hash
      @indexes.each do |index|
        add_index_with_retry db, index
      end
    end

    def add_index_with_retry db, index, retry_count=0
      begin
        opts = {}
        if index.unique?
          opts[:unique] = true
        end
        db.collection(index.collection).create_index(index.path, opts)
      rescue => e
        if retry_count > 5
          puts "Error applying index #{index.path} to collection #{index.collection} in database #{db.name}"
          exit 1
        end
        puts "encountered an exception #{e}"
        puts "retrying failed index creation"
        add_index_with_retry db, index, retry_count + 1
      end
    end

    def drop_collections_with_retry tenant, exclusion=nil, retry_count=0
      collections = get_collections tenant
      conn = client.db tenant.hash

      collections.each do |collection|
        begin
          conn.drop_collection collection if ((!exclusion.include? collection) && (!collection.start_with? "system"))
        rescue => e
          puts "encountered an exception #{e}"
          if retry_count > 5 
            puts "Failed to drop collections in tenant #{tenant.name} within 5 tries."
            exit 1
          end
          puts "Retrying removal of data in tenant #{tenant.name}"
          drop_collections_with_retry tenant, exclusion, retry_count + 1
        end
      end
    end

    def get_collections tenant
      db = client.db tenant.hash
      db.collection_names
    end
    
    class Tenant
      attr_reader :hash, :name
      def initialize hash, name
        @hash = hash
        @name = name
      end
    end
end

class IndexReader
  def self.from_file file_location
    puts "Reading tenant index file from location #{file_location}"
    indexes = IO.readlines(file_location).select { |line| !line.start_with? '#' }
                                         .select { |line| !line.chomp.empty? }
    indexes.map do |line|
      from_string line
    end
  end

  def self.from_string str
    parse str
  end

  private
    def self.parse index
      fields = index.split(',')
      coll = fields[0]
      unique = fields[1] == "true" ? true : false
      index_fields = get_index_fields(fields.slice(2, fields.size))
      Index.new(coll, unique, index_fields)
    end

    def self.get_index_fields indexes
      map = {}
      indexes.each do |index|
        path, order = index.split(':')
        map[path] = order.to_i == 1 ? Mongo::ASCENDING : Mongo::DESCENDING  
      end
      map
    end
end

class Index
  attr_reader :collection, :path
  
  def initialize collection, unique, path
    @collection = collection
    @unique = unique
    @path = path
  end

  def ==(other)
    other.equal?(self) ||
      other.instance_of?(self.class) &&
      other.collection.eql?(self.collection) && 
      other.unique?.eql?(self.unique?) &&
      other.path.eql?(self.path)
  end

  def unique?
    @unique
  end
end

if __FILE__ == $0
  unless ARGV.length > 0
    puts "Will remove all tenant data, excluding the applicationAuthorization collection."
    puts "Each tenant will also be reindexed according to the new tenant db indexes file"
    puts 'Usage: ' + $0 + ' <dbhost:dbport> <optional: path to tenant index file default: ./index_migration/tenantDB_indexes.txt>'
    puts 'Example: ' + $0 + ' localhost:27017 tenantDB_index.txt'
    exit(1)
  end

  host, port = ARGV[0].split(':')
  index_file = ARGV[1] || "./index_migration/tenantDB_indexes.txt"

  migrater = Migration.new({ mongo_host: host, mongo_port: port, index_file: index_file })
  migrater.start
end
