require 'jenkins_api_client'

@client = JenkinsApi::Client.new(:server_url => 'http://jenkinselb.slidev.org', :server_port => '8080',
      :username => 'jenkinsapi_user', :password => 'test1234')

#@client = JenkinsApi::Client.new(:server_url => 'https://jenkins.slidev.org',
#     :server_port => '443', :username => 'jenkinsapi_user', :password => 'test1234')

def create_view(new_view_name)
  @client.view.create(new_view_name, "listview")
  puts "Created new view named: #{new_view_name}"
end

def copy_view(source_view, target_view)
  begin
    jobs = @client.view.list_jobs(source_view)
    jobs.each do |job_name|
      job_xml = @client.job.get_config(job_name)
      new_job_name = job_name.gsub("_#{source_view}", "_#{target_view}")
      new_job_xml = job_xml.gsub(source_view, target_view)
      puts "Creating new job: #{new_job_name}"
      @client.job.create(new_job_name, new_job_xml)
      puts "#{new_job_name} created"
    
      # this won't work until Jenkins is upgraded
      @client.view.add_job(target_view, new_job_name)  
      #puts "job: #{new_job_name} added to view: new_view_name"
    end
  rescue Exception => e
    puts e.inspect
  end
end


if ARGV.length != 2
  puts "Incorrect usage."
  puts "Correct usage is: ruby copyView.rb <existing view> <new view>"
  puts "     <existing view>: the suffix to use to copy the view and jobs from (eg. 'Master')"
  puts "     <new view>: the suffix to use to create the new view and jobs (eg. 'CI-74-Rel')"
  exit(1)
end

existing_view = ARGV[0]
new_view = ARGV[1]
create_view(new_view)
copy_view(existing_view, new_view)