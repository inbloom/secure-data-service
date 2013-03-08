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

@view = "http://jenkins.slidev.org:8080/view/Components/"


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

e2e_prod = "http://jenkins.slidev.org:8080/job/E2E_Integration_Tests_Prod/"
e2e_sandbox = "http://jenkins.slidev.org:8080/job/E2E_Integration_Tests_Sandbox/"
puts "Checking #{e2e_prod}"
if job_successful e2e_prod and job_successful e2e_sandbox
    puts "E2E tests are green so doing the nightly build"
else
    puts "E2E Tests are RED (or currently running), so not doing the nightly build."
    exit(1)
end

