/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

For the purposes of this README, we will use /opt/tomcat as $TOMCAT_HOME

===============================================================================
1. Register for SLC
   A  If you have not registered a user ID, please register@ https://admin.sandbox.slcedu.org/registration.
      You will recieve an email after registering.  It will contain instructions on how
      to ingest data. Please make sure you follow those instructions.

   B  After registering, log in to https://portal.sandbox.slcedu.org/portal/
       - Click the Admin button in the top right
         Click Application Registration
       - Point your dashboard application to the correct urls:
         URL == http://YOUR_IP_ADDRESS:Port/dashboard/service/layout/listOfStudentsPage
         Redirect URL == http://YOUR_IP_ADDRESS:Port/dashboard/callback

   C  Copy Down Client Secret and Client ID
      - Click Admin (Top right corner)
        Click Application Registration
        Click the "In Progress| Edit" button on the App you just created.
      - Copy the Client ID and the Shared Secret

   D Enable the App for a District:
     - If you ingested data (You should have received an email detailing how to 
       ingest the data), Then you can enable the application for users of a district.
     - Click Admin
       Click Application Registration
       Click the "In Progress| Edit" button on the App you just created.
     - Scroll to the bottom and check the district you want to enable.
     - Then click save.

===============================================================================

2. Edit dashboard.properties and move the file to the conf directory of tomcat.
   A  Point the API server and Security server to the proper url:
       - As of now the sandbox api server are:
          api.server.url =  https://api.sandbox.slcedu.org/
          security.server.url =  https://api.sandbox.slcedu.org/

   B  In order to use the dashboard app the client_ID and client_secret must be
      encrypted and stored within the dashboard.properties file.
      *** The following instructions use the clien_id and client_secret from step 1.
      - Run the following command to generate a key store:
          keytool -genseckey -alias $YOUR_ALIAS_NAME -keypass $YOUR_ALIAS_PASSWORD -keystore $YOUR_KEYSTORE_NAME-keystore.jks -storepass $YOUR_Keytore_PASSWORD -storetype JCEKS -keysize 256 -keyalg AES
          * Note: Do not change storetype, keysize or keyalg arguments.
      - Using EncryptionGenerator.jar (included), run the following command:
          java -jar EncyptionGenerator.jar <location_of_keyStore> < keystore_password> <key_alias> <key_password> <string_to_encrypt>
          Output will be the encrypted string.  Make sure you encrypt both client_id, and secret, separately.
      - Point the redirect URL to the dashboard's callback, then 
        copy the encrypted strings to dashboard.properties for
        client_id and client_secret:
          oauth.client.id = <copy the encrypeted client_id here>
          oauth.client.secret = <copy the encrypted client_secret here>
          oauth.redirect = http://$YOUR_IP_ADDRESS:$YOUR_PORT/dashboard/callback
      - Update the keyStore properties
          sli.encryption.keyStore = $LOCATION_TO_YOUR_keyStore/$Your_Key_Store_NAME.jks
          dashboard.encryption.keyStorePass = $YOUR_KeyStorePass
          dashboard.encryption.dalKeyAlias = $YOUR_Alias_Pass
          dashboard.encryption.dalKeyPass = $YOUR_Key_Pass
      - cp dashboard.properties to $TOMCAT_HOME/conf

===============================================================================

3. Set Default Port on Tomcat if needed
   A  The default port for Tomcat is 8080.  If you are running multiple apps, 
      you may need to change this number.
        - Open $TOMCAT_HOME/conf/server.xml
        -- Change the port number on the Connector element:
        For example: 

        <Connector port="8080" protocol="HTTP/1.1"
                   connectionTimeout="20000"
                   redirectPort="8443" />

        ==============BECOMES=============
    
        <Connector port="8888" protocol="HTTP/1.1"
                   connectionTimeout="20000"
                   redirectPort="8443" />

===============================================================================

4. Set up tomcat JAVA_OPTS  (System Properties) 
   A  Open $TOMCAT_HOME/bin/catalina.sh for editing
        Change JAVA_OPTS in tomcat to the following:

        JAVA_OPTS=" -Xms1024m \
             -Xmx1024m \
             -XX:+CMSClassUnloadingEnabled \
             -XX:+CMSPermGenSweepingEnabled \
             -XX:PermSize=512m \
             -XX:MaxPermSize=512m \
             -Dsli.env=team  \
             -Dsli.conf=/opt/tomcat/conf/dashboard.properties \
             -Dnet.sf.ehcache.pool.sizeof.AgentSizeOf.bypass=true"

        Notes for JAVA_OPTS:
        -- The -Dnet.sf.ehcache.pool.sizeof.AgentSizeOf.bypass=true option is only required for MAC OS X users.
        -- sli.conf should be set to $TOMCAT_HOME/conf/dashboard.properties

===============================================================================

5. Copy the dashboard.war file to the $TOMCAT_HOME/webapps directory

===============================================================================

6. Run tomcat:  
   chmod 700 catalina.sh 
   *chmod arguments may need to change based on your relation to the catalina.sh file (owner, group, everyone else)
   $TOMCAT_HOME/bin/catalina.sh run
