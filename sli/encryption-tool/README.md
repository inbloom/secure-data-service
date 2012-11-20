Encryption Tool
------------------

This is a Java-based application to encrypt select configuration parameters that require encryption for the SLC Educator Dashboard.

Usage: java -jar encryption-tool.jar <keystore_location> <keystore_password> <key_alias> <key_password> <string>

keystore_location - the location of the keystore file
keystore_password - the password of the keystore file
key_alias - the key alias
key_password - the key password
string - the string to be encrypted

In order to create a distribution, run the following Maven command in the cloned directory: 
% mvn package –DskipTests

The distribution will be created under target.
