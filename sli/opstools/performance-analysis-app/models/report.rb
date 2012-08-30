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
    @db['apiResponse'].find({'processed'=>true}).sort([['body.Date',-1],['body.endTime',-1]]).limit(1).each do |record|
       buildTag = record['body']['buildNumber']
    end
    finalQuery = {"build_tag"=>buildTag}

    create_report('end_point','avg',finalQuery)
  end

  def self.generate_end_point_report(endPoint)
    initialize_parameters
    query = {"end_point"=> "/"+endPoint[0].to_s}
    create_report('build_tag','avg',query)
  end
  def self.generate_end_point_report_for_build(params)
      urlParams = params[0].to_s
      index = urlParams.index('/buildTag')
      endPoint =  urlParams[0..(index-1)]
      buildTag = urlParams[(index+10)..-1]
      initialize_parameters
      query = {"body.resource"=> "/"+endPoint,"body.buildNumber"=>buildTag}
      create_report('url','responseTime',query,'apiResponse')
    end
  def self.get_single_call_record(params)
    urlParams = params[0].to_s
    puts urlParams
    doc = @db['apiResponse'].find_one({'_id'=>urlParams})
    doc['body'].to_json()
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
                     {"label": "%s", "type": "number"},
                     {"label":"%s","type":"number"},
                     {"label":"%s","type":"number"}
                     ],
              "rows":['
      @row = '{"c":[{"v":"%s"},{"v":%s},{"v":%s},{"v":%s}],"p": {"id":"%s"}}'
    end

  def self.create_report(hAxis,yAxis,query,collection='apiResponseStat')
       response = "{" + @col%["endPoint","responseTime","response time for 80% api call","db call count"]

       bench_mark = String.new
       avarage = String.new
       if collection == 'apiResponse'
         doc = @db['apiResponseStat'].find_one({'$and'=>[{'end_point'=>query['body.resource']},{'build_tag'=>query['body.buildNumber']}]})
         bench_mark=doc['br']
         avarage = doc['avg']
       end
          @db[collection].find(query, @basic_options) do |cur|
            cur.each do |record|
              dbCount = 0

              if collection == 'apiResponse'
                hAxisVal = record['body'][hAxis]
                responseTime = record['body'][yAxis]
                cutOff = bench_mark
                if record['body']['dbHitCount'].nil? == false
                  dbCount = record['body']['dbHitCount']
                end
              else
                hAxisVal = record[hAxis]
                responseTime = record[yAxis]
                cutOff = record["br"]
              end
              id = record["_id"]
              response = response + @row%[hAxisVal,responseTime.to_s,cutOff.to_s,dbCount.to_s,id.to_s] +','
            end
          end
          response = response[0..-2]+ "]}"
          response
  end
end
