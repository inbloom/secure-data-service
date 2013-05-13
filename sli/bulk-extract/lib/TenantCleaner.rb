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

require 'digest/sha1'
require 'mongo'
require 'time'
require 'fileutils'

class TenantCleaner
  def initialize(tenantName, dateTime, edOrgName, filePathname, logger, dbHost, dbPort, dbName, \
                 remDbRecRetries, remDbRecRetrySecs)
    @logger = logger

    @conn = Mongo::Connection.new(dbHost, dbPort)
    @sliDb = @conn.db(dbName)
    @beColl = @sliDb.collection('bulkExtractFiles')
    @remDbRecRetries = remDbRecRetries
    @remDbRecRetrySecs = remDbRecRetrySecs

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
      edOrgRecord = edOrgColl.find_one({"_id"=>edOrgName})
      if (edOrgRecord == nil)
        edOrgRecord = edOrgColl.find_one({"body.stateOrganizationId"=>edOrgName})
        if (edOrgRecord == nil)
          raise(NameError, "Tenant " + @tenant + " does not contain EdOrg " + edOrgName)
        end
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
          raise(NameError, "Tenant " + @tenant + " does not have bulk extract files for EdOrg " + @edOrg + \
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
      @fileRecords = @beColl.find({"body.tenantId"=>@tenant, "body.path"=>filePathname})
      if (!@fileRecords.has_next?)
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
      puts "Removing bulk extract file " + @file + " and its database metadata for tenant " + @tenant
      @logger.info "Removing bulk extract file " + @file + " and its database metadata for tenant " + @tenant
      remove_files_and_records(@fileRecords)
    end
  end

  def cleanEdOrgDate()
    okayed = getOkay("You are about to delete all bulk extract files and database metadata for " + \
                     "education organization " + @edOrg + " for tenant " + @tenant + " up to date " + @date)
    if (okayed)
      puts "Removing all bulk extract files and database metadata for " + \
           "education organization " + @edOrg + " for tenant " + @tenant + " up to date " + @date
      @logger.info "Removing all bulk extract files and database metadata for " + \
                  "education organization " + @edOrg + " for tenant " + @tenant + " up to date " + @date
      remove_files_and_records(@datedBERecords)
    end
  end

  def cleanEdOrg()
    okayed = getOkay("You are about to delete all bulk extract files and database metadata for " + \
                     "education organization " + @edOrg + " for tenant " + @tenant)
    if (okayed)
      puts "Removing all bulk extract files and database metadata for " + \
           "education organization " + @edOrg + " for tenant " + @tenant
      @logger.info "Removing all bulk extract files and database metadata for " + \
                   "education organization " + @edOrg + " for tenant " + @tenant
      remove_files_and_records(@edOrgBERecords)
    end
  end

  def cleanDate()
    okayed = getOkay("You are about to delete all bulk extract files and database metadata " + \
                     "for tenant " + @tenant + " up to date " + @date)
    if (okayed)
      puts "Removing all bulk extract files and database metadata " + \
           "for tenant " + @tenant + " up to date " + @date
      @logger.info "Removing all bulk extract files and database metadata " + \
                   "for tenant " + @tenant + " up to date " + @date
      remove_files_and_records(@datedBERecords)
    end
  end

  def cleanTenant()
    okayed = getOkay("You are about to delete all bulk extract files and database metadata for " + "tenant " + @tenant)
    if (okayed)
      puts "Removing all bulk extract files and database metadata for " + "tenant " + @tenant
      @logger.info "Removing all bulk extract files and database metadata for " + "tenant " + @tenant
      remove_files_and_records(@tenantBERecords)
    end
  end

  def remove_files_and_records(dbRecordsToRemove)
    totalFiles = dbRecordsToRemove.count
    removedFiles = 0
    failedFiles = 0
    success = false
    dbRecordsToRemove.each do |dbRecordToRemove|
      # Remove db record.
      retries = 0
      success = false
      begin
        @beColl.remove(dbRecordToRemove)
        success = true
        if (retries > 0)
          puts "Retry successful!"
          @logger.info "Retry successful!"
        end
      rescue Exception => ex
        retries += 1
        if (retries <= @remDbRecRetries)
          puts "ERROR: Cannot remove database record: " + ex.message
          puts "Retrying in " + @remDbRecRetrySecs.to_s + " seconds (retry # " + retries.to_s + ")..."
          @logger.error "Cannot remove database record: " + ex.message
          @logger.error "Retrying in " + @remDbRecRetrySecs.to_s + " seconds (retry # " + retries.to_s + ")..."
          sleep(@remDbRecRetrySecs)
          retry
        end
      end
      if (!success)
        puts "Retries failed!"
        @logger.info "Retries failed!"
        break
      end

      # Remove file.
      begin
        fileToRm = dbRecordToRemove['body']['path']
        @logger.info "rm " + fileToRm
        FileUtils.rm(fileToRm)
        removedFiles += 1
      rescue Exception => ex
        @logger.warn ex.message
        failedFiles += 1
      end
    end

    printStats(totalFiles, removedFiles, failedFiles)

    raise(NameError, "Number of retries exceeded - aborting bulk extract cleanup") if (!success)
  end

  def printStats(totalFiles, removedFiles, failedFiles)
    processedFiles = removedFiles + failedFiles

    puts processedFiles.to_s + " files processed of " + totalFiles.to_s + " total files"
    puts removedFiles.to_s + " files removed"
    puts failedFiles.to_s + " files failed"
    puts (totalFiles - processedFiles).to_s + " files not processed"

    @logger.info processedFiles.to_s + " files processed of " + totalFiles.to_s + " total files"
    @logger.info removedFiles.to_s + " files removed"
    @logger.info failedFiles.to_s + " files failed"
    @logger.info (totalFiles - processedFiles).to_s + " files not processed"
  end

  def getOkay(prompt)
    puts prompt
    print("Do you wish to proceed ('y' or 'yes' to proceed)? ")
    answer = STDIN.gets
    return (answer.strip.eql?("y") || answer.strip.eql?("yes"))
  end

  def convertTenantIdToDbName(tenantId)
    dbName = Digest::SHA1.hexdigest(tenantId)
    return dbName
  end

end
