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

=begin
  The section and courseOffering collections should be exported prior to running this script.
    mongoexport --host <HOST> --db <DATABASE> --out <OUTPUT_FILE> --collection section
    mongoexport --host <HOST> --db <DATABASE> --out <OUTPUT_FILE> --collection courseOffering

  To revert, run these commands, where INPUT_FILE is the respective OUTPUT_FILE above.
    mongoimport --drop --host <HOST> --db <DATABASE> --file <INPUT_FILE> --collection section
    mongoimport --drop --host <HOST> --db <DATABASE> --file <INPUT_FILE> --collection courseOffering
=end

require 'securerandom'
require 'mongo'
require 'pp'

if (ARGV.size < 2)
  puts " How to use... "
  puts " ruby courseOffering_migration.rb <DATABASE> <SERVER>:<MONGO_PORT> [DEBUG]"
  puts " example: "
  puts "   ruby courseOffering_migration.rb sli localhost:27017"
  puts "   ruby courseOffering_migration.rb sli localhost:27017 true"
  exit
end

# remove index on section.courseId

# for each section
#   create new courseOffering
#     session is section's session
#     edOrg is section's edOrg
#     localCourseCode is section's uniqueSectionCode
#     course is section's course
#     tenant is section's tenant
#     remaining metaData is hard-coded, stamper will take care of
#   add courseOffering reference to section
#   remove course reference from section

def createGuid
  chars = (0...2).map{ ('a'..'z').to_a[rand(26)] }.join
  prefix = "#{Time.new.year}#{chars}-"

  return prefix + SecureRandom.uuid
end

dbName = ARGV[0]
host, port = ARGV[1].split(':')
debug = !ARGV[2].nil?

conn = Mongo::Connection.new(host, port)

begin
  @db = conn.db(dbName)

  sectionColl = @db.collection('section')
  courseOfferingColl = @db.collection('courseOffering')

  sectionCount = sectionColl.count
  courseOfferingCount = courseOfferingColl.count
  
  puts "Sections: #{sectionCount}"
  puts "CourseOfferings: #{courseOfferingCount}"

  # TODO get this working
  #sectionColl.drop_index("i_1")

  sectionColl.find.to_a.each do |section|
    begin
      if (debug)
        puts "#### Section before ########################################################"
        pp section
        puts "#### End section before ####################################################"
      end

      section_id = section['_id']
      puts "Migrating section #{section_id}"

      # TODO add error checking on these
      sectionBody = section['body']
      if (sectionBody.nil?)
        puts "Section has no body, skipping"
        next
      end

      section_sessionId = sectionBody['sessionId']
      section_schoolId = sectionBody['schoolId']
      
      section_courseId = sectionBody['courseId']
      if (section_courseId.nil? || section_courseId.empty?)
        puts "Section has no courseId, skipping"
        next
      end

      section_uniqueSectionCode = sectionBody['uniqueSectionCode']
      if (section_uniqueSectionCode.nil? || section_uniqueSectionCode.empty?)
        puts "Section has no uniqueSectionCode, skipping"
        next
      end

      sectionMetadata = section['metaData']
      section_tenantId = sectionMetadata['tenantId']
      section_updated = sectionMetadata['updated']
      section_created = sectionMetadata['created']

      courseOffering_id = createGuid

      courseOffering = {
        '_id' => courseOffering_id,
        'type' => "courseOffering",
        'body' => {
          'schoolId' => section_schoolId,
          'courseId' => section_courseId,
          'sessionId' => section_sessionId,
          'localCourseCode' => section_uniqueSectionCode
        },
        'metaData' => {
          'tenantId' => section_tenantId,
          'updated' => section_updated,
          'created' => section_created
        }
      }

      # save courseOffering
      courseOfferingColl.save(courseOffering)

      # save section
      sectionBody['courseOfferingId'] = courseOffering_id
      sectionBody.delete('courseId')
      sectionColl.save(section)
   
      if (debug)
        puts "#### CourseOffering ########################################################"
        pp courseOffering
        puts "#### End courseOffering ####################################################"

        puts "#### Section after #########################################################"
        pp section
        puts "#### End section after #####################################################"
      end
    rescue Exception => e
      puts "Failed to migrate document: #{section}"
      puts e
      next
    end
  end

  sectionCount = sectionColl.count
  courseOfferingCount = courseOfferingColl.count
  
  puts "Sections: #{sectionCount}"
  puts "CourseOfferings: #{courseOfferingCount}"

rescue Exception => e
  $stderr.print "Migration failed: #{e}"
  conn.close
  raise
end

