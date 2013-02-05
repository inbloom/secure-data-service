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
require 'optparse'
require_relative 'apiBenchmarker'

# Start with environment properties
props = ENV

OptionParser.new do |opts|
  # input file
  opts.on("-i", "--input file", "Input file") do |value|
    props['INPUT_FILE'] = value
  end

  # resource
  opts.on("-a", "--api resource", "API resource") do |value|
    props['API_RESOURCE'] = value
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
  
  # session token
  opts.on("-t", "--session token", "Session token") do |value|
    props['API_SESSION_TOKEN'] = value
  end
end.parse!

apiBenchmarker = APIBenchmarker.new(props)
apiBenchmarker.benchmark()

