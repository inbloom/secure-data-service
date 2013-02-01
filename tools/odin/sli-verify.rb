#!/usr/bin/env ruby
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

require 'digest/sha1'
require 'json'
require 'logger'
require 'mongo'

$stdout.sync = true
@log = Logger.new($stdout)
@log.level = Logger::INFO

if ARGV.length != 2
  @log.error "Usage: sli-verify.rb {tenant id} {path to manifest}"
  exit 0
end

@manifest = ARGV[1]
@dbname = Digest::SHA1.hexdigest ARGV[0]
@db = Mongo::Connection.new('localhost').db(@dbname)

@ignored_entities = ["AssessmentFamily", "BehaviorDescriptor", "DisciplineDescriptor"]

def where_stored(entity_type)
  case entity_type
  when "AssessmentItem"
    {collection: 'assessment', subdoc: ['body', 'assessmentItem']}
  when "AttendanceEvent"
    {collection: 'attendance', subdoc: ['body', 'schoolYearAttendance', :*, 'attendanceEvent']}
  when "GradebookEntry"
    {collection: 'section', subdoc: ['gradebookEntry']}
  when "LocalEducationAgency"
    {collection: 'educationOrganization', query: {type: 'localEducationAgency'}}
  when "School"
    {collection: 'educationOrganization', query: {type: 'school'}}
  when "Staff"
    {collection: 'staff', query: {type: 'staff'}}
  when 'StaffEducationOrgAssignmentAssociation'
    {collection: 'staffEducationOrganizationAssociation'}
  when "StateEducationAgency"
    {collection: 'educationOrganization', query: {type: 'stateEducationAgency'}}
  when 'StudentAssessment'
    {collection: 'student', subdoc: ['studentAssessment']}
  when 'StudentAssessmentItem'
    {collection: 'student', subdoc: ['studentAssessment', :*, 'body', 'studentAssessmentItems']}
  when 'StudentCohortAssociation'
    {collection: 'cohort', subdoc: ['studentCohortAssociation']}
  when 'StudentDisciplineIncidentAssociation'
    {collection: 'student', subdoc: ['studentDisciplineIncidentAssociation']}
  when 'StudentParentAssociation'
    {collection: 'student', subdoc: ['studentParentAssociation']}
  when 'StudentProgramAssociation'
    {collection: 'program', subdoc: ['studentProgramAssociation']}
  when 'StudentSectionAssociation'
    {collection: 'section', subdoc: ['studentSectionAssociation']}
  when "Teacher"
    {collection: 'staff', query: {type: 'teacher'}}
  when 'TeacherSectionAssociation'
    {collection: 'section', subdoc: ['teacherSectionAssociation']}
  else
    sli_entity = String.new entity_type
    sli_entity[0] = sli_entity[0].downcase
    {collection: sli_entity}
  end
end

def value_in(entity, path)
  if path.empty?
    entity
  else
    field = path[0]
    if field == :*
      entity.map{|s| value_in(s, path.drop(1))}.flatten
    else
      value_in(entity[field], path.drop(1))
    end
  end
end

def count(entity_type)
  unless @ignored_entities.include? entity_type
    where = where_stored(entity_type)
    docs = @db[where[:collection]].find(where[:query])
    if where[:subdoc].nil?
      docs.count
    else
      docs.map{|d| (value_in(d, where[:subdoc]) or []).count}.inject(:+)
    end
  end
end

passed = true
File.open(@manifest){ |file|
  JSON.parse(file.read).each{|entity, count|
    entity_count = count(entity)
    unless entity_count.nil?
      if entity_count != count then
        @log.warn "Expected #{count} #{entity} entities to be ingested, found #{entity_count}"
        passed = false
      end
    end
  }
}

if passed
  @log.info "All expected entities found"
  exit 0
else
  @log.warn "The entities above were not found --> ingestion failed."
  exit 1
end
