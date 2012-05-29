# Or: `accurev`, `bzr`, `cvs`, `darcs`, `git`, `mercurial`, `perforce`, `subversion` or `none`
server "ci.slidev.org", :app, :web, :db, :primary => true
set :rails_env, "production"