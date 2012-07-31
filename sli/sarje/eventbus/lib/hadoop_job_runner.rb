require 'systemu'

SLI_HOME = "/Users/joechung/gitrepo/sli"
HADOOP_HOME = "/usr/local/hadoop"
MATH_HIGHEST_EVER = "#{SLI_HOME}/POC/aggregation/Math-HighestEver/target/Math-HighestEver-1.0-SNAPSHOT-job.jar"
HADOOP_EXEC = "#{HADOOP_HOME}/bin/hadoop"

class HadoopJobRunner
  def execute_job(job)
    Thread.new do
      command = "#{HADOOP_EXEC} jar #{job}"
      puts "running '#{command}'"
      status, stdout, stderr = systemu command
      puts "finished '#{command}'"
    end
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

Thread.new do
  loop do
    sleep 5
    if(list_jobs.size == 0)
      puts "No hadoop job found"
    else
      list_jobs.each do |job|
        print_job(job)
      end
    end

  end
end

execute_job MATH_HIGHEST_EVER
sleep
