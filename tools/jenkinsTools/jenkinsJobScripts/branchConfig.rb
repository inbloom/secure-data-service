require 'net/http'
require 'uri'
require 'rexml/document'

@new_branch = ENV['NEW_BRANCH'] || 'Release-1.0.65'
@job_url = 'http://jenkins.slidev.org:8080/job/Config-Sprint-Release-Branch/'
@url_base = 'http://jenkins.slidev.org:8080/view/CI-Sprint-Release-Branch/'

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
  matches.delete_if {|j| j == @job_url}
end
def fix_job(base_url)
  puts "Adjusting #{base_url} to #{@new_branch}"
  uri = URI.parse(base_url + "config.xml")
  http = Net::HTTP.new(uri.host, uri.port)
  req = Net::HTTP::Get.new(uri.request_uri)
  req.basic_auth 'jenkinsapi_user', 'test1234'
  response = http.request(req)
  body = response.body
  doc = REXML::Document.new(body)
  doc.elements.each("*/scm/branches/hudson.plugins.git.BranchSpec/name") do |element| 
    puts "Current Branch: #{element.text} changing it to #{@new_branch}"
    element.text = @new_branch
    req = Net::HTTP::Post.new(uri.request_uri)
    req.basic_auth 'jenkinsapi_user', 'test1234'
    req.body = doc.to_s
    response = http.request(req)
  end
end

actual_jobs = get_jobs @url_base
actual_jobs.each {|j| fix_job(j) }