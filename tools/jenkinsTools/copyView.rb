
require 'jenkins_api_client'

@client = JenkinsApi::Client.new(:server_ip => 'jenkins.slidev.org',
     :server_port => '8080', :username => 'jenkinsapi_user', :password => 'test1234')

def create_view(new_view_name)
  @client.view.create(new_view_name, "listview")
  puts "Created new view named: #{new_view_name}"
end

def copy_view(copy_from, copy_to)
  begin
    new_view_name = "CI#{copy_from}"
    jobs = @client.view.list_jobs(new_view_name)
    jobs.each do |job_name|
      job_xml = @client.job.get_config(job_name)
      new_job_name = job_name.gsub(copy_from, copy_to)
      new_job_xml = job_xml.gsub(copy_from, copy_to)
      puts "Creating new job: #{new_job_name}"
      @client.job.create(new_job_name, new_job_xml)
      puts "#{new_job_name} created"
    
      #@client.view.add_job(new_view_name, new_job_name)  
      #puts "job: #{new_job_name} added to view: new_view_name"
    end
  rescue Exception => e
    puts e.inspect
  end
end


if ARGV.length != 2
  puts "Incorrect usage."
  puts "Correct usage is: ruby copyView.rb <existing job/view suffix> <new job/view suffix>"
  puts "     <existing job/view suffix>: the suffix to use to copy the view and jobs from (eg. '-Sprint-Rel')"
  puts "     <new job/view suffix>: the suffix to use to create the new view and jobs (eg. '-74-Rel')"
  exit(1)
end

existing_suffix = ARG[0]
new_suffix = ARG[1]
create_view("CI#{new_suffix}")
copy_view(existing_suffix, new_suffix)