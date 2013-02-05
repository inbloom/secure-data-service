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

module Eventbus
  require 'systemu'

  class HadoopJobRunner
    def initialize(config = {}, logger = nil)
      @hadoop_jars = config[:hadoop_jars]
      @logger = logger if logger
      @hadoop_home = config[:hadoop_home]
      @hadoop_exec = "#{@hadoop_home}/bin/hadoop"
    end

    def execute_job(job)
      if job['test'] 
         command = "#{job['test']}"
      else
         command = "#{@hadoop_exec} jar #{@hadoop_jars}#{job['jar']}"
      end
      @logger.info "running '#{command}'" if @logger
      status, stdout, stderr = systemu command
      #puts "finished '#{command}', status = #{status}"
      @logger.info "finished '#{command}', status = #{status}" if @logger
    end

    def list_jobs
      status, stdout, stderr = systemu "#{@hadoop_exec} job -list"
      lines = stdout.split(/\n/)

      jobs = []
      if(lines.size > 2)
        lines[2..-1].each do |line|
          job_array = line.split(/\t/)
          jobs << create_job(job_array)
        end
      end

      return jobs
    end

    def create_job(job_array)
      job = {}
      job[:job_id] = job_array[0]
      job[:state] = job_array[1]
      job[:start_time] = job_array[2]
      job[:user_name] = job_array[3]
      job[:priority] = job_array[4]
      job[:scheduling_info] = job_array[5]
      return job
    end

    def print_job(job)
      #puts "Job ID = #{job[:job_id]}, State = #{job[:state]}, Start Time = #{job[:start_time]}, User Name = #{job[:user_name]}, Priority = #{job[:priority]}, SchedulingInfo = #{job[:scheduling_info]}"
      @logger.info "Job ID = #{job[:job_id]}, State = #{job[:state]}, Start Time = #{job[:start_time]}, User Name = #{job[:user_name]}, Priority = #{job[:priority]}, SchedulingInfo = #{job[:scheduling_info]}" if @logger
    end
  end
end
