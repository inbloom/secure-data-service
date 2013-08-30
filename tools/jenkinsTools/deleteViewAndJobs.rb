require 'jenkins_api_client'

@client = JenkinsApi::Client.new(:server_url => 'http://jenkins.slidev.org', :server_port => '8080',
      :username => '', :password => '', :jenkins_path => '/jenkins', :debug => false)

#@client = JenkinsApi::Client.new(:server_url => 'https://jenkins.slidev.org',
#     :server_port => '443', :username => 'jenkinsapi_user', :password => 'test1234')

def get_jobs(view_name)
  @client.view.list_jobs(view_name)
end

def delete_jobs(jobs)
  jobs.each do |job_name|
    puts "Deleting job: #{job_name}"
    @client.job.delete(job_name)
  end
end

def delete_view(view_name)
  puts "Deleting view: #{view_name}"
  @client.view.delete(view_name)
end

if ARGV.length != 1
  puts "Incorrect usage."
  puts "Correct usuage is: ruby deleteViewAndJobs.rb <view_name>"
  puts "This will delete the view and all jobs that are in that view."
  exit(1)
end

view_name = ARGV[0]
jobs = get_jobs(view_name)
delete_jobs(jobs)
delete_view(view_name)