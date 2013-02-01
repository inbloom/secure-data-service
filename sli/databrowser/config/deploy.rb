=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


require "bundler/capistrano"
require 'capistrano/ext/multistage'
set :stages, %w(integration, deployment)

working_dir = "sli/databrowser"

set :application, "Identity Management Admin Tool"
set :repository,  "git@git.slidev.org:sli/sli.git"
set :bundle_gemfile, "#{working_dir}/Gemfile"
set :keep_releases, 2

set :user, "rails"
set :use_sudo, false
set :deploy_via, :remote_cache
set :deploy_to, "~/prowler"

set :scm, :git
set :subdomain, nil





# Generate an additional task to fire up the thin clusters
namespace :deploy do
  desc "Start the Thin processes"
  task :start do
    if !subdomain.nil?
      run <<-CMD
        cd #{deploy_to}/current/sli; sh profile_swap.sh #{subdomain}
      CMD
    end
    run  <<-CMD
      cd #{deploy_to}/current/#{working_dir}; bundle exec thin start -C config/thin.yml -e #{rails_env}
    CMD
  end

  desc "Stop the Thin processes"
  task :stop do
    run  <<-CMD
      cd #{deploy_to}/current/#{working_dir}; bundle exec thin stop -C config/thin.yml -e #{rails_env}
    CMD
  end

  desc "Restart the Thin processes"
  task :restart do
    stop
    start
  end
  
  namespace :assets do
    task :precompile, :roles => :web, :except => { :no_release => true } do
      run "cd #{latest_release}/#{working_dir} && #{rake} RAILS_ENV=#{rails_env} #{asset_env} assets:precompile"
    end
  end
  
  task :finalize_update, :except => { :no_release => true } do
    run "chmod -R g+w #{latest_release}" if fetch(:group_writable, true)

    # mkdir -p is making sure that the directories are there for some SCM's that don't
    # save empty folders
    run <<-CMD
      rm -rf #{latest_release}/#{working_dir}/log #{latest_release}/#{working_dir}/public/system #{latest_release}/#{working_dir}/tmp/pids &&
      mkdir -p #{latest_release}/#{working_dir}/public &&
      mkdir -p #{latest_release}/#{working_dir}/tmp &&
      ln -s #{shared_path}/log #{latest_release}/#{working_dir}/log &&
      ln -s #{shared_path}/system #{latest_release}/#{working_dir}/public/system &&
      ln -s #{shared_path}/pids #{latest_release}/#{working_dir}/tmp/pids
    CMD

    if fetch(:normalize_asset_timestamps, true)
      stamp = Time.now.utc.strftime("%Y%m%d%H%M.%S")
      asset_paths = fetch(:public_children, %w(images stylesheets javascripts)).map { |p| "#{latest_release}/#{working_dir}/public/#{p}" }.join(" ")
      run "find #{asset_paths} -exec touch -t #{stamp} {} ';'; true", :env => { "TZ" => "UTC" }
    end
    cleanup
  end

end

# role :web, "your web-server here"                          # Your HTTP server, Apache/etc
# role :app, "your app-server here"                          # This may be the same as your `Web` server
# role :db,  "your primary db-server here", :primary => true # This is where Rails migrations will run
# role :db,  "your slave db-server here"

# if you're still using the script/reaper helper you will need
# these http://github.com/rails/irs_process_scripts

# If you are using Passenger mod_rails uncomment this:
# namespace :deploy do
#   task :start do ; end
#   task :stop do ; end
#   task :restart, :roles => :app, :except => { :no_release => true } do
#     run "#{try_sudo} touch #{File.join(current_path,'tmp','restart.txt')}"
#   end
# end
