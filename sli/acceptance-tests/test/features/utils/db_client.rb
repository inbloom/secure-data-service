require 'mongo'
require_relative 'rakefile_common'

class DbClient

  attr_reader :db

  # Valid options are :host, :port, :tenant or :db_name
  def initialize(options={})
    host = options[:host] || Property['DB_HOST'] || 'localhost'
    port = options[:port] || Property['DB_PORT'] || 27017
    db_name = options[:db_name] || tenant_to_db_name(options[:tenant] || ENV['DB_NAME'] || 'Midgar')
    @conn = Mongo::Connection.new(host, port)
    @db = @conn[db_name]
  end

  def open
    yield self
    close
  end

  def for_tenant(tenant)
    @db = @conn[tenant_to_db_name(tenant)]
    self
  end

  def for_sli
    @db = @conn[Property[:sli_database_name]]
    self
  end

  def close
    @conn.close
  end

  def collection(collection)
    db[collection]
  end

  def find(collection, query)
    db[collection].find query
  end

  def find_one(collection, query)
    db[collection].find_one query
  end

  def find_one_by_id(collection, id)
    find_one collection, id_query(id)
  end

  alias_method :find_by_id, :find_one_by_id

  def find_ids(collection)
    db[collection].find({}, :fields=>'_id').map{|f| f['_id']}
  end

  def update(collection, query, document, flags={})
    db[collection].update(query, document, flags)
  end

  def remove(collection, query)
    db[collection].remove query
  end

  def remove_by_id(collection, id)
    remove collection, id_query(id)
  end

  def insert(collection, document)
    db[collection].insert(document)
  end

  private

  def id_query(id)
    {'_id' => id}
  end

  def tenant_to_db_name(tenant)
    Digest::SHA1.hexdigest tenant
  end

end
