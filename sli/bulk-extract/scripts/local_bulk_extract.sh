#!/bin/bash

PRG="$0"
PRGDIR=`dirname "$PRG"`
ROOT="$PRGDIR/.."

EXTRACTER_CONFIG="/etc/sysconfig/bulk-extract"


# bulk extract requires the following environment settings, it is recommended that the following settings be
# stored in an external file such as "/etc/sysconfig/bulk-extract" or any file specified by the extractConfig 
# setting.  If the file designated by the extractConfig setting is present, the values in that file will be used
# instead of the values designated here, as default.  It is important the if the extractConfig file does exist, it 
# must define all of the values below.
function configure {

    CONFIG_SETTING="default"     # determine if custom settings are used as opposed to default

    if  [ -f ${EXTRACTER_CONFIG} ]
    then
        echo "Using custom environment settings."
        . ${EXTRACTER_CONFIG}
        CONFIG_SETTING="custom"     # determine if custom settings are used as opposed to default
    else
        echo "Using default environment settings."
        DEFAULT_CHECK_SLI_CONF="$ROOT/../config/properties/sli.properties"
        DEFAULT_CHECK_KEYSTORE="$ROOT/../data-access/dal/keyStore/ciKeyStore.jks"
        DEFAULT_BULK_EXTRACTOR_JAR="$ROOT/target/bulk-extract-1.0-SNAPSHOT.jar"
        DEFAULT_TENANT="Midgar"
        DEFAULT_ZIP_DIR="~/"

        DEFAULT_MAX_MEMORY="1024m"
        DEFAULT_MIN_MEMORY="1024m"

        CHECK_SLI_CONF=0
        CHECK_KEYSTORE=0

        RUN_EXTRACT=0
        RUN_HELP=0
        RUN_STOP=0
        RUN_START=0

        SLI_CONF="sli.conf"
        SLI_ENCRYPTION_KEYSTORE="sli.encryption.keyStore"
        BULK_EXTRACT_OPT="-D${SLI_CONF}=${DEFAULT_CHECK_SLI_CONF}"
        SLI_ENCRYPTION_OPT="-D${SLI_ENCRYPTION_KEYSTORE}=${DEFAULT_CHECK_KEYSTORE}"

        BULK_EXTRACTER_LOG="bulk-extracter.log"
    fi
}

function show_help {
    echo "TBD help"
}

function run {
    T="$(date +%s)"
    echo "$(date): bulk-extracter Starting with $BULK_EXTRACT_OPT $SLI_ENCRYPTION_OPT"
    java $BULK_EXTRACT_OPT $SLI_ENCRYPTION_OPT -jar $DEFAULT_BULK_EXTRACTOR_JAR $DEFAULT_TENANT $DEFAULT_ZIP_DIR
    T="$(($(date +%s)-T))"
    echo "$(date): bulk-extracter Finished in $T seconds"
}


#############
# MAIN
############
configure
echo $ROOT
#Execute
run
exit 0
