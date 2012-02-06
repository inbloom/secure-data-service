require "bundler/capistrano"

set :application, "Identity Management Admin Tool"
set :repository,  "git@github.com:WGEN-SLI/SLI.git"
set :bundle_gemfile, "sli/admin-tools/admin-rails/Gemfile"

set :user, "rails"
set :use_sudo, false
set :deploy_via, :remote_cache
set :deploy_to, "~/admin"

set :scm, :git

# Or: `accurev`, `bzr`, `cvs`, `darcs`, `git`, `mercurial`, `perforce`, `subversion` or `none`
server "devrails1.slidev.org", :app, :web, :db, :primary => true

# Generate an additional task to fire up the thin clusters
namespace :deploy do
  desc "Start the Thin processes"
  task :start do
    run  <<-CMD
      cd #{deploy_to}/current/sli/admin-tools/admin-rails; bundle exec thin start -C config/thin.yml
    CMD
  end

  desc "Stop the Thin processes"
  task :stop do
    run  <<-CMD
      cd #{deploy_to}/current/sli/admin-tools/admin-rails; bundle exec thin stop -C config/thin.yml
    CMD
  end

  desc "Restart the Thin processes"
  task :restart do
    run  <<-CMD
      cd #{deploy_to}/current/sli/admin-tools/admin-rails; bundle exec thin restart -C config/thin.yml
    CMD
  end
  
  task :finalize_update, :except => { :no_release => true } do
    run "chmod -R g+w #{latest_release}" if fetch(:group_writable, true)

    # mkdir -p is making sure that the directories are there for some SCM's that don't
    # save empty folders
    run <<-CMD
      rm -rf #{latest_release}/log #{latest_release}/public/system #{latest_release}/tmp/pids &&
      mkdir -p #{latest_release}/public &&
      mkdir -p #{latest_release}/tmp &&
      ln -s #{shared_path}/log #{latest_release}/log &&
      ln -s #{shared_path}/system #{latest_release}/public/system &&
      ln -s #{shared_path}/pids #{latest_release}/tmp/pids
    CMD

    if fetch(:normalize_asset_timestamps, true)
      stamp = Time.now.utc.strftime("%Y%m%d%H%M.%S")
      asset_paths = fetch(:public_children, %w(images stylesheets javascripts)).map { |p| "#{latest_release}/sli/admin-tools/admin-rails/public/#{p}" }.join(" ")
      run "find #{asset_paths} -exec touch -t #{stamp} {} ';'; true", :env => { "TZ" => "UTC" }
    end
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