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
    @apiSessionToken = benchmarkProps['API_SESSION_TOKEN']

    # use a token if specified and a user is not specified
    @useToken = @apiUser.nil? && !@apiSessionToken.nil?

    @apiAcceptFormat = benchmarkProps['API_FORMAT']

    @apiServer = benchmarkProps['API_SERVER']
    @apiResource = benchmarkProps['API_RESOURCE']
    @apiServerInstance = benchmarkProps['API_SERVER_INSTANCE']
    @apiServerUser = benchmarkProps['API_SERVER_SSH_USER']
    @apiServerPassword = benchmarkProps['API_SERVER_SSH_PASSWORD']

    @useLocalApi = @apiServer == 'localhost'

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

    logAnalysisMode = benchmarkProps['LOG_ANALYSIS_MODE']
    # analyze the client log by default, only analyze api log if explicitly specified
    @analyzeClientLog = logAnalysisMode.nil? || logAnalysisMode != 'api'
  end

  def benchmark()
    timestamp "Starting benchmarking process"
    useClientLog = @logAnalysisMode == 'client'

    if (!@apiResource.nil?)
      timestamp "Resource: #{@apiResource}"
    else
      timestamp "Input file: #{@inputFile}"
    end

    timestamp "Batch id: #{@batchId}"
    timestamp "Number of iterations: #{@numIterations}"

    if (@useToken)
      timestamp "Session token: #{@apiSessionToken}"
    else
      timestamp "API user: #{@apiUser}"
      timestamp "API user realm: #{@apiUserRealm}"
    end

    # only need to mark API log if that's what we're using for analysis
    if (!@analyzeClientLog)
      markLog
    end 

    executeCalls

    # only need to reduce/download API log if that's what we're using for analysis
    if (!@analyzeClientLog)
      reduceLog
    end

    stats = analyzeLog

    timestamp "Ending benchmarking process"

    return stats
  end

  private

  def timestamp(line)
    time = Time.now.strftime("[%Y%m%d_%H%M%S]")
    puts "#{time} #{line}"
  end

  def markLog
    if (@useLocalApi)
      timestamp "Preparing API log, local mode"
      logMarker = LogMarker::LocalLogMarker.new
    else
      timestamp "Preparing API log, remote mode"
      logMarker = LogMarker::RemoteLogMarker.new(@apiServer, @apiServerUser, @apiServerPassword)
    end
    logMarker.markLog(@apiOriginalLog, @apiLogMarker)
  end

  def executeCalls
    timestamp "Executing API calls against #{@apiServerInstance}"
    authenticationDetails = {
      :user => @apiUser,
      :realm => @apiUserRealm,
      :token => @apiSessionToken
    }
    apiCallExecutor = APICallExecutor.new(authenticationDetails, @apiAcceptFormat, @apiServerInstance)
    i = 1
    while i <= @numIterations do
#      timestamp "Starting iteration #{i}"
      # if single resource is defined, perform GET
      if (!@apiResource.nil?)
        apiCallExecutor.get(@apiResource, @benchmarkBatchLog)
      else # otherwise, perform GET on every resource in the input file
        apiCallExecutor.getEachInFile(@inputFile, @benchmarkBatchLog)
      end
      i += 1
    end
  end

  def reduceLog
    if (@useLocalApi) 
      timestamp "Reducing API log, local mode"
      logReducer = LogReducer::LocalLogReducer.new
      # In local mode, can reduce the log directly into it's final local version
      logReducer.reduceLog(@apiOriginalLog, @benchmarkAPILog, @apiLogMarker, @apiLogSearchPattern)
    else
      timestamp "Reducing API log, remote mode"
      logReducer = LogReducer::RemoteLogReducer.new(@apiServer, @apiServerUser, @apiServerPassword)
      # In remote mode, need to reduce the log to an intermediary remote reduced version
      logReducer.reduceLog(@apiOriginalLog, @apiReducedLog, @apiLogMarker, @apiLogSearchPattern)
      
      downloadLog
    end
  end

  def downloadLog
    timestamp "Downloading API log"
    logDownloader = LogDownloader.new(@apiServer, @apiServerUser, @apiServerPassword)
    # Now download to the final local version
    logDownloader.downloadLog(@apiReducedLog, @benchmarkAPILog)
  end

  def analyzeLog
    stats = {}
    logAnalyzer = LogAnalyzer.new(@benchmarkBatchLog, @numIterations, @benchmarkResults)
    if (@analyzeClientLog)
      timestamp "Will analyze client log"
      stats = logAnalyzer.analyzeClientLog()
    else 
      timestamp "Will analyze API log"
      stats = logAnalyzer.analyzeAPILog(@benchmarkAPILog)
    end
    return stats
  end

end

