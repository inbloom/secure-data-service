#!/usr/bin/ruby

# STEPS TO RUNNING BULK-EXTRACT FROM SCRATCH

#start mongod
#run sli/config/scripts/resetAllDbs.sh to clean database
#start activemq
#start api to bootstrap database
#start search indexer
#bundle exec rake realmInit
#bundle exec rake importSandboxData
#run ./compile_local_extract_jar.rb
#then run this script! ./start_local_extract.rb


# configuration variables
sli_conf="../../config/properties/sli.properties"
sli_keystore="../../data-access/dal/keyStore/ciKeyStore.jks"
bulk_extract_jar=`ln ../target/bulk-extract-*.jar`
tenant="Midgar"
is_delta="false"

# run it!
puts "starting extract"
`java -Xms1G -Xmx2G -Dsli.conf=#{sli_conf} -Dsli.encryption.keyStore=#{sli_keystore} -jar #{bulk_extract_jar} #{tenant} #{is_delta}`
puts "post extract"
