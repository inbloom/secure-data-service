#!/usr/bin/bash

#Take care of build profiles for Spring
echo "Altering dashboard/api/ingetion projects..."
grep -lR "sli.dev.subdomain=" sli-configuration/* | xargs -L 1 sed -i'' -e "s/sli\.dev\.subdomain=.*/sli.dev.subdomain=$1/g" 

#Take care of fixture data for applications
echo "Altering fixture data for applications to match..."
sed -i'' -e "s/https:\/\/ci.slidev.org/https:\/\/$1.slidev.org/g" acceptance-tests/test/data/team_application_fixtures.json

#Take care of rails projects
echo "Altering rails applications to match..."
grep -lR "https://ci.slidev.org" admin-tools/admin-rails/config/config.yml | xargs -L 1 sed -i'' -e "s/https:\/\/ci.slidev.org/https:\/\/$1.slidev.org/g" 
grep -lR "https://ci.slidev.org" databrowser/config/config.yml | xargs -L 1 sed -i'' -e "s/https:\/\/ci.slidev.org/https:\/\/$1.slidev.org/g" 
sed -i'' -e "s/ci.slidev.org/$1.slidev.org/g" admin-tools/admin-rails/config/deploy/team.rb 
sed -i'' -e "s/ci.slidev.org/$1.slidev.org/g" databrowser/config/deploy/team.rb 

echo "Done.. ready to build and deploy!"
