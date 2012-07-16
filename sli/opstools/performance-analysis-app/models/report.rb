require 'rubygems'
require 'mongo'
require "set"
require 'date'
require 'logger'
require 'json'

class Report
  
  def self.generate_latest_report
    initialize_parameters
    query = {"_id.build_tag"=>"jenkins-api-27"}
    create_report('end_point',query)
  end

  def self.generate_end_point_report(endPoint)
    initialize_parameters
    query = {"_id.end_point"=> "/"+endPoint[0].to_s}
    create_report('build_tag',query)
  end
  def self.generate_end_point_report_for_build(params)
      urlParams = params[0].to_s
      index = urlParams.index('/buildTag')
      endPoint =  urlParams[0..(index-1)]
      buildTag = urlParams[(index+10)..-1]
      initialize_parameters
      query = {"body.resource"=> "/"+endPoint,"body.buildNumber"=>buildTag}
      create_report('build_tag',query,'apiResponse')
    end

  def self.initialize_parameters
      terminates = false
      database = 'apiPerf'
      host = 'localhost'
      port = '27017'
      connection = Mongo::Connection.new(host, port, :pool_size => 10, :pool_timeout => 25, :safe => {:wtimeout => 500})
      log = Logger.new(STDOUT)
      log.level = Logger::ERROR

      @db = connection[database]
      @count = 0
      @basic_options = {:timeout => false, :batch_size => 100}
      @log = Logger.new(STDOUT)
      @col = '"cols": [{"label": "%s", "type": "string"},
                     {"label": "%s", "type": "number"}],
              "rows":['
      @row = '{"c":[{"v":"%s"},{"v":%s}]}'
    end

  def self.create_report(hAxis,query,collection='apiResponse_stat')
       response = "{" + @col%["endPoint","responseTime"]

       @log.info "Iterating sections with query: "+query.to_s

          @db[collection].find(query, @basic_options) do |cur|
            cur.each do |record|
              hAxisVal = record['_id'][hAxis]
              responseTime = record['value']['avg_time']
              response = response + @row%[hAxisVal,responseTime.to_s] +','
            end
          end
          response = response[0..-2]+ "]}"
          response
  end
end
