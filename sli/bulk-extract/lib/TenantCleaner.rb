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

    @tenant = tenantName
    @date = dateTime
    @edOrg = edOrgName
    @file = filePathname
  end

  def verifyInitParams(tenantName, dateTime, edOrgName, filePathname)
    # Verify tenant name.
    @tenantDBName = convertTenantIdToDbName(tenantName)
    @tenantDbs = @conn.database_names
    if (!@tenantDbs.include?(@tenantDBName))
      raise(ArgumentError, "Tenant " + tenantName + " does not exist in the database")
    end
    @tenantDb = @conn.db(@tenantDBName)

    # Verify Ed Org, if not null.
    if (edOrgName != nil)
      edOrgColl = @tenantDb.collection('educationOrganization')
      edOrgRecord = edOrgColl.find_one({"body.stateOrganizationId"=>edOrgName})
      if (edOrgRecord == nil)
        raise(ArgumentError, "Tenant " + tenantName + " does not contain EdOrg " + edOrgName)
      end
      @edOrgId = edOrgRecord['_id']
    end

    # Verify date time, if not null.
    if (dateTime != nil)
      isoTime = Time.iso8601(dateTime)
      if (isoTime == nil)
        raise(ArgumentError, "Date-time " + dateTime + " does not conform to ISO8601 format")
      end
      if (@edOrgId != nil)
        tenantRecords = @beColl.find({"body.tenantId"=>tenantName, "body.edorg"=>@edOrgId})
      else
        tenantRecords = @beColl.find({"body.tenantId"=>tenantName})
      end
      @datedFiles = Array.new
      tenantRecords.each do |tenantRecord|
        if (tenantRecord['body']['date'] <= isoTime)
          @datedFiles.push(tenantRecord['body']['path'])
        end
      end
      if (@datedFiles.empty?)
        if (@edOrgId != nil)
          raise(ArgumentError, "Tenant " + tenantName + " does not have bulk extract files for EdOrg " + edOrgName + \
                               " dated up to " + dateTime)
        else
          raise(ArgumentError, "Tenant " + tenantName + " does not have bulk extract files dated up to " + dateTime)
        end
      end
    end

    # Verify file pathname, if not null.
    if (filePathname != nil)
      if (filePathname[0] != '/')
        raise(ArgumentError, "File path " + filePathname + " is not absolute")
      elsif (File.extname(filePathname) != ".tar")
        raise(ArgumentError, "File " + filePathname + " is not a tar file")
      elsif (!File.exist?(filePathname))
        raise(ArgumentError, "File " + filePathname + " does not exist")
      end
      fileRecord = @beColl.find_one({"body.tenantId"=>tenantName, "body.path"=>filePathname})
      if (fileRecord == nil)
        raise(ArgumentError, "Tenant " + tenantName + " does not have bulk extract file " + filePathname)
      end
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

  def clean()
    if (file != nil)
      cleanFile()
    elsif ((edOrg != nil) && (date != nil))
      cleanEdOrgDate()
    elsif (date != nil)
      cleanDate()
    elsif (edOrg != nil)
      cleanEdOrg()
    else
      cleanTenant()
    end
  end

  def cleanFile()
    okayed = getOkay("You are about to delete bulk extract file " + @file + " and its database metadata " + \
                               "for tenant " + @tenant)
    if (okayed)
    end
  end

  def cleanEdOrgDate()
    okayed = getOkay("You are about to delete all bulk extract files and database metadata for " + \
                               "education organization " + @edOrg + " for tenant " + @tenant + " up to date " + @date)
    if (okayed)
    end
  end

  def cleanEdOrg()
    okayed = getOkay("You are about to delete all bulk extract files and database metadata for " + \
                               "education organization " + @edOrg + " for tenant " + @tenant)
    if (okayed)
    end
  end

  def cleanDate()
    okayed = getOkay("You are about to delete all bulk extract files and database metadata " + \
                               "for tenant " + @tenant + " up to date " + @date)
    if (okayed)
    end
  end

  def cleanTenant()
    okayed = getOkay("You are about to delete all bulk extract files and database metadata for " + "tenant " + @tenant)
    if (okayed)
    end
  end

  def getOkay(prompt)
    puts(prompt)
    print("Do you wish to proceed ('y' or 'yes' to proceed)? ")
    answer = STDIN.gets
    return (answer.eql?("y") || answer.eql?("yes"))
  end

  def convertTenantIdToDbName(tenantId)
    dbName = Digest::SHA1.hexdigest(tenantId)
    return dbName
  end

end
