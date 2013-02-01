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
require 'statsample'
require 'json'


class LogAnalyzer

  def initialize(clientLog, numIterations, resultFileName)
    @clientLog = clientLog
    @numIterations = numIterations.to_i
    @resultFileName = resultFileName
  end

  def printTimeArrayMap(timeArrayMap)
    timeArrayMap.each do |key, values|
      puts key
      values.each do |value|
        puts "  #{value}"
      end
    end
  end

  def analyzeClientLog
    timestamp "Analyzing client log: #{@clientLog}"

    timeArrayMap = {}

    # sample line
    # { "resource" : "http://local.slidev.org:8080/api/rest/v1/cohorts", "code" : 200, "length" : 12861, "time" : "23.442" }
    begin
      file = File.new(@clientLog, "r")
      regex = Regexp.new(/.*"resource" : "(http.*?)", .* "time" : "(.*?)".*/)
      while (entry = file.gets)
        entry.chomp!
        entry.scan(regex) do |md|
          url = md[0]
          time = md[1]
    
          key = url
          if (!timeArrayMap[key])
            arr = []
            timeArrayMap[key] = arr
          end
    
          array_ref = timeArrayMap[key]
          array_ref.push(time.to_f)
        end
      end
      file.close
    rescue => err
      puts "Exception: #{err}"
      err
    end

    analyze(timeArrayMap)
  end

  def analyzeAPILog(apiLog)
    @apiLog = apiLog

    timestamp "Analyzing API log: #{@apiLog}"
    
    timeArrayMap = {}
    
    begin
      file = File.new(@apiLog, "r")
      regex = Regexp.new(/\[.*\] - (http[^ ]*) finished in ([0123456789]*) ms/)
      while (entry = file.gets)
        entry.chomp!
        entry.scan(regex) do |md|
          url = md[0]
          time = md[1]
    
          key = url
          if (!timeArrayMap[key])
            arr = []
            timeArrayMap[key] = arr
          end
    
          array_ref = timeArrayMap[key]
          array_ref.push(time.to_f)
        end
      end
      file.close
    rescue => err
      puts "Exception: #{err}"
      err
    end

    analyze(timeArrayMap)
  end

  private

  def timestamp(line)
    time = Time.now.strftime("[%Y%m%d_%H%M%S]")
    puts "#{time} #{line}"
  end

  def analyze(timeArrayMap)  
    responseSizeMap = {}
    
    timestamp "Using client batch log: #{@clientLog}"
    begin
      file = File.new(@clientLog, "r")
      regex = Regexp.new(/^\{/)
      while (entry = file.gets)
        entry.chomp!
        if (entry.match(regex))
          message = JSON.parse(entry)
          url = message['resource']
          responseSize = message['length']
          if (responseSizeMap.has_key?(url))
            oldResponseSize = responseSizeMap[url]
            if (responseSize != oldResponseSize)
              raise "Error: different response sizes for same resource"
            end
          else
            responseSizeMap[url] = responseSize
          end
        end
      end
      file.close
    rescue => err
      puts "Exception: #{err}"
      err
    end
    
    puts "Results written to #{@resultFileName}"
    resultFile = File.new(@resultFileName, "w")
    
    # analysis
    returnMap = {}
    resultFile.write("Call,Number,Response time (ms),Max time, Min time,90th Percentile,Standard deviation,Response size (bytes),Percent standard deviation, Weighted total time\n")
    
    totalTime = 0
    
    timeArrayMap.each do |key, timeArrayRef|
      callStats = {}
      
      numCalls = timeArrayRef.length
      v = timeArrayRef.to_vector(:scale)
      averageTime = v.mean
      
      if (@numIterations == 1)
        std = 0
      else
        std = v.sd
      end
      max = v.max
      min = v.min
      percentile90 = v.percentil(90)
      responseSize = responseSizeMap[key]
      weightedTotalTime = (numCalls / @numIterations) * averageTime
      percStdDev = (std / averageTime) * 100
   
      callStats['NUM_CALLS'] = numCalls
      callStats['AVG_TIME'] = averageTime
      callStats['MAX_TIME'] = max
      callStats['MIN_TIME'] = min
      callStats['PERC_90'] = percentile90
      callStats['STD_DEV'] = std
      callStats['RESP_SIZE'] = responseSize
      callStats['PERC_STD_DEV'] = percStdDev
      callStats['WGT_TOTAL_TIME'] = weightedTotalTime

      resultFile.write("\"#{key}\",#{numCalls},#{averageTime},#{max},#{min},#{percentile90},#{std},#{responseSize},#{percStdDev},#{weightedTotalTime}\n")
      totalTime += weightedTotalTime

      returnMap[key] = callStats
    end
    
    resultFile.write(",,,,,,,,,#{totalTime}\n")
    resultFile.close

    return returnMap
  end
end


# TEST
#la = LogAnalyzer.new("/storage/logs/performance/rrogers_2012716_84129_349841_client.log", "5", "/tmp/test.csv") 
#la.analyzeClientLog



