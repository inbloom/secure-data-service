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
require 'mongo'
require "benchmark"
require "set"
require 'date'
require 'logger'

class Analyser
  attr_accessor :count, :db, :log
  def initialize(server,port,database, logger = nil)
    @SERVER = server
    @PORT = port
    @DB = database
    @frequency_hash = Hash.new
    @build_perf_hash = Hash.new
    @stat_hash = Hash.new
    @basic_options = {:timeout => false, :batch_size => 100}
    @log = logger || Logger.new(STDOUT)
    @log.level = Logger::WARN if logger.nil?
    @key = " %s|%s" 

  end
  def create_connection
    @conn = Mongo::Connection.new(@SERVER,@PORT, :safe => {:wtimeout => 500})
    @db = @conn[@DB]
  end

  def start
    time = Time.now
    build_metrices
    finalTime = Time.now - time
    @log.info "\t Final time is #{finalTime} secs"
    puts finalTime
  end
  def build_metrices
    count = 0
    continue_exec = true
    while continue_exec
      continue_exec = false
      create_connection
      id = String.new
      begin
      @db['apiResponse'].find({'$or'=>[{'processed'=>{'$exists'=>false}},{'processed'=>false}]},@basic_options) do |cur|
          cur.each do |rec|
            id = rec['_id']
            count = count +1
            build_number = rec['body']['buildNumber']
            end_point = rec['body']['resource']
            response_time = rec['body']['responseTime']
            hash_key = @key%[build_number,end_point]
            update_frequency_hash(hash_key,response_time)
            update_stat_hash(hash_key,response_time)
            rec['processed'] = true 
            @db['apiResponse'].save rec
          end
      end
      rescue => e
        puts "Exception received after "+count.to_s+" number of records " + e.message
        puts "removing record with id #{id}"
        @db['apiResponse'].remove({'_id'=>id})
        @conn.close
        continue_exec = true 
      end
    end
    calculate_stat
    update_db!
    determine_gradual_perf_degrade
    notify
    @conn.close
    puts "Processed total "+count.to_s+" records"
  end
  def update_frequency_hash(hash_key,response_time)
      if @frequency_hash[hash_key].nil?
         @frequency_hash[hash_key] = Hash.new
         @frequency_hash[hash_key][response_time.to_i] = 1
      else
         if @frequency_hash[hash_key][response_time.to_i].nil?
           @frequency_hash[hash_key][response_time.to_i] = 1
         else
            @frequency_hash[hash_key][response_time.to_i] = @frequency_hash[hash_key][response_time.to_i] + 1
         end
      end
  end
  def update_stat_hash(hash_key,response_time)
      if @stat_hash[hash_key].nil?
         @stat_hash[hash_key] = {"Max"=>response_time.to_f,"Min"=>response_time.to_f,"Tc"=>1,"Tt"=>response_time.to_f}
      else
        max_time =@stat_hash[hash_key]["Max"].to_f
        min_time = @stat_hash[hash_key]["Min"].to_f
        total_time = @stat_hash[hash_key]["Tt"] + response_time.to_f
        total_count = @stat_hash[hash_key]["Tc"] + 1
        if response_time.to_f > max_time
          max_time = response_time.to_f
        elsif response_time.to_f < min_time
          min_time = response_time.to_f
        end
        @stat_hash[hash_key] = {"Max"=>max_time,"Min"=>min_time,"Tc"=>total_count,"Tt"=>total_time}
      end
  end
  def determine_gradual_perf_degrade
    @db.collection('apiResHistory').find({},@basic_options) do |cur|
      cur.each do |rec|
        all_build_stat = rec['buildStat']
        time = 0.0
        count = 0
        buildTag = String.new
        all_build_stat.sort.reverse.map do |key,val|
          if time <= val.to_f
            time = val.to_f
            buildTag = key
            count = 0
          else
            count = count +1
          end
          if count >10
            if @build_perf_hash[buildTag].nil?
              @build_perf_hash[buildTag] =  Array.new
            end
            @build_perf_hash[buildTag].push("Performance gradual degradation for end point #{rec['end_point']}")
            break
          end
        end
      end
    end
  end
  def update_db!
    coll =  @db.collection('apiResponseStat')
    count = 0
    begin
      @stat_hash.each { |key,value|
        count = count +1
        buildTag = value['build_tag']
        endPoint = value['end_point']
        doc = @db.collection('apiResHistory').find_one({'end_point'=>endPoint})
        if doc.nil? or doc.empty?
          doc = {'end_point'=>endPoint,'buildStat'=>{buildTag.to_s=>value['br']}}
        else
          doc['buildStat'][buildTag.to_s]=value['br']
        end
        @db.collection('apiResHistory').save doc
        coll.save value
      }
    rescue =>e
      puts "excepton receive after inserting " +count.to_s+" records with message " + e.message
    end
    puts "updated #{count.to_s} records"
  end
  def calculate_stat
    @stat_hash.each {|key,value|
      mean = value["Tt"]/value["Tc"]
      value["avg"] = mean
      freq_data = @frequency_hash[key]
      variance = 0.0
      count_p=0.8*value["Tc"].to_i
      benchmark_res = 1000
      freq_data_to_insert = Hash.new
      freq_data.sort.map do |keyf,valf|
        variance = variance + ((mean.to_f - keyf.to_f)**2)*valf
        if count_p > 0
          benchmark_res = keyf
          count_p = count_p - valf
        end
        freq_data_to_insert[keyf.to_s]=valf
      end
      variance = variance/(value["Tc"] -1)
      std_dev = Math.sqrt(variance)
      coeff_var = (std_dev/mean)*100 
      value["var"]=variance
      value["sd"]=std_dev
      value["coeffVar"] = coeff_var
      value["br"]=benchmark_res
      value["freq_chart"]=freq_data_to_insert
      key.each_line('|') { |s|
      if value['build_tag'].nil? 
         buildTag = s[0..-2]
         buildTag.strip!
         value['build_tag'] = buildTag 
      else
         endPoint = s
         value['end_point'] = endPoint
      end
      }
      generate_notification value
    }
  end
  def generate_notification(stat)
    buildTag = stat['build_tag']
    notify = "For endpoint : %s potential performance issue: "
    sendNotification = false
    if stat["coeffVar"] > 100.0
      sendNotification = true
      notify = notify + "co-efficient of variance is "+stat["coeffVar"].to_s  
    elsif (stat["Max"] - stat["Min"]) >50
      sendNotification = true
      notify = notify + "Max response time is more than 50ms than Min response time "
    elsif (stat["avg"] - stat["br"]) >5
      sendNotification = true
      notify = notify + "avg is : #{stat['avg']} and benchmarked at : #{stat['br']}"
    end 
    if sendNotification
      if @build_perf_hash[buildTag].nil?
        @build_perf_hash[buildTag] = Array.new
      end
      @build_perf_hash[buildTag].push(notify%[stat["end_point"]])
    end
  end
  def notify
    if @build_perf_hash.empty? == false
      @build_perf_hash.each {|key,val|
        puts ""
        puts red("***********Build Number #{key} ****************")
        val.each{|ep|
          if ep.include?"co-efficient"
            puts magenta("#{ep}")
          elsif ep.include?"benchmarked"
            puts yellow("#{ep}")
          elsif ep.include?"Max"
            puts blue("#{ep}")
          else
            puts "#{ep}"
          end
        }
      }
    end
  end
  def colorize(text, color_code)
      "\e[#{color_code}m#{text}\e[0m"
  end
  def red(text)
    colorize(text, 31)
  end
  def green(text)
    colorize(text, 32)
  end
  def yellow(text)
    colorize(text, 33)
  end
  def blue(text)
    colorize(text, 34)
  end
  def magenta(text)
    colorize(text, 35)
  end
end
