#!/bin/bash

PRG="$0"
PRGDIR=`dirname "$PRG"`
ROOT="$PRGDIR/.."

INDEXER_CONFIG="/etc/sysconfig/search-indexer"


# search indexer requires the following environment settings, it is recommended that the following settings be
# stored in an external file such as "/etc/sysconfig/search-indexer" or any file specified by the indexerConfig 
# setting.  If the file designated by the indexerConfig setting is present, the values in that file will be used
# instead of the values designated here, as default.  It is important the if the indexerConfig file does exist, it 
# must define all of the values below.
function configure {

    CONFIG_SETTING="default"     # determine if custom settings are used as opposed to default

    if  [ -f ${INDEXER_CONFIG} ]
    then
        echo "Using custom environment settings."
        . ${INDEXER_CONFIG}
        CONFIG_SETTING="custom"     # determine if custom settings are used as opposed to default
    else
        echo "Using default environment settings."
        DEFAULT_CHECK_SLI_CONF="$ROOT/../config/properties/sli.properties"
        DEFAULT_CHECK_KEYSTORE="$ROOT/../data-access/dal/keyStore/ciKeyStore.jks"
        DEFAULT_BULK_EXTRACTOR_JAR="$ROOT/target/bulk-extract-1.0-SNAPSHOT.jar"
        DEFAULT_MAX_MEMORY="1024m"
        DEFAULT_MIN_MEMORY="1024m"
        DEFAULT_REMOTE_COMMAND_PORT=10024

        SEARCH_INDEXER_OPT="-Dfile.encoding=UTF-8"
        SEARCH_INDEXER_COMMAND_OPTIONS=""

        CHECK_SLI_CONF=0
        CHECK_KEYSTORE=0
        CHECK_SEARCH_INDEXER_TAR=0

        RUN_EXTRACT=0
        RUN_HELP=0
        RUN_STOP=0
        RUN_START=0

        REMOTE_COMMAND_PORT=0

        SLI_CONF="sli.conf"
        SLI_ENCRYPTION_KEYSTORE="sli.encryption.keyStore"
        BULK_EXTRACT_OPT="-D${SLI_CONF}=${DEFAULT_CHECK_SLI_CONF}"

        SEARCH_INDEXER_LOG="search-indexer.log"
    fi
}

function show_help {
    echo "TBD help"
}

function run {
echo $BULK_EXTRACT_OPT
java $BULK_EXTRACT_OPT -Dsli.encryption.keyStore=/Users/ablum/git/sli/sli/data-access/dal/keyStore/ciKeyStore.jks -jar $DEFAULT_BULK_EXTRACTOR_JAR Midgar
}


#############
# MAIN
############
configure
echo $ROOT
#Execute
run
exit 0
