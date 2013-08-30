#!/bin/bash

PRG="$0"
PRGDIR=`dirname "$PRG"`
ROOT="$PRGDIR/.."
JAR_NAME="bulk-extract-*.jar"

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
        DEFAULT_BULK_EXTRACTOR_JAR=`ls $ROOT/target/$JAR_NAME`
        DEFAULT_TENANT="Midgar"
        IS_DELTA="false"

        DEFAULT_MAX_MEMORY="1024m"
        DEFAULT_MIN_MEMORY="1024m"

        JAVA_OPT="-Dfile.encoding=UTF-8"

        CHECK_SLI_CONF=0
        CHECK_KEYSTORE=0
        CHECK_SEARCH_INDEXER_TAR=0

        RUN_EXTRACT=1
        RUN_HELP=0

        SLI_CONF="sli.conf"
        SLI_ENCRYPTION_KEYSTORE="sli.encryption.keyStore"

        BULK_EXTRACTER_LOG="bulk-extracter.log"
    fi
}

function readOption {
   if [ ${1:0:2} == "-D" ]; then
      PROPERTY=`echo ${1:2} |cut -d'=' -f1`
      FILE_LOCATION=`echo ${1:2} |cut -d'=' -f2`
      if [ ${PROPERTY} == ${SLI_CONF} ]; then
         DEFAULT_CHECK_SLI_CONF=${FILE_LOCATION}
      elif [ ${PROPERTY} == ${SLI_ENCRYPTION_KEYSTORE} ]; then
         DEFAULT_CHECK_KEYSTORE=${FILE_LOCATION}
      else
         JAVA_OPT="${JAVA_OPT} ${1}"
      fi
   elif [ ${1:0:2} == "-X" ]; then
      PROPERTY=${1:2:2}
      if [ ${PROPERTY} == "mx" ]; then
         DEFAULT_MAX_MEMORY=${1:4}
      elif [ ${PROPERTY} == "ms" ]; then
         DEFAULT_MIN_MEMORY=${1:4}
      else
         JAVA_OPT="${JAVA_OPT} ${1}"
      fi
   elif [ ${1:0:2} == "-f" ]; then
      DEFAULT_BULK_EXTRACTOR_JAR=${1:2}
      FILEEXT=${1#*.}
      if [ "${FILEEXT}" == "tar.gz" -o "{$FILEEXT}" == "tgz" ]; then
        CHECK_SEARCH_INDEXER_TAR=1
      fi
      echo Setting bulk extract to use file at: ${DEFAULT_BULK_EXTRACTOR_JAR}
   elif [ ${1:0:2} == "-t" ]; then
      DEFAULT_TENANT=${1:2}
   elif [ ${1} == "help" ]; then
      RUN_EXTRACT=0
      RUN_HELP=1
   elif [ ${1} == "-d" ]; then
      IS_DELTA="true"
   else
      RUN_EXTRACT=0
      RUN_HELP=1
   fi
}

function prepareJava {
   if [ ${CHECK_SEARCH_INDEXER_TAR} != 0 ]; then
    TAR_FILE_DIR=`dirname ${DEFAULT_BULK_EXTRACTOR_JAR}`
    tar -C ${TAR_FILE_DIR} -zxf ${DEFAULT_BULK_EXTRACTOR_JAR}
    DEFAULT_BULK_EXTRACTOR_JAR=`ls $DEFAULT_BULK_EXTRACTOR_JAR/$JAR_NAME`
   fi
}

function show_help {
   echo 
   echo "Usage: /local_bulk_extract.sh [memoryOption] [fileOption] [tenantOption]"
   echo 
   echo "# run bulk-extract with default"
   echo "./local_bulk_extract.sh"
   echo 
   echo "# run bulk-extract with specifying memory"
   echo "./local_bulk_extract.sh -Xms<size>[g|G|m|M|k|K] -Xmx<size>[g|G|m|M|k|K]"
   echo 
   echo "# run bulk-extract with specifying conf/keyStore files"
   echo "./local_bulk_extract.sh -D${SLI_CONF}=<file> -D${SLI_ENCRYPTION_KEYSTORE}=<file>"
   echo 
   echo "# run bulk-extract with specifying tenant"
   echo "./local_bulk_extract.sh -t<tenant>"
   echo
   echo "# run delta bulk-extract with specifying tenant"
   echo "./local_bulk_extract.sh -t<tenant> -d"
   echo
   echo "# run bulk-extract with specifying path to a jar or tar file"
   echo "./local_bulk_extract.sh -f<filepath>"
   echo
}

function run {
    stat=0
    if [ ${RUN_EXTRACT} == 0 -a ${RUN_HELP} == 0 ]; then
      RUN_HELP=1
    fi

    if [ ${RUN_HELP} == 1 ]; then
        show_help
        return $stat
    fi
   prepareJava
#   if [ ${CHECK_SEARCH_INDEXER_TAR} != 0 ]; then
      #DEFAULT_BULK_EXTRACTOR_JAR="`dirname ${DEFAULT_BULK_EXTRACTOR_JAR}`/bulk-extract-1.0-SNAPSHOT.jar"
#   fi

    BULK_EXTRACT_OPT="-D${SLI_CONF}=${DEFAULT_CHECK_SLI_CONF}"
    SLI_ENCRYPTION_OPT="-D${SLI_ENCRYPTION_KEYSTORE}=${DEFAULT_CHECK_KEYSTORE}"
    T="$(date +%s)"
    echo "$(date): bulk-extracter Starting with $BULK_EXTRACT_OPT $SLI_ENCRYPTION_OPT"
    java -Xms$DEFAULT_MIN_MEMORY -Xmx$DEFAULT_MAX_MEMORY $BULK_EXTRACT_OPT $SLI_ENCRYPTION_OPT -jar $DEFAULT_BULK_EXTRACTOR_JAR $DEFAULT_TENANT $IS_DELTA
    stat=$?
    T="$(($(date +%s)-T))"
    echo "$(date): bulk-extracter Finished in $T seconds"

    return $stat
}


#############
# MAIN
############
configure

for OPT in $*
do
   readOption "$OPT"
done

echo $ROOT
#Execute
run
