# Or: `accurev`, `bzr`, `cvs`, `darcs`, `git`, `mercurial`, `perforce`, `subversion` or `none`
server "nxbuild.slidev.org", :app, :web, :db, :primary => true
set :rails_env, "acceptance-tests"
