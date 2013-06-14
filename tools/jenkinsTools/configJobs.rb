require 'net/http'
require 'uri'
require 'rexml/document'
require 'json'

@new_branch = ENV['NEW_BRANCH'] || 'Release-1.0.65'
@config_job = 'Config-Branch-79-Rel'
@url_base = 'https://jenkins.slidev.org/view/CI-79-Rel/'

def get_jobs(url)
  uri = URI.parse(url + "api/json")
  http = Net::HTTP.new(uri.host, uri.port)
  http.use_ssl = true if uri.scheme == "https"
  http.verify_mode = OpenSSL::SSL::VERIFY_NONE if uri.scheme == "https"

  req = Net::HTTP::Get.new(uri.request_uri)
  req.basic_auth 'jenkinsapi_user', 'test1234'

  response = http.request(req)

  body = JSON.parse(response.body)

  body['jobs'].delete_if {|j| j['name'] == @config_job}
  return body['jobs']

  #matches = body.scan(/"url":"([^"]+)"/)
  #matches.flatten!
  #matches.delete_if {|j| j == url}
  #matches.delete_if {|j| j == @job_url}
end

def fix_job(base_url)
  puts "Adjusting " + base_url + " to " + @new_branch  
  uri = URI.parse(base_url + "config.xml")
  http = Net::HTTP.new(uri.host, uri.port)
  http.use_ssl = true if uri.scheme == "https"
  http.verify_mode = OpenSSL::SSL::VERIFY_NONE if uri.scheme == "https"

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
puts actual_jobs
actual_jobs.each {|j| fix_job(j['url']) }