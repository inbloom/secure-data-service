require 'net/http'
require 'uri'

@new_branch = ENV['NEW_BRANCH'] || 'Release-1.0.65'

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
end
def fix_job(base_url)
  puts "Adjusting " + base_url +" to " + @new_branch  
  uri = URI.parse(base_url + "config.xml")
  http = Net::HTTP.new(uri.host, uri.port)
  req = Net::HTTP::Get.new(uri.request_uri)
  req.basic_auth 'jenkinsapi_user', 'test1234'
  response = http.request(req)
  body = response.body
  match = /<name>([^<]+)<\/name>/.match(body)[1]
  body.sub!(/<name>([^<]+)<\/name>/, "<name>" + @new_branch + "</name>")
  req = Net::HTTP::Post.new(uri.request_uri)
  req.basic_auth 'jenkinsapi_user', 'test1234'
  req.body = body
  response = http.request(req)
end

url_base = 'http://jenkins.slidev.org:8080/view/CI-Sprint-Release-Branch/'
actual_jobs = get_jobs url_base
actual_jobs.delete_if {|j| /\/view\/|Config-Sprint-Release-Branch/ =~ j}
actual_jobs.each {|j| fix_job(j)}