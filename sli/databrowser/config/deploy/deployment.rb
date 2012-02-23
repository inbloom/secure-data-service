# Or: `accurev`, `bzr`, `cvs`, `darcs`, `git`, `mercurial`, `perforce`, `subversion` or `none`
server "devdatab1.slidev.org", :app, :web, :db, :primary => true
set :rails_env, "deployment"