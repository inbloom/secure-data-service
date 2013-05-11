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

# Clean up the bulk extraction zone and bulk extract database according to arguments.
# This is the definition of the TenantCleaner class.

require 'rubygems'
require 'digest/sha1'
require 'yaml'
require 'mongo'
require 'time'
require 'fileutils'

properties = YAML::load_file('../config/bulk_extract_cleanup.yml')
DATABASE_NAME = properties["sli_database_name"]
DATABASE_HOST = properties["bulk_extract_host"]
DATABASE_PORT = properties["bulk_extract_port"]

class TenantCleaner
  def initialize(tenantName, dateTime, edOrgName, filePathname)
    @conn = Mongo::Connection.new(DATABASE_HOST, DATABASE_PORT)
    @sliDb = @conn.db(DATABASE_NAME)
    @beColl = @sliDb.collection('bulkExtractFiles')

    verifyInitParams(tenantName, dateTime, edOrgName, filePathname)
  end

  def verifyInitParams(tenantName, dateTime, edOrgName, filePathname)
    # Verify tenant name.
    verifytenantName(tenantName)

    # Verify Ed Org name, if not null.
    verifyEdOrgName(edOrgName)

    # Verify date time, if not null.
    verifyDateTime(dateTime)

    # Verify file pathname, if not null.
    verifyFilePathname(filePathname)
  end

  def verifytenantName(tenantName)
    # Verify tenant name.
    @tenantDbName = convertTenantIdToDbName(tenantName)
    tenantDbs = @conn.database_names
    if (!tenantDbs.include?(@tenantDbName))
      raise(NameError, "Tenant " + tenantName + " does not exist in the database")
    end
    @tenantBERecords = @beColl.find({"body.tenantId"=>tenantName})
    if (!@tenantBERecords.has_next?)
      raise(NameError, "Tenant " + tenantName + " does not have bulk extract files")
    end
    @tenantDb = @conn.db(@tenantDbName)
    @tenant = tenantName
  end

  def verifyEdOrgName(edOrgName)
    # Verify Ed Org, if not null.
    if (edOrgName != nil)
      edOrgColl = @tenantDb.collection('educationOrganization')
      if (edOrgName.end_with?("_id"))
        edOrgRecord = edOrgColl.find_one({"_id"=>edOrgName})
      else
        edOrgRecord = edOrgColl.find_one({"body.stateOrganizationId"=>edOrgName})
      end
      if (edOrgRecord == nil)
        raise(NameError, "Tenant " + @tenant + " does not contain EdOrg " + edOrgName)
      end
      @edOrgId = edOrgRecord['_id']
      @edOrgBERecords = @beColl.find({"body.tenantId"=>@tenant, "body.edorg"=>@edOrgId})
      if (!@edOrgBERecords.has_next?)
        raise(NameError, "Tenant " + @tenant + " does not have bulk extract files for EdOrg " + edOrgName)
      end
    end
    @edOrg = edOrgName
  end

  def verifyDateTime(dateTime)
    # Verify date time, if not null.
    if (dateTime != nil)
      begin
        isoTime = Time.iso8601(dateTime)
      rescue Exception => ex
        begin
          utcTime = Time.parse(dateTime)
        rescue Exception => ex
          raise(NameError, "Date-time " + dateTime + " is invalid")
        end
        begin
          dateTime = utcTime.iso8601
          isoTime = Time.iso8601(dateTime)
        rescue Exception => ex
          raise(NameError, "Date-time " + dateTime + " cannot be converted to ISO8601 format")
        end
      end
      if (isoTime == nil)
        raise(NameError, "Date-time " + dateTime + " cannot be converted to ISO8601 format")
      end
      if (@edOrgId != nil)
        @datedBERecords = @beColl.find({"body.tenantId"=>@tenant, "body.edorg"=>@edOrgId, \
                                        :"body.date"=>{:$lte=>isoTime}})
      else
        @datedBERecords = @beColl.find({"body.tenantId"=>@tenant, :"body.date"=>{:$lte=>isoTime}})
      end
      if (!@datedBERecords.has_next?)
        if (@edOrgId != nil)
          raise(NameError, "Tenant " + @tenant + " does not have bulk extract files for EdOrg " + edOrgName + \
                           " dated up to " + dateTime)
        else
          raise(NameError, "Tenant " + @tenant + " does not have bulk extract files dated up to " + dateTime)
        end
      end
    end
    @date = dateTime
  end

  def verifyFilePathname(filePathname)
    # Verify file pathname, if not null.
    if (filePathname != nil)
      if (filePathname[0] != '/')
        raise(NameError, "File path " + filePathname + " is not absolute")
      elsif (File.extname(filePathname) != ".tar")
        raise(NameError, "File " + filePathname + " is not a tar file")
      elsif (!File.exist?(filePathname))
        raise(NameError, "File " + filePathname + " does not exist")
      end
      @fileRecord = @beColl.find_one({"body.tenantId"=>@tenant, "body.path"=>filePathname})
      if (@fileRecord == nil)
        raise(NameError, "Tenant " + @tenant + " does not have bulk extract file " + filePathname)
      end
    end
    @file = filePathname
  end

  def tenant()
    return @tenant
  end

  def date()
    return @date
  end

  def edOrg()
    return @edOrg
  end

  def file()
    return @file
  end

  def clean()
    if (@file != nil)
      cleanFile()
    elsif ((@edOrg != nil) && (@date != nil))
      cleanEdOrgDate()
    elsif (@date != nil)
      cleanDate()
    elsif (@edOrg != nil)
      cleanEdOrg()
    else
      cleanTenant()
    end
  end

  def cleanFile()
    okayed = getOkay("You are about to delete bulk extract file " + @file + " and its database metadata " + \
                     "for tenant " + @tenant)
    if (okayed)
      begin
        FileUtils.rm(@file, :verbose => true)
      rescue Exception => ex
        puts "No files removed"
      end
      @beColl.remove(@fileRecord)
      puts "One file removed"
    end
  end

  def cleanEdOrgDate()
    okayed = getOkay("You are about to delete all bulk extract files and database metadata for " + \
                     "education organization " + @edOrg + " for tenant " + @tenant + " up to date " + @date)
    if (okayed)
      removed = 0
      failed = 0
      @datedBERecords.each do |datedBERecord|
        begin
        fileToRm = datedBERecord['body']['path']
          FileUtils.rm(fileToRm, :verbose => true)
          removed += 1
        rescue Exception => ex
          puts "WARNING: " + ex.message
          failed += 1
        end
        @beColl.remove(datedBERecord)
      end
      puts (removed + failed).to_s + " total files "
      puts removed.to_s + " files removed"
      puts failed.to_s + " files failed"
    end
  end

  def cleanEdOrg()
    okayed = getOkay("You are about to delete all bulk extract files and database metadata for " + \
                     "education organization " + @edOrg + " for tenant " + @tenant)
    if (okayed)
      removed = 0
      failed = 0
      @edOrgBERecords.each do |edOrgBERecord|
        begin
          fileToRm = edOrgBERecord['body']['path']
          FileUtils.rm(fileToRm, :verbose => true)
          removed += 1
        rescue Exception => ex
          puts "WARNING: " + ex.message
          failed += 1
        end
        @beColl.remove(edOrgBERecord)
      end
      puts (removed + failed).to_s + " total files "
      puts removed.to_s + " files removed"
      puts failed.to_s + " files failed"
    end
  end

  def cleanDate()
    okayed = getOkay("You are about to delete all bulk extract files and database metadata " + \
                     "for tenant " + @tenant + " up to date " + @date)
    if (okayed)
      removed = 0
      failed = 0
      @datedBERecords.each do |datedBERecord|
        begin
          fileToRm = datedBERecord['body']['path']
          FileUtils.rm(fileToRm, :verbose => true)
          removed += 1
        rescue Exception => ex
          puts "WARNING: " + ex.message
          failed += 1
        end
        @beColl.remove(datedBERecord)
      end
      puts (removed + failed).to_s + " total files "
      puts removed.to_s + " files removed"
      puts failed.to_s + " files failed"
    end
  end

  def cleanTenant()
    okayed = getOkay("You are about to delete all bulk extract files and database metadata for " + "tenant " + @tenant)
    if (okayed)
      removed = 0
      failed = 0
      @tenantBERecords.each do |tenantBERecord|
        begin
          fileToRm = tenantBERecord['body']['path']
          FileUtils.rm(fileToRm, :verbose => true)
          removed += 1
        rescue Exception => ex
          puts "WARNING: " + ex.message
          failed += 1
        end
        @beColl.remove(tenantBERecord)
      end
      puts (removed + failed).to_s + " total files "
      puts removed.to_s + " files removed"
      puts failed.to_s + " files failed"
    end
  end

  def getOkay(prompt)
    puts(prompt)
    print("Do you wish to proceed ('y' or 'yes' to proceed)? ")
    answer = STDIN.gets
    return (answer.strip.eql?("y") || answer.strip.eql?("yes"))
  end

  def convertTenantIdToDbName(tenantId)
    dbName = Digest::SHA1.hexdigest(tenantId)
    return dbName
  end

end
