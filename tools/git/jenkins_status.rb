require 'json'
require 'net/http'

#SMOKE_TEST="/API/job/NxFullBuild"
SMOKE_TEST="Integration/job/NxIntegration%20Tests/"

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
    broken=false
    BUILDS.each do |section|
      jobs = self.getJson("#{BASE_URI}/#{section}/api/json")

      printf "\e[35m%-15s\e[0m",section
      jobs["jobs"].each do |job|
        name = job["name"]
        lastBuild = self.getJson(job["url"]+"lastBuild/api/json")

        if lastBuild["building"]==true
          color="\e[36m"
        elsif lastBuild["result"]=="SUCCESS"
          color="\e[32m"
        elsif lastBuild["result"]=="FAILURE"
          color="\e[31m\e[5m"
          broken=true
        else
          color="ERROR"
          puts lastBuild.inspect
        end    
          
        printf "#{color}%-30s\e[0m",name
      end
      puts ""
    end

    if broken
      puts "\e[31mPUSHING WHEN BUILD IS BROKEN\e[0m"
    end
    
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

Jenkins.printJobStatuses()

lastBuild=Jenkins.getJob(SMOKE_TEST,:completed)

if lastBuild["result"]=="FAILURE"
  puts "Time broken: "+Jenkins.getTimeBroken(SMOKE_TEST)

  Jenkins.getFirstFailureCulprits(SMOKE_TEST).each do |person|
    puts person["fullName"]
  end
end

