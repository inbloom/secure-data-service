#!/usr/bin/bash

#Take care of build profiles for Spring
echo "Altering dashboard/api/ingetion projects..."
grep -lR "sli.dev.subdomain=" * | xargs -L 1 sed -n -e "s/sli\.dev\.subdomain=.*/sli.dev.subdomain=$1/p"

#Take care of fixture data for applications
echo "Altering fixture data for applications to match..."
grep -lR "https://ci.slidev.org" acceptance-tests/test/data/team_application_fixtures.json | xargs -L 1 sed -n -e "s/https:\/\/ci.slidev.org/https:\/\/$1.slidev.org/p"

#Take care of rails projects
echo "Altering rails applications to match..."
grep -lR "https://ci.slidev.org" admin-tools/admin-rails/config/config.yml | xargs -L 1 sed -n -e "s/https:\/\/ci.slidev.org/https:\/\/$1.slidev.org/p"
grep -lR "https://ci.slidev.org" databrowser/config/config.yml | xargs -L 1 sed -n -e "s/https:\/\/ci.slidev.org/https:\/\/$1.slidev.org/p"

echo "Done.. ready to build and deploy!"
