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

require_relative '../../../../../../../tools/performance/scripts/apiBenchmarker'
require_relative '../../../../../../../tools/performance/scripts/databaseUtils'

require 'pp'
require 'fileutils'

require_relative '../../../utils/sli_utils.rb'
require_relative '../../entities/common.rb'
require_relative '../../utils/api_utils.rb'

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<([^"]*)>$/ do |human_readable_id|

  #general
  id = "AVG_TIME"                               if human_readable_id == "AVERAGE TIME STAT"
  id = "PERC_90"                                if human_readable_id == "90TH PERCENTILE STAT"

  #return the translated value
  id
end

############################################################
# STEPS: BEFORE
############################################################

Before do
  # bootstrap system env variables
  @props = ENV

  @batchLogRoot = '/storage/logs/performance'
  if (!File.exists?(@batchLogRoot))
    FileUtils.mkdir_p(@batchLogRoot)
    FileUtils.chmod(0777, @batchLogRoot)
  end

  @props['API_FORMAT'] = 'application/vnd.slc+json'
  @props['API_SERVER'] = 'localhost'

  # allows you to override locally
  @props['API_SERVER_INSTANCE'] = 'https://local.slidev.org/api/rest/v1' if @props['API_SERVER_INSTANCE'].nil?

  # this prop will likely be the only one overridden from the env
  @props['API_LOG_ORIGINAL'] = '/storage/logs/apicall.log' if @props['API_LOG_ORIGINAL'].nil?

  @props['API_LOG_SEARCH_PATTERN'] = 'APICall|finished in'
  @props['API_LOG_SEARCH_MARKER'] = 'START API RECORDING'

  @props['LOG_ANALYSIS_MODE'] = 'api' if @props['LOG_ANALYSIS_MODE'].nil?
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^I copy the database "([^\"]*)" to database "([^\"]*)"$/ do |sourceDb, targetDb|
  # copy source db into target db
  dbHost = 'localhost'
  copyDatabase(dbHost, sourceDb, targetDb)
end

Given /^I want to execute each call "([^\"]*)" times/ do |numIterations|
  @props['NUM_ITERATIONS'] = numIterations
end

Given /^I want to log in as "([^\"]*)" to realm "([^\"]*)"$/ do |user, realm|
  @props['API_LOGIN_USER'] = user
  @props['API_LOGIN_REALM'] = realm
end

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

When /^I execute a series of GETs on "([^"]*)"$/ do |resource|
  # kick off job, save results
  @props['API_RESOURCE'] = resource

  time = Time.new
  batchId = "#{@props['API_LOGIN_USER']}_#{time.year}#{time.month}#{time.day}_#{time.hour}#{time.min}#{time.sec}_#{time.usec}"

  @props['BENCHMARK_BATCH_ID'] = batchId
  @props['BENCHMARK_LOG_BATCH'] = "#{@batchLogRoot}/#{batchId}_client.log"
  @props['BENCHMARK_LOG_API'] = "#{@batchLogRoot}/#{batchId}_api.log"
  @props['BENCHMARK_RESULTS'] = "#{@batchLogRoot}/#{batchId}_report.csv"

 # pp @props

  benchmarker = APIBenchmarker.new(@props)
  @results = benchmarker.benchmark
  puts "#{@results}"
end

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^the "([^"]*)" should be less than (\d+) ms$/ do |statKey, threshold|
  # get actual value for statKey, ensure less than threshold

  resource = @props['API_SERVER_INSTANCE'] + @props['API_RESOURCE']
  statsMap = @results[resource]
  assert(!statsMap.nil? && !statsMap.empty?, "Results null or empty")

  value = statsMap[statKey]

  fValue = Float(value)
  fThreshold = Float(threshold)

  assert(fValue < fThreshold, "#{statKey}: #{fValue} not less than #{fThreshold}")
end


