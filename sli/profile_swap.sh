#!/usr/bin/bash

if [ $# -eq 1 ]; then
    hostname=$1
else
    hostname=`hostname -s`
fi

#Take care of build profiles for Spring
echo "Altering dashboard/api/ingetion projects..."
grep -lR "sli.dev.subdomain:" config/* | xargs --verbose -L 1 sed -i "s/sli\.dev\.subdomain:.*/sli.dev.subdomain: $hostname/g"
grep -lR "\${sample.swap}" config/* | xargs --verbose -L 1 sed -i "s/\${sample\.swap}/$hostname/g"
grep -lR "\${sli.dev.subdomain}" SDK/sample/* | xargs --verbose -L 1 sed -i "s/\${sli\.dev\.subdomain}/$hostname/g"
sed -i "s/https:\/\/ci.slidev.org/https:\/\/$hostname.slidev.org/g" simple-idp/src/main/resources/config/team-simple-idp.properties

#Take care of fixture data for applications
echo "Altering fixture data for applications to match..."
# sed -i "" -e "s/https:\/\/ci.slidev.org/https:\/\/$hostname.slidev.org/g" acceptance-tests/test/data/team_application_fixtures.json
sed -i "s/https:\/\/ci.slidev.org/https:\/\/$hostname.slidev.org/g" acceptance-tests/test/data/application_fixture.json
sed -i "s/https:\/\/ci.slidev.org/https:\/\/$hostname.slidev.org/g" acceptance-tests/test/data/realm_fixture.json
sed -i "s/http:\/\/local.slidev.org:8082/https:\/\/$hostname.slidev.org/g" acceptance-tests/test/data/realm_fixture.json
sed -i "s/https:\/\/ci.slidev.org/https:\/\/$hostname.slidev.org/g" acceptance-tests/test/data/application_denial_fixture.json

#Take care of rails projects
echo "Altering rails applications to match..."
grep -lR "https://ci.slidev.org" admin-tools/admin-rails/config/config.yml | xargs -L 1 sed -i "s/https:\/\/ci.slidev.org/https:\/\/$hostname.slidev.org/g"
grep -lR "https://ci.slidev.org" databrowser/config/config.yml | xargs -L 1 sed -i "s/https:\/\/ci.slidev.org/https:\/\/$hostname.slidev.org/g"
sed -i "s/ci.slidev.org/$hostname.slidev.org/g" admin-tools/admin-rails/config/deploy/*.rb
sed -i "s/ci.slidev.org/$hostname.slidev.org/g" databrowser/config/deploy/*.rb

echo "Done.. ready to build and deploy!"
