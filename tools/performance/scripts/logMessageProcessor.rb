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

######################################
# WIP: Requires changing API logging #
# Eventually could insert records    #
# into mongo from API logs           #
######################################

require 'rubygems'
require 'json'
require 'pp'
require 'mongo'
 
class LogMessageProcessor

  def parseSessionData(line)
    sessionRegex = Regexp.new(/APICallLogger \[(.*?)@(.*?)\]/)
    results = []
    line.scan(sessionRegex) do |user, realm|
      results.push(user)
      results.push(realm)
    end
    timeRead = Time.new
    return "{\"metaData\":{\"user\":\"#{results[0]}\",\"realm\":\"#{results[1]}\",\"timeRead\":\"#{timeRead}\"}}"
  end
  
  def parseMetrics(line)
    metricsRegex = Regexp.new(/(\{ "APICall" : .*\})$/)
    metricsStr = nil
    line.scan(metricsRegex) do |metrics|
      metricsStr = metrics.to_a[0].to_s
    end
    return metricsStr
  end
  
  def processFile(clientLog)
    conn = Mongo::Connection.new("localhost")
    db = conn.db("test")
    coll = db.collection("apiCall")
    coll.remove
  
    file = File.new(clientLog, "r")
    lineRegex = Regexp.new(/"APICall" : /)
    while (entry = file.gets)
      entry.chomp!
      if (entry.match(lineRegex))
        metadata = parseSessionData(entry)
        metadataJson = JSON.parse(metadata)
        metrics = parseMetrics(entry)
        metricsJson = JSON.parse(metrics)
        record = {"call" => metricsJson['APICall'], "metaData" => metadataJson['metaData']}
        pp record
        coll.insert(record)
      end
    end
    file.close
    conn.close
  end
  
end

logMessageProcessor = LogMessageProcessor.new()
logMessageProcessor.processFile(ARGV[0])

