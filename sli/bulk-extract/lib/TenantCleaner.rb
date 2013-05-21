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

require 'yaml'
require 'digest/sha1'
require 'mongo'
require 'time'
require 'fileutils'

class TenantCleaner

  ##########
  public
  ##########

  def initialize(logger, tenantName, dateTime, edOrgName, filePathname)
    @logger = logger

    getConfigProperties()
    @conn = Mongo::Connection.new(@dbHost, @dbPort)
    @sliDb = @conn.db(@dbName)
    @beColl = @sliDb.collection('bulkExtractFiles')

    verifyInitParams(tenantName, dateTime, edOrgName, filePathname)
  end

  def clean()
    filesToCleanup = nil
    dbRecordsToRemove = nil
    if (@file != nil)
      filesToCleanup = "extract file " + @file + " and its database metadata for tenant " + @tenant
      dbRecordsToRemove = @fileRecords
    elsif ((@edOrg != nil) && (@date != nil))
      filesToCleanup = "all bulk extract files and database metadata for " + \
                       "education organization " + @edOrg + " for tenant " + @tenant + " up to date " + @date
      dbRecordsToRemove = @datedBERecords
    elsif (@date != nil)
      filesToCleanup = "all bulk extract files and database metadata " + \
                       "for tenant " + @tenant + " up to date " + @date
      dbRecordsToRemove = @datedBERecords
    elsif (@edOrg != nil)
      filesToCleanup = "all bulk extract files and database metadata for " + \
                       "education organization " + @edOrg + " for tenant " + @tenant
      dbRecordsToRemove = @edOrgBERecords
    else
      filesToCleanup = "all bulk extract files and database metadata for " + "tenant " + @tenant
      dbRecordsToRemove = @tenantBERecords
    end

    okayed = getOkay(filesToCleanup)
    if (okayed)
      putAndLogInfo("Removing " + filesToCleanup)
      removeFilesAndRecords(dbRecordsToRemove)
    end
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

  ##########
  private
  ##########

  def getConfigProperties()
    absParentDirname = File.expand_path('../..', __FILE__)
    properties = YAML::load_file(File.expand_path('../../config/bulk_extract_cleanup.yml', __FILE__))
    @dbHost = properties['bulk_extract_host']
    @dbPort = properties['bulk_extract_port']
    @dbName = properties['sli_database_name']
    @remDbRecRetries = properties['remove_db_record_retries']
    @remDbRecRetrySecs = properties['remove_db_record_retry_interval_secs']
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
    tenantDbName = convertTenantIdToDbName(tenantName)
    tenantDbs = @conn.database_names
    if (!tenantDbs.include?(tenantDbName))
      puts "WARNING: Database for tenant " + tenantName + " does not exist"
      @logger.warn "Database for tenant " + tenantName + " does not exist"
      @tenantDb = nil
    else
      @tenantDb = @conn.db(tenantDbName)
    end
    @tenantBERecords = @beColl.find({"body.tenantId"=>tenantName})
    if (!@tenantBERecords.has_next?)
      raise(NameError, "Tenant " + tenantName + " does not have bulk extract files")
    end
    @tenant = tenantName
  end

  def verifyEdOrgName(edOrgName)
    # Verify Ed Org, if not null.
    if (edOrgName != nil)
      if (@tenantDb != nil)
        edOrgColl = @tenantDb.collection('educationOrganization')
        edOrgRecord = edOrgColl.find_one({"body.stateOrganizationId"=>edOrgName})
        if (edOrgRecord == nil)
          puts "FATAL: Tenant " + @tenant + " does not contain EdOrg " + edOrgName
          @logger.fatal "Tenant " + @tenant + " does not contain EdOrg " + edOrgName
          raise(NameError, "Bulk extract files for EdOrg " + edOrgName + " for tenant " + @tenant + \
                           " cannot be identified, if they exist")
        end
        @edOrgId = edOrgRecord['_id']
        @edOrgBERecords = @beColl.find({"body.tenantId"=>@tenant, "body.edorg"=>@edOrgId})
        if (!@edOrgBERecords.has_next?)
          raise(NameError, "Tenant " + @tenant + " does not have bulk extract files for EdOrg " + edOrgName)
        end
      else
        raise(NameError, "Bulk extract files for EdOrg " + edOrgName + " for tenant " + @tenant + \
                         " cannot be identified, if they exist")
      end
    end
    @edOrg = edOrgName
  end

  def verifyDateTime(dateTime)
    # Verify date time, if not null.
    if (dateTime != nil)
      isoTime = toIsoTime(dateTime)
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

  def toIsoTime(dateTime)
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
    return isoTime
  end

  def getOkay(filesToCleanup)
    puts "You are about to delete " + filesToCleanup
    print("Do you wish to proceed ('y' or 'yes' to proceed)? ")
    answer = STDIN.gets
    return (answer.strip.eql?("y") || answer.strip.eql?("yes"))
  end

  def removeFilesAndRecords(dbRecordsToRemove)
    totalFiles = dbRecordsToRemove.count
    removedFiles = 0
    failedFiles = 0
    success = false
    dbRecordsToRemove.each do |dbRecordToRemove|
      success = removeDbRecord(dbRecordToRemove)
      break if (!success)

      removed = removeFile(dbRecordToRemove)
      if (removed)
        removedFiles += 1
      else
        failedFiles += 1
      end
    end

    printStats(totalFiles, removedFiles, failedFiles)

    raise(NameError, "Number of retries exceeded - aborting bulk extract cleanup") if (!success)
  end

  def removeDbRecord(dbRecordToRemove)
    retries = 0
    success = false
    fileToRm = dbRecordToRemove['body']['path']
    begin
      @beColl.remove(dbRecordToRemove)
      success = true
      if (retries > 0)
        putAndLogInfo("Retry successful!")
      end
    rescue Exception => ex
      retries += 1
      if (retries <= @remDbRecRetries)
        puts "ERROR: Cannot remove database record for file " + fileToRm + ": " + ex.message
        puts "Retrying in " + @remDbRecRetrySecs.to_s + " seconds (retry # " + retries.to_s + ")..."
        @logger.error "Cannot remove database record for file " + fileToRm + ": " + ex.message
        @logger.error "Retrying in " + @remDbRecRetrySecs.to_s + " seconds (retry # " + retries.to_s + ")..."
        sleep(@remDbRecRetrySecs)
        retry
      end
    end
    if (!success)
      putAndLogInfo("Retries failed!")
    end
    return success
  end

  def removeFile(dbRecordToRemove)
    begin
      fileToRm = dbRecordToRemove['body']['path']
      @logger.info "rm " + fileToRm
      FileUtils.rm(fileToRm)
      removed = true
    rescue Exception => ex
      @logger.warn ex.message
      removed = false
    end
    return removed
  end

  def printStats(totalFiles, removedFiles, failedFiles)
    processedFiles = removedFiles + failedFiles

    putAndLogInfo(processedFiles.to_s + " files processed of " + totalFiles.to_s + " total files")
    putAndLogInfo(removedFiles.to_s + " files removed")
    putAndLogInfo(failedFiles.to_s + " files failed")
    putAndLogInfo((totalFiles - processedFiles).to_s + " files not processed")
  end

  def putAndLogInfo(output)
    puts output
    @logger.info output
  end

  def convertTenantIdToDbName(tenantId)
    dbName = Digest::SHA1.hexdigest(tenantId)
    return dbName
  end

end
