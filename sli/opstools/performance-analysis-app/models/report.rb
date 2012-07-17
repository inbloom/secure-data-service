require 'rubygems'
require 'mongo'
require "set"
require 'date'
require 'logger'
require 'json'
require 'yaml'

class Report
  
  def self.generate_latest_report
    initialize_parameters
    buildTag = ""
    @db['apiResponse'].find.sort([['body.Date',-1],['body.endTime',-1]]).limit(1).each do |record|
       buildTag = record['body']['buildNumber']
    end
    finalQuery = {"_id.build_tag"=>buildTag}
    create_report('end_point',finalQuery)
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
      create_report('url',query,'apiResponse')
    end

  def self.initialize_parameters
      environment = ENV['RACK_ENV']
      app_config = YAML.load_file(settings.root+'/config/config.yml')[environment]
      terminates = false
      database = app_config["database"]
      host = app_config["host"]
      port = app_config["port"]
      puts host
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

              if collection == 'apiResponse'
                hAxisVal = record['body'][hAxis]
                responseTime = record['body']['responseTime']
              else
                hAxisVal = record['_id'][hAxis]
                responseTime = record['value']['avg_time']
              end
              response = response + @row%[hAxisVal,responseTime.to_s] +','
            end
          end
          response = response[0..-2]+ "]}"
          response
  end
end
