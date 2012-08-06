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

module Eventbus
  require 'systemu'

  SLI_HOME = "/Users/joechung/gitrepo/sli"
  HADOOP_HOME = "/usr/local/hadoop"
  MATH_HIGHEST_EVER = "#{SLI_HOME}/POC/aggregation/Math-HighestEver/target/Math-HighestEver-1.0-SNAPSHOT-job.jar"
  HADOOP_EXEC = "#{HADOOP_HOME}/bin/hadoop"

  class HadoopJobRunner

    # implemented because JobScheduler expects it 
    def running_jobs
      list_jobs
    end

    def execute_job(job)
      command = "#{HADOOP_EXEC} jar #{SLI_HOME}#{job['jar']}"
      puts "running '#{command}'"
      status, stdout, stderr = systemu command
      puts "finished '#{command}', status = #{status}"
    end

    def list_jobs
      status, stdout, stderr = systemu "#{HADOOP_EXEC} job -list"
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
      puts "Job ID = #{job[:job_id]}, State = #{job[:state]}, Start Time = #{job[:start_time]}, User Name = #{job[:user_name]}, Priority = #{job[:priority]}, SchedulingInfo = #{job[:scheduling_info]}"
    end
  end
end

# Thread.new do
#   loop do
#     sleep 5
#     if(list_jobs.size == 0)
#       puts "No hadoop job found"
#     else
#       list_jobs.each do |job|
#         print_job(job)
#       end
#     end

#   end
# end

# runner = HadoopJobRunner.new
# runner.execute_job MATH_HIGHEST_EVER
# sleep
