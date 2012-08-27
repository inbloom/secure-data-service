require 'mongo'
require 'json'
require_relative '../../ingestion/features/step_definitions/ingestion_steps.rb'


Before do
  @conn = Mongo::Connection.new("127.0.0.1", 27017)
  assert(@conn != nil)
  @db = @conn.db("sli")
  assert(@db != nil)
end

Given /^I am using the general data store$/ do
    @local_file_store_path = File.dirname(__FILE__) + "/../../../data/"
end

When /^I run the highest ever aggregation job$/ do
    `hadoop jar $SLI_HOME/POC/aggregation/mapreduce/target/mapreduce-1.0-SNAPSHOT-job.jar org.slc.sli.aggregation.jobs.student.assessment.HighestEver`
end

When /^I run the proficiency aggregation job$/ do
    `hadoop jar $SLI_HOME/POC/aggregation/mapreduce/target/mapreduce-1.0-SNAPSHOT-job.jar org.slc.sli.aggregation.jobs.school.assessment.SchoolProficiency`
end

Then /^for the (.*?) with "(.*?)" set to "(.*?)", "(.*?)" is "(.*?)"$/ do |type, query, queryValue, testKey, testValue|
    coll = @db[type]
    e = coll.find_one({query => queryValue})
    for k in testKey.split(".") do
        e = e[k]
    end
    assert(e == testValue, "#{e} is not equal to #{testValue}")
end

Then /^I see the expected number of <Collection> records for <Assessment> with score <Score> is <Count>:$/ do |data|
    data.hashes.map do |row|
        collection = row["Collection"].to_s
        assessment = row["Assessment"].to_s
        score = row["Score"].to_f
        count = row["Count"].to_i

        id = getAssessmentId(assessment)
        assert(id != nil)

        @targetColl = @db[collection]
        assert(@targetColl != nil)

        json_query = '{"calculatedValues.assessments.' + id + '.HighestEver.ScaleScore":32}'
        query  = JSON.parse( json_query )
        puts query
        c = @targetColl.find(query).count()
        assert (c != nil)

        assert(c == count, "#{c} is not equal to #{count}")
    end
end

Then /^for the student with ID <Id>, I see a highest ever score of <Score> for the <Assessment> assessment:$/ do |data|
    data.hashes.map do |row|
        studentStateId = row["Id"]
        score = row["Score"].to_f
        assessment = row["Assessment"].to_s

        id = getAssessmentId(assessment)
        assert(id != nil)

        @targetColl = @db["student"]
        assert(@targetColl != nil)

        json_query = '{"body.studentUniqueStateId":"' + studentStateId + '"}'
        query  = JSON.parse( json_query )
        entity = @targetColl.find_one(query)
        assert (entity != nil)

        assert(entity["calculatedValues"]["assessments"][id]["HighestEver"]["ScaleScore"] == score)
    end
end

Then /^I see the expected number of <Collection> records with aggregates for <Assessment> is <Count>:$/ do |data|
    data.hashes.map do |row|
        collection = row["Collection"].to_s
        assessment = row["Assessment"].to_s
        count = row["Count"].to_i

        id = getAssessmentId(assessment)
        assert(id != nil)

        @targetColl = @db[collection]
        assert(@targetColl != nil)

        json_query = '{"aggregates.assessments.' + id + '.cut_points":{"$exists":1}}'
        query  = JSON.parse( json_query )
        c = @targetColl.find(query).count()
        assert(c == count, "#{c} is not equal to #{count}")    end
end

Then /^East Daybreak Junior High has cut point <Cut> count <Count> for assessment <Assessment>:$/ do |data|
    data.hashes.map do |row|
        cut = row["Cut"].to_s
        assessment = row["Assessment"].to_s
        count = row["Count"].to_i

        id = getAssessmentId(assessment)
        assert(id != nil)

        @targetColl = @db["educationOrganization"]
        assert(@targetColl != nil)

        json_query = '{"body.educationOrgIdentificationCode.ID":"East Daybreak Junior High"}'
        query  = JSON.parse( json_query )
        entity = @targetColl.find_one(query)
        assert(entity["aggregates"]["assessments"][id]["cut_points"][cut] == count)
    end
end

def getAssessmentId(name)
    @assessmentColl = @db["assessment"]
    assert(@assessmentColl != nil)

    json_query = '{"body.assessmentIdentificationCode.ID":"' + name + '"}'
    query  = JSON.parse( json_query )
    entry = @assessmentColl.find_one(query)
    assert(entry != nil, "Failed to find assessment.")
    return entry["_id"]
end
