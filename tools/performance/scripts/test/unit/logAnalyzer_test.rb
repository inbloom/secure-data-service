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

require_relative '../../logAnalyzer.rb'
require 'test/unit'

class LogAnalyzerTest < Test::Unit::TestCase
  
  def test_analyze
    apiLog = '../resources/reduced_apicall.log'
    clientLog = '../resources/client.log'
    numIterations = 10
    @resultsFile = "results_#{@timestamp}.csv"

    analyzer = LogAnalyzer.new(apiLog, clientLog, numIterations, @resultsFile)
    results = analyzer.analyze

    expectedResults = {
      "http://local.slidev.org:8080/api/rest/v1/test_call1" => {
        "NUM_CALLS" => 10,
        "AVG_TIME" => 14.5,
        "MAX_TIME" => 19,
        "MIN_TIME" => 10,
        "PERC_90" => 18.5,
        "STD_DEV" => 3.0276503540974917,
        "RESP_SIZE" => 1024,
        "PERC_STD_DEV" => 20.880347269637873,
        "WGT_TOTAL_TIME" => 14.5
      },
      "http://local.slidev.org:8080/api/rest/v1/test_call2" => {
        "NUM_CALLS" => 10,
        "AVG_TIME" => 24.5,
        "MAX_TIME" => 29,
        "MIN_TIME" => 20,
        "PERC_90" => 28.5,
        "STD_DEV" => 3.0276503540974917,
        "RESP_SIZE" => 2048,
        "PERC_STD_DEV" => 12.3577565473367,
        "WGT_TOTAL_TIME" => 24.5
      },
      "http://local.slidev.org:8080/api/rest/v1/test_call3" => {
        "NUM_CALLS" => 10,
        "AVG_TIME" => 34.5,
        "MAX_TIME" => 39,
        "MIN_TIME" => 30,
        "PERC_90" => 38.5,
        "STD_DEV" => 3.0276503540974917,
        "RESP_SIZE" => 4096,
        "PERC_STD_DEV" => 8.775798127818817,
        "WGT_TOTAL_TIME" => 34.5
      }
    } 

    assert(results == expectedResults, "Incorrect results\nExpected: #{expectedResults}\nActual: #{results}")

    checksum = Digest::MD5.new.hexdigest(File.read(@resultsFile))
    expectedChecksum = Digest::MD5.new.hexdigest(File.read('../resources/results.csv'))

    assert(checksum == expectedChecksum, "Results csv didn't match expected output")

    # remove results file
    cleanup
  end
  
  protected

  def setup
    time = Time.new
    @timestamp = "#{time.year}#{time.month}#{time.day}_#{time.hour}#{time.min}#{time.sec}_#{time.usec}"
  end

  def cleanup
    if (File.exists?(@resultsFile))
      File.delete(@resultsFile)
    end
  end
end
