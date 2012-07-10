require 'optparse'
require 'logDownloader'
require 'remoteLogReducer'
require 'apiCallExecutor'
require 'logAnalyzer'
require 'remoteLogMarker'
require 'pp'

class APIBenchmarker
  
  def initialize(benchmarkProps)
    @apiUser = benchmarkProps['API_LOGIN_USER']
    @apiUserRealm = benchmarkProps['API_LOGIN_REALM']
    @apiAcceptFormat = benchmarkProps['API_FORMAT']

    @apiServer = benchmarkProps['API_SERVER']
    @apiServerUser = benchmarkProps['API_SERVER_SSH_USER']
    @apiServerPassword = benchmarkProps['API_SERVER_SSH_PASSWORD']

    @mongoServer = benchmarkProps['MONGO_SERVER']
    @mongoDatabase = benchmarkProps['MONGO_DATABASE']

    @benchmarkBatchLog = benchmarkProps['BENCHMARK_LOG_BATCH']
    @benchmarkAPILog = benchmarkProps['BENCHMARK_LOG_API']
    @benchmarkResults = benchmarkProps['BENCHMARK_RESULTS']

    @inputFile = benchmarkProps['INPUT_FILE']
    @batchId = benchmarkProps['BENCHMARK_BATCH_ID']
    
    @apiOriginalLog = benchmarkProps['API_LOG_ORIGINAL']
    @apiReducedLog = benchmarkProps['API_LOG_REDUCED']
    @apiLogSearchPattern = benchmarkProps['API_LOG_SEARCH_PATTERN']
    @apiLogMarker = benchmarkProps['API_LOG_SEARCH_MARKER'] + " " + @batchId

    if benchmarkProps['NUM_ITERATIONS'].nil?
      @numIterations = 1
    else
      @numIterations = benchmarkProps['NUM_ITERATIONS'].to_i
    end
  end

  def benchmark()
    timestamp "Starting benchmarking process"

    timestamp "Input file: #{@inputFile}"
    timestamp "Batch id: #{@batchId}"
    timestamp "Number of iterations: #{@numIterations}"
    timestamp "API user: #{@apiUser}"
    timestamp "API user realm: #{@apiUserRealm}"

    timestamp "Preparing remote API log"
    remoteLogMarker = RemoteLogMarker.new(@apiServer, @apiServerUser, @apiServerPassword)
    remoteLogMarker.markRemoteLog(@apiOriginalLog, @apiLogMarker)

    timestamp "Executing API calls"
    apiCallExecutor = APICallExecutor.new(@apiUser, @apiUserRealm, @apiAcceptFormat)
    i = 1
    while i <= @numIterations do
      timestamp "Starting iteration #{i}"
      apiCallExecutor.getEachInFile(@inputFile, @benchmarkBatchLog)
      i += 1
    end

    timestamp "Reducing remote API log"
    remoteLogReducer = RemoteLogReducer.new(@apiServer, @apiServerUser, @apiServerPassword)
    remoteLogReducer.reduceRemoteLog(@apiOriginalLog, @apiReducedLog, @apiLogMarker, @apiLogSearchPattern)

    timestamp "Downloading API log"
    logDownloader = LogDownloader.new(@apiServer, @apiServerUser, @apiServerPassword)
    logDownloader.downloadLog(@apiReducedLog, @benchmarkAPILog)

    timestamp "Analyzing API log"
    logAnalyzer = LogAnalyzer.new(@inputFile, @benchmarkAPILog, @benchmarkBatchLog, @batchId, @numIterations, @benchmarkResults)
    logAnalyzer.analyze()

    timestamp "Ending benchmarking process"
  end

  def timestamp(line)
    time = Time.now.strftime("[%Y%m%d_%H%M%S]")
    puts "#{time} #{line}"
  end
end


# Start with environment properties
props = ENV

OptionParser.new do |opts|
  # input file
  opts.on("-i", "--input file", "Input file") do |value|
    props['INPUT_FILE'] = value
  end

  # batch id
  opts.on("-b", "--batch identifier", "Batch job identifier") do |value|
    props['BENCHMARK_BATCH_ID'] = value
  end

  # API user
  opts.on("-u", "--user name", "API user") do |value|
    props['API_LOGIN_USER'] = value
  end

  # API realm
  opts.on("-r", "--realm for user", "API user realm") do |value|
    props['API_LOGIN_REALM'] = value
  end

  # number of iterations
  opts.on("-n", "--num iterations", "Number of iterations") do |value|
    props['NUM_ITERATIONS'] = value
  end
end.parse!

apiBenchmarker = APIBenchmarker.new(props)
apiBenchmarker.benchmark()

