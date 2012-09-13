=begin

Copyright 2012 Shared Learning Collaborative, LLC

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

class GenerateReport
  attr_accessor :count, :db, :log, :parent_ed_org_hash
  def initialize(db, logger = nil)
    @db = db
    @count = 0
    @basic_options = {:timeout => false, :batch_size => 100}
    @log = logger || Logger.new(STDOUT)
    @col = '"cols": [{label: %s, type: "string"},
                   {label: %s, type: "number"}],
            "rows":['
    @row = '{"c":[{"v":"%s"},{"v":%s}]}'
  end

  def start
    puts create_report_endpoint
  end

  def create_report_endpoint
     response = "{" + @col%["endPoint","responseTime"]
     @log.info "Iterating sections with query: {}"
        puts "{"
        @db['apiResponse_stat'].find({}, @basic_options) do |cur|
          cur.each do |record|
            endPoint = record['_id']['end_point']
            responseTime = record['value']['avg_time']
            response = response + @row%[endPoint,responseTime.to_s] +','
          end
        end
        response = response+ "]}"
        response
  end

end
