Summary
=======

This is the inBloom Datastore. It contains the backend for the inBloom project. The main projects that are to be used are ingestion-service, api, simple-idp, search-indexer, dashboard, admin-tools and databrowser. All of the projects are Maven driven with the exception of admin-tools and databrowser. Those are both Rails applications. This project runs on Java 6 and Maven 3. For Ruby, version 2.0.0 is the recommended version.

*Note - These instructions were written using Ubuntu 12.04 and above.

Prerequisites
=============

MongoDB
-------
  - Version 2.4.8 is a tested version.  Version 2.6 is the current version but is incompatible.
  - Used as the database for the system.
  - Required to be running locally
  - Add `smallfiles=true` to your mongodb.conf

ActiveMQ
--------
  - Version 5.8 is the current version.
  - Used as a processor for ingestion
  - Required to be running locally
  - Stomp must be activated.  Add `<transportConnector name="stomp" uri="stomp://0.0.0.0:61613"/>` to the conf/activemq.xml file in the `<transportConnectors>` block if it isn't there already
  - You can start ActiveMQ with the following command (executed from apache-activemq-5.8.0/):  `bin/activemq console`

libxml2-dev and libxslt-dev
---------------------------
  - Needed as dependencies for Ruby gems that will be installed
  - Not required for MacOS

ruby, rubygems and ruby-dev
---------------------------
  - Acceptance-tests are written using Ruby
  - Admin Tools and Databrowser are written in Ruby

The Ruby bundler gem
--------------------
  - Used to install Ruby dependencies
  - `gem install bundler`

Open-ADK Installation - Project Dependency
------------------------------------------
  - `git clone https://github.com/open-adk/OpenADK-java.git`
  - `cd OpenADK-java/adk-generator`
  - `ant clean US`
  - Change the ADK-library pom.xml line #7 from <version>1.0.0-snapshot</version> to: <version>1.0.0</version> - This is due to the fact that the project dependency does not have SNAPSHOT in the version.
  - `cd ../OpenADK-java/adk-library`
  - `mvn -P US install`

ldap-in-memory 
--------------
  - Required for an entirely local dev environment
  - `git clone https://github.com/inbloom/ldap-in-memory.git`
  - `cd ldap-in-memory`
  - `mvn clean install`
  - `mvn jetty:run` #start the ldap server

Building the Datastore
======================

If you have not done so, change the PermGen size to a higher number. If not, you could run into PermGen space issues during the build.
  - `export MAVEN_OPTS=-XX:MaxPermSize=512m`
  - More on `MAVEN_OPTS` below

Get the source for the datastore
--------------------------------
  - git clone https://github.com/inbloom/secure-data-service.git

Build the build-tools
---------------------
  - `cd {git_root}/build-tools`
  - `mvn clean install`

Build the main part of the system
---------------------------------
  - `cd {git_root}/sli`
  - `mvn clean install -DskipTests [-Dsli.env=local-ldap-server]`
  - Use the (optional) '-Dsli.env=local-ldap-server' flag to use configs for the ldap-in-memory component (https://github.com/inbloom/ldap-in-memory).  Optionally, use -Dsli.env=local-sandbox-ldap-server if you intend to run the sandbox version locally.  The list of available configurations are at $SLI_ROOT/config/config.in/canonical_config.yml

Set the `SLI_ROOT` variable
---------------------------
  - Exporting this variable will help with browsing the datastore as well as being required for some of the tests
  - `export SLI_ROOT = {git_root}/sli`

Setup your Mongo DBs
--------------------
  - It is important that this be run from the directory where it exists
  - `cd $SLI_ROOT/config/scripts`
  - `./resetAllDbs.sh`
  - **NOTE:**  You must restart the API service whenever you reset the Mongo DBs

Bringing Up Services
====================

Next, we start bringing services up. Before that however, we need to set up some MAVEN_OPTS. For Ingestion and the API, the Maven defaults are not good enough. Please set MAVEN_OPTS like the following for Ingestion and API
  - `export MAVEN_OPTS="-Xmx2g -XX:+CMSClassUnloadingEnabled -XX:PermSize=128M -XX:MaxPermSize=512M"`

Start Up Ingestion
------------------
  - Used for ingesting data into the system
  - `cd $SLI_ROOT/ingestion/ingestion-service`
  - `mvn jetty:run`
  - You will see that Ingestion is started by seeing it running on port 8000

Start Up API
------------
  - Main part of the application for accessing the data
  - `cp $SLI_ROOT/data-access/dal/keyStore/trustey.jks /tmp`
  - `cd $SLI_ROOT/api`
  - `mvn jetty:run`
  - You will see that API is started by seeing it running on port 8080

Start Simple-IDP
----------------
  - Performs the security portions by providing authentication/federation
  - `cd $SLI_ROOT/simple-idp`
  - `mvn jetty:run`
  - You will see that Simple-IDP is running by seeing it run on port 8082

Start Search Indexer
--------------------
  - `cd $SLI_ROOT/search-indexer`
  - `scripts/local_search_indexer.sh start && tail -f logs/search-indexer.log`
  - When you want to stop the Search Indexer run `scripts/local_search_indexer.sh stop`

Import Data
-----------
### Required Steps
  - `cd $SLI_ROOT/acceptance-tests`
  - `bundle install`
  - `bundle exec rake realmInit` 

### Option 1:  Rake approach
  - Less hassle, but keys are assigned (not generated from the natural key data members as usual)
  - `bundle exec rake importSandboxData`

### Option 2:  Ingestion approach
  - This is the method production uses.  Uses keys generated from natural key data members
  - `cd $SLI_ROOT/ingestion/ingestion-service/target/ingestion/lz/inbound/Midgar-DAYBREAK`
  - `cp $SLI_ROOT/acceptance-tests/test/features/ingestion/test_data/SmallSampleDataSet.zip ./`
  - `ruby $SLI_ROOT/opstools/ingestion_trigger/publish_file_uploaded.rb STOR $(pwd)/SmallSampleDataSet.zip`
  - `tail -F $SLI_ROOT/ingestion/ingestion-service/target/ingestion/logs/ingestion.log` for verification. You should see 'Clearing cache at job completion' when it is finished.

Start Dashboard
---------------
  - `cd $SLI_ROOT/dashboard`
  - `mvn jetty:run`
  - You will see that Dashboard is running by seeing it run on port 8888

Start Admin Tools
-----------------
  - `cd $SLI_ROOT/admin-tools/admin-rails`
  - `bundle install`
  - `bundle exec rails server`
  - You will see that admin tools are running on port 3001

Start Databrowser
-----------------
  - `cd $SLI_ROOT/databrowser`
  - `bundle install`
  - `bundle exec rails server`
  - You will see that Databrowser is running on port 3000

Run Acceptance Smoke Tests
--------------------------
 - `cd $SLI_ROOT/acceptance-tests`
 - `bundle install`
 - `bundle exec rake smokeTests`
 
Community
=========

Licensing
=========

Secure Data Service is licensed under the Apache License, Version 2.0. See LICENSE for full license text.

