require 'net/http'
require 'uri'
require 'rexml/document'
require 'json'
require 'jenkins_api_client'

@new_branch = ENV['NEW_BRANCH'] || 'Release-1.0.XX'
@view_to_configure = ENV['VIEW_TO_CONFIGURE'] || 'CI-XX-Rel'

@client = JenkinsApi::Client.new(:server_url => 'http://jenkins.slidev.org', :server_port => '8080',
      :username => '', :password => '', :jenkins_path => '/jenkins', :debug => false)

def get_jobs(url)
  jobs = @client.view.list_jobs(source_view)
  
  body = JSON.parse(response.body)

  return body['jobs']
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

actual_jobs = get_jobs @view_to_configure
puts actual_jobs
actual_jobs.each {|j| fix_job(j['url']) }