require 'net/http'
require 'uri'
require 'rexml/document'
require 'json'
require 'jenkins_api_client'

@new_branch = ENV['NEW_BRANCH'] || 'Release-1.0.XX'
@view_to_configure = ENV['VIEW_TO_CONFIGURE'] || 'CI-XX-Rel'

@client = JenkinsApi::Client.new(:server_url => 'http://jenkins.slidev.org', :server_port => '8080',
      :username => '', :password => '', :jenkins_path => '/jenkins', :debug => false)

def get_jobs(view)
  jobs = @client.view.list_jobs(view)
  
  body = JSON.parse(response.body)

  return body['jobs']
end

def fix_job(job_name)
  puts "Adjusting " + job_name + " to " + @new_branch  

  job_xml = @client.job.get_config(job_name)

  job_xml.elements.each("*/scm/branches/hudson.plugins.git.BranchSpec/name") do |element| 
    puts "Current Branch: #{element.text} changing it to #{@new_branch}"
    element.text = @new_branch
  end

  @client.job.post_config(job_name, job_xml)
end

actual_jobs = get_jobs @view_to_configure
puts actual_jobs
actual_jobs.each {|j| fix_job(j) }