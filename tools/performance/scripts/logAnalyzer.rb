require 'rubygems'
require 'statsample'
require 'json'

class LogAnalyzer

  def initialize(originalInputFileName, apiLog, clientLog, batchId, numIterations, resultFileName)
    @originalInputFileName = originalInputFileName
    @apiLog = apiLog
    @clientLog = clientLog
    @batchId = batchId
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
    
  def analyze()  
    @originalInputFileName = File.basename(@originalInputFileName, '.*')
    
    puts "Analyzing API log: #{@apiLog}"
    puts "Using client batch log: #{@clientLog}"
    
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
          array_ref.push(time.to_i)
        end
      end
      file.close
    rescue => err
      puts "Exception: #{err}"
      err
    end
    
    #printTimeArrayMap(timeArrayMap)
    
    
    responseSizeMap = {}
    
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
    resultFile.write("Call,Number,Response time (ms),Max time, Min time,90th Percentile,Standard deviation,Response size (bytes),Percent standard deviation, Weighted total time\n")
    
    totalTime = 0
    
    timeArrayMap.each do |key, timeArrayRef|
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
    
      resultFile.write("\"#{key}\",#{numCalls},#{averageTime},#{max},#{min},#{percentile90},#{std},#{responseSize},#{percStdDev},#{weightedTotalTime}\n")
      totalTime += weightedTotalTime
    end
    
    resultFile.write(",,,,,,,,,#{totalTime}\n")
    resultFile.close
  end
end
