#!/usr/bin/env ruby

require 'json'
require 'net/http'

SMOKE_TEST={"Smoke/job/Smoke/"=>"Smoke Unit Tests","Smoke/job/SmokeTest/"=>"Smoke Acceptance Tests"}

module Jenkins
  BASE_URI="http://jenkins.slidev.org:8080/view"
  STATII={:last=>"lastBuild",:failed=>"lastFailedBuild",:successful=>"lastSuccessfulBuild",:completed=>"lastCompletedBuild"}
  BUILDS=["API","Dashboard","Ingestion","Integration","Portal","Sandbox","Tools"]

  #  Gets the job for given name and id
  def self.getJob(jobName, jobId)
    jobId = STATII[jobId] unless jobId.is_a? Integer
    resp = Net::HTTP.get_response(URI.parse("#{BASE_URI}/#{jobName}/#{jobId}/api/json"))

    if resp.code=="200"
      job=JSON.parse(resp.body)
    else
      puts 'FAIL'
      # TODO add robustness code
    end

    return job
  end

  # Obtains first failed job in the list
  def self.getFirstFailure(jobName)
    lastSuccess=self.getJob(jobName,:successful)
    return self.getJob(jobName,lastSuccess["number"]+1)
  end

  # Obtains all culprits for all currently broken jobs
  def self.getFirstFailureCulprits(jobName)
    firstFailure = self.getFirstFailure(jobName)
    return firstFailure["culprits"]
  end

  # Obtains time the build has been broken
  def self.getTimeBroken(jobName)
    firstFailure = self.getFirstFailure(jobName)
    time = Time.now - firstFailure["timestamp"]/1000
    return (time.to_f/60).round().to_s + " mins"
  end

  # Print status of all jenkins jobs
  def self.printJobStatuses()
    threads = []

    BUILDS.each do |section|
      threads << Thread.new {
        Thread.current[:output] = {}
        jobs = self.getJson("#{BASE_URI}/#{section}/api/json")

        out_str = "\e[35m%-15s\e[0m" % section
        jobs["jobs"].each do |job|
          name = job["name"]
          lastBuild = self.getJson(job["url"]+"lastBuild/api/json")

          building = lastBuild["building"]==true

          if building
            build_num = lastBuild['number']
            (1..20).each {
              build_num = build_num - 1
              lastBuild = self.getJson("#{job['url']}#{build_num}/api/json")
              break if lastBuild['result'] == "SUCCESS" || lastBuild['result'] == "FAILURE"
            }
          end

          if lastBuild["result"]=="SUCCESS"
            color="\e[32m"
          elsif lastBuild["result"]=="FAILURE"
            color="\e[31m\e[5m"
            Thread.current[:output][:broken] = true
          else
            color="ERROR"
            out_str += lastBuild.inspect
          end

          out_str += building ? "\e[36m*" : " "
          out_str += "#{color}%-30s\e[0m" % name
        end
        Thread.current[:output][:text] = out_str
      }
    end

    text = []
    threads.each {
      |t| t.join
      text << t[:output][:text]
    }

    puts text.sort
  end

  # Calls the url and returns processed json
  def self.getJson(url)
    result={}
    resp = Net::HTTP.get_response(URI.parse(url))
    if resp.code=="200"
      result=JSON.parse(resp.body)
    end
    return result
  end
end

puts "-----------------------------"
puts "key: \e[36mbuilding \e[32mpassing \e[31mfailure\e[0m"
puts "-----------------------------"

Jenkins.printJobStatuses()

puts "-----------------------------"

SMOKE_TEST.each do |url,name|
  lastBuild=Jenkins.getJob(url,:completed)

  if lastBuild["result"]=="FAILURE"
    puts "#{name} are \e[31mBROKEN\e[0m"
    puts "Time broken: "+Jenkins.getTimeBroken(SMOKE_TEST)

    Jenkins.getFirstFailureCulprits(SMOKE_TEST).each do |person|
      puts person["fullName"]
    end
  else
    puts "\e[34m#{name} are \e[32mGREEN\e[0m"
  end
end

puts "-----------------------------"
