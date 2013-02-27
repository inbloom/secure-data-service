#!/usr/bin/env ruby

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

require 'json'
require 'digest/sha1'
require 'mongo'
require 'securerandom'

class Migration
  @@attendance_natural_keys = ['schoolYear', 'studentId', 'schoolId']

  def self.migrate doc, tenant=nil
    school_year_key = 'schoolYearAttendance'
    school_year_attendance = doc['body'][school_year_key]

    if school_year_attendance.nil?
      puts "It seems like the data has already been migrated!"
      exit
    end
    
    base_doc = deep_copy(doc)
    base_doc['body'].delete school_year_key

    if school_year_attendance.length == 0
      #Add the default school year if we have nowhere to pull it from
      doc['body']['schoolYear'] = '2011-2012'
      doc
    elsif school_year_attendance.length == 1
      #No splits necessary, just move fields around
      base_doc['body']['schoolYear'] = school_year_attendance[0]['schoolYear']
      base_doc['body']['attendanceEvent'] = school_year_attendance[0]['attendanceEvent']
      base_doc
    else
      { original_id: doc["_id"], split_records: split_school_year_attendance(base_doc, school_year_attendance, tenant) }
    end
  end
  
  def self.split_school_year_attendance base_doc, school_year_attendance_array, tenant
    #Remove the old id
    base_doc.delete("_id")
    docs = []
    school_year_attendance_array.each do |school_year|
      split = deep_copy(base_doc)
      split["body"]["schoolYear"] = school_year.fetch('schoolYear')
      split["body"]["attendanceEvent"] = school_year.fetch('attendanceEvent')
      split[:_id] = gen_id split, tenant
      docs << split
    end

    docs
  end

  def self.gen_id entity, tenant
    # for fixture data
    if tenant.nil?
      SecureRandom.uuid
    end

    natural_keys = @@attendance_natural_keys
		hash = ''
		delimiter = '|~'
		natural_keys.sort!
		#First we do entity type and tenant
		hash << "attendance" << delimiter << tenant.name << delimiter
		natural_keys.each do |nk|
			hash << entity['body'][nk] << delimiter
		end
		id = Digest::SHA1.hexdigest hash
		id += '_id'
		id
  end

  def self.start_migration input_file
    migrated_records = []
    File.readlines(input_file).each do |line|
      migrated_records << migrate(JSON.parse(line)).to_json
    end
    
    File.open(input_file, 'w') do |file|
      migrated_records.each do |record|
        file.puts record
      end
    end
  end

  def self.deep_copy(o)
    Marshal.load(Marshal.dump(o))
  end
end

class MongoHelper

  def initialize params
    @mongo_host = params[:mongo_host] || 'localhost'
    @mongo_port = params[:mongo_port] || 27017
  end

  def start!
    puts "Starting migration on mongo database #{@mongo_host}:#{@mongo_port}"
    tenants = get_tenant_dbs
    migrate_tenants tenants
  end

  private
    def client
      @mongo_client ||= Mongo::MongoClient.new(@mongo_host, @mongo_port)
    end

    def get_tenant_dbs
      tenants = []
      sli = client.db 'sli'
      sli.collection('tenant').find({}).each do |tenant|
        hash = tenant.fetch('body').fetch('dbName')
        name = tenant.fetch('body').fetch('tenantId')
        tenants << Tenant.new(hash, name)
      end
      tenants
    end

    def migrate_tenants tenants
      tenants.each do |tenant|
        puts "Migrating all attendance records for tenant #{tenant.name}"

        count = 0
        tenant_connection = client.db tenant.hash 
        tenant_connection.collection('attendance').find({}).each do |attendance|
          migrated = Migration.migrate attendance, tenant
          save_documents migrated, tenant
          count += 1
        end

        puts "Migrated #{count} attendance records for tenant #{tenant.name}"
        puts "There are now #{attendance_collection(tenant).count} attendance records in #{tenant.name}"
        puts 
      end
    end

    def save_documents document_hash, tenant
      if document_hash.is_a? Hash
        #Remove the old "parent" document
        remove_document document_hash[:original_id], tenant
        documents = document_hash[:split_records]
        documents.each { |doc| save_document doc, tenant }
      elsif document_hash.is_a? BSON::OrderedHash
        #No need to delete the "parent" document since we're just going to upsert it
        save_document document_hash, tenant
      end
    end

    def remove_document id, tenant
      attendance_collection(tenant).remove({ _id: id})
    end

    def save_document document, tenant
      attendance_collection(tenant).save(document.to_hash, 'upsert' => true)
    end

    def attendance_collection tenant
      db = client.db tenant.hash
      db.collection 'attendance'
    end

    class Tenant
      attr_reader :hash, :name
      def initialize hash, name
        @hash = hash
        @name = name
      end
    end
end

if __FILE__ == $0
  unless ARGV.length == 2
    puts "Will migrate attendance collection for every tenant entry in sli.tenant"
    puts 'Usage: ' + $0 + ' <dbhost> <dbport>'
    puts 'Example: ' + $0 + ' localhost 27017'
    exit(1)
  end
  
  migrater = MongoHelper.new({ mongo_host: ARGV[0], mongo_port: ARGV[1] })
  migrater.start!
end
