=begin

Copyright 2012 Shared Learning Collaborative, LLC

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

require 'rubygems'
require 'pp'
require_relative 'logMarker'
require_relative 'apiCallExecutor'
require_relative 'logReducer'
require_relative 'logDownloader'
require_relative 'logAnalyzer'

class APIBenchmarker

  def initialize(benchmarkProps)
    @apiUser = benchmarkProps['API_LOGIN_USER']
    @apiUserRealm = benchmarkProps['API_LOGIN_REALM']
    @apiAcceptFormat = benchmarkProps['API_FORMAT']

    @apiServer = benchmarkProps['API_SERVER']
    @apiResource = benchmarkProps['API_RESOURCE']
    @apiServerInstance = benchmarkProps['API_SERVER_INSTANCE']
    @apiServerUser = benchmarkProps['API_SERVER_SSH_USER']
    @apiServerPassword = benchmarkProps['API_SERVER_SSH_PASSWORD']

    @apiServer == 'localhost' ? @apiMode = 'local' : @apiMode = 'remote'

    @mongoServer = benchmarkProps['MONGO_SERVER']
    @mongoDatabase = benchmarkProps['MONGO_DATABASE']

    @benchmarkBatchLog = benchmarkProps['BENCHMARK_LOG_BATCH']
    @benchmarkAPILog = benchmarkProps['BENCHMARK_LOG_API']
    @benchmarkResults = benchmarkProps['BENCHMARK_RESULTS']

    @inputFile = benchmarkProps['INPUT_FILE']
    @batchId = benchmarkProps['BENCHMARK_BATCH_ID']
    @batchId = "default_batch_id" if @batchId.nil?

    @apiOriginalLog = benchmarkProps['API_LOG_ORIGINAL']
    @apiReducedLog = benchmarkProps['API_LOG_REDUCED']
    @apiLogSearchPattern = benchmarkProps['API_LOG_SEARCH_PATTERN']

    marker = benchmarkProps['API_LOG_SEARCH_MARKER']
    marker = "DEFAULT MARKER" if marker.nil?
    @apiLogMarker = marker + " " + @batchId

    if benchmarkProps['NUM_ITERATIONS'].nil?
      @numIterations = 1
    else
      @numIterations = benchmarkProps['NUM_ITERATIONS'].to_i
    end
  end

  def benchmark()
    timestamp "Starting benchmarking process"

    if (!@apiResource.nil?)
      timestamp "Resource: #{@apiResource}"
    else
      timestamp "Input file: #{@inputFile}"
    end

    timestamp "Batch id: #{@batchId}"
    timestamp "Number of iterations: #{@numIterations}"
    timestamp "API user: #{@apiUser}"
    timestamp "API user realm: #{@apiUserRealm}"

    timestamp "Preparing API log, #{@apiMode} mode"
    if (@apiMode == 'local')
      logMarker = LogMarker::LocalLogMarker.new
    else
      logMarker = LogMarker::RemoteLogMarker.new(@apiServer, @apiServerUser, @apiServerPassword)
    end
    logMarker.markLog(@apiOriginalLog, @apiLogMarker)

    timestamp "Executing API calls against #{@apiServerInstance}"
    apiCallExecutor = APICallExecutor.new(@apiUser, @apiUserRealm, @apiAcceptFormat, @apiServerInstance)
    i = 1
    while i <= @numIterations do
      timestamp "Starting iteration #{i}"
      # if single resource is defined, perform GET
      if (!@apiResource.nil?)
        apiCallExecutor.get(@apiResource, @benchmarkBatchLog)
      else # otherwise, perform GET on every resource in the input file
        apiCallExecutor.getEachInFile(@inputFile, @benchmarkBatchLog)
      end
      i += 1
    end

    timestamp "Reducing API log, #{@apiMode} mode"
    if (@apiMode == 'local') 
      logReducer = LogReducer::LocalLogReducer.new
      # In local mode, can reduce the log directly into it's final local version
      logReducer.reduceLog(@apiOriginalLog, @benchmarkAPILog, @apiLogMarker, @apiLogSearchPattern)
    else
      logReducer = LogReducer::RemoteLogReducer.new(@apiServer, @apiServerUser, @apiServerPassword)
      # In remote mode, need to reduce the log to an intermediary remote reduced version
      logReducer.reduceLog(@apiOriginalLog, @apiReducedLog, @apiLogMarker, @apiLogSearchPattern)
    
      timestamp "Downloading API log"
      logDownloader = LogDownloader.new(@apiServer, @apiServerUser, @apiServerPassword)
      # Now download to the final local version
      logDownloader.downloadLog(@apiReducedLog, @benchmarkAPILog)
    end

    timestamp "Analyzing API log"
    logAnalyzer = LogAnalyzer.new(@benchmarkAPILog, @benchmarkBatchLog, @numIterations, @benchmarkResults)
    stats = logAnalyzer.analyze()

    timestamp "Ending benchmarking process"

    return stats
  end

  def timestamp(line)
    time = Time.now.strftime("[%Y%m%d_%H%M%S]")
    puts "#{time} #{line}"
  end
end

