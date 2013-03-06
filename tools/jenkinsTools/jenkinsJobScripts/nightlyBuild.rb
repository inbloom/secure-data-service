require 'net/http'
require 'uri'
require 'json'

#####
# Part of the nightly build. 
#  1) Get a list of all jobs in this view (ignore the *Release-Build* jobs)
#  2) Check to see if each job is green
#  3) If all jobs are green continue with the nightly build
#
#####

@sli_nightly_job = "http://jenkins.slidev.org:8080/job/Nightly-SLI-Sprint-Release-Build/"
@view = "http://jenkins.slidev.org:8080/view/CI-Sprint-Release-Branch/"

# master
#@sli_nightly_job = "http://jenkins.slidev.org:8080/job/Nightly-SLI-Master-Build/"
#@view = "http://jenkins.slidev.org:8080/view/Components/"

def get_jobs(url)
  uri = URI.parse(url + "api/json")
  http = Net::HTTP.new(uri.host, uri.port)
  req = Net::HTTP::Get.new(uri.request_uri)
  req.basic_auth 'jenkinsapi_user', 'test1234'
  response = http.request(req)
  body = response.body
  matches = body.scan(/"url":"([^"]+)"/)
  matches.flatten!
  matches.delete_if {|j| j == url}
  matches.delete_if {|j| j == @sli_nightly_job}
  portal_nightly_job = @sli_nightly_job.gsub("-SLI-", "-Portal-")
  matches.delete_if {|j| j == portal_nightly_job}
end

def job_successful(job_url)
  uri = URI.parse(job_url + "lastBuild/api/json")
  http = Net::HTTP.new(uri.host, uri.port)
  req = Net::HTTP::Get.new(uri.request_uri)
  req.basic_auth 'jenkinsapi_user', 'test1234'
  response = http.request(req)
  data = JSON.parse response.body
  #puts "Result: #{response.body}\n\n"
  return data['result'] == "SUCCESS"
end

url_base = @view
actual_jobs = get_jobs url_base
success = true
actual_jobs.each  do |job| 
  puts "Checking #{job}"
  result = job_successful job
  if !result
    puts "#{job} is RED, so not doing the nightly build."
    exit(1)
  end
end

puts "All jobs are green! Continuing with the nightly build."
