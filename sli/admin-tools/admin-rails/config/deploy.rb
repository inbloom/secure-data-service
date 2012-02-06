set :application, "Identity Management Admin Tool"
set :repository,  "set your repository location here"

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
    run "cd #{:deploy_to}/current"
    run "bundle exec thin start -C config/thin.yml"
  end

  desc "Stop the Thin processes"
  task :stop do
    run "cd #{:deploy_to}/current"
    run "bundle exec thin stop config/thin.yml"
  end

  desc "Restart the Thin processes"
  task :restart do
    run "cd #{:deploy_to}/current"
    run "bundle exec thin restart config/thin.yml"
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