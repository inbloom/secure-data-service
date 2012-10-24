#!/bin/bash

SEARCH_INDEXER_OPT=""
SEARCH_INDEXER_COMMAND_OPTIONS=""

CHECK_SLI_CONF=0
CHECK_KEYSTORE=0
CHECK_SEARCH_INDEXER_TAR=0
DEFAULT_CHECK_SLI_CONF="../../config/properties/sli.properties"
DEFAULT_CHECK_KEYSTORE="../../data-access/dal/keyStore/ciKeyStore.jks"
DEFAULT_SEARCH_INDEXER_JAR="target/search-indexer-1.0-SNAPSHOT.jar"

RUN_EXTRACT=0
RUN_HELP=0
RUN_STOP=0
RUN_START=0

REMOTE_COMMAND_PORT=0

SLI_CONF="sli.conf"
SLI_ENCRYPTION_KEYSTORE="sli.encryption.keyStore"

function readOption {
   if [ ${1:0:2} == "-D" ]; then
      PROPERTY=`echo ${1:2} |cut -d'=' -f1`
      FILE_LOCATION=`echo ${1:2} |cut -d'=' -f2`
      if [ ${PROPERTY} == ${SLI_CONF} ]; then
         CHECK_SLI_CONF=${FILE_LOCATION}
      elif [ ${PROPERTY} == ${SLI_ENCRYPTION_KEYSTORE} ]; then
         CHECK_KEYSTORE=${FILE_LOCATION}
      else
         SEARCH_INDEXER_OPT="${SEARCH_INDEXER_OPT} ${1}"
      fi
   else
      FILEEXT=${1#*.}
      if [ "${FILEEXT}" == "tar.gz" -o "{$FILEEXT}" == "tgz" ]; then
         CHECK_SEARCH_INDEXER_TAR=$1
      else
         if [ ${1} == "start" ]; then
            RUN_START=1
         elif [ ${1} == "extract" ]; then
            RUN_EXTRACT=1
         elif [ ${1} == "help" ]; then
            RUN_HELP=1
         elif [ ${1} == "stop" ]; then
            RUN_STOP=1
         elif [ ${1} == "debug" ]; then
            RUN_START=1
            SEARCH_INDEXER_OPT="${SEARCH_INDEXER_OPT} -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"
         else
            SEARCH_INDEXER_COMMAND_OPTIONS="${SEARCH_INDEXER_COMMAND_OPTIONS} $1"
         fi
      fi
   fi
}

function isJavaReady {
   if [ ${RUN_STOP} == 0 -a ${RUN_EXTRACT} == 0 -a ${RUN_HELP} == 0 -a ${RUN_START} == 0 ]; then
      RUN_HELP=1
   fi

   ##CHECK FOR STOP/EXTRACT
   if [ ${RUN_STOP} == 1 -o ${RUN_EXTRACT} == 1 ]; then
      if [ ${CHECK_SLI_CONF} == 0 ]; then
         CHECK_SLI_CONF=${DEFAULT_CHECK_SLI_CONF}
      fi
      echo "Reading default ${SLI_CONF} [${DEFAULT_CHECK_SLI_CONF}]"
      if [ ! -f ${CHECK_SLI_CONF} ]; then
         echo "File does not exit '${CHECK_SLI_CONF}'"
         return 0
      fi
      SEARCH_INDEXER_OPT="${SEARCH_INDEXER_OPT} -D${SLI_CONF}=${CHECK_SLI_CONF}"
      REMOTE_COMMAND_PORT=`grep sli.search.indexer.service.port ${CHECK_SLI_CONF}|cut -d '=' -f2`
      return 1
   fi

   ##CHECK FOR START/DEBUG
   if [ ${RUN_START} == 1 ]; then
      if [ ${CHECK_SLI_CONF} == 0 ]; then
         CHECK_SLI_CONF=${DEFAULT_CHECK_SLI_CONF}
      fi
      echo "Reading default ${SLI_CONF} [${DEFAULT_CHECK_SLI_CONF}]"
      if [ ${CHECK_KEYSTORE} == 0 ]; then
         CHECK_KEYSTORE=${DEFAULT_CHECK_KEYSTORE}
      fi
      echo "Reading default keyStore [${DEFAULT_CHECK_KEYSTORE}]"
      for FILE_LOCATION in "${CHECK_SLI_CONF}" "${CHECK_KEYSTORE}"
      do
         if [ ! -f ${FILE_LOCATION} ]; then
            echo "File does not exit: '${FILE_LOCATION}'"
            return 0
         fi
      done
      if [ ${CHECK_SEARCH_INDEXER_TAR} == 0 ]; then
         if [ ! -f ${DEFAULT_SEARCH_INDEXER_JAR} ];then
            echo "Please specify search_indexer.tar.gz"
            return 0;
         fi
      else
         if [ ! -f ${CHECK_SEARCH_INDEXER_TAR} ]; then
            echo "File [${CHECK_SEARCH_INDEXER_TAR}] does not exist"
            return 0
         fi
      fi
      SEARCH_INDEXER_OPT="${SEARCH_INDEXER_OPT} -D${SLI_CONF}=${CHECK_SLI_CONF} -D${SLI_ENCRYPTION_KEYSTORE}=${CHECK_KEYSTORE}"
      return 1
   fi
   return 1
}

function prepareJava {
   if [ ${CHECK_SEARCH_INDEXER_TAR} != 0 ]; then
      tar -C `dirname ${CHECK_SEARCH_INDEXER_TAR}` -zxf ${CHECK_SEARCH_INDEXER_TAR}
   fi
}

function show_help {
   echo "# run search-indexer"
   echo "local_search_indexer.sh start"
   echo "# run search-indexer with specifying files"
   echo "local_search_indexer.sh start search_indexer.tar.gz -D${SLI_CONF}=<file> -D${SLI_ENCRYPTION_KEYSTORE}=<file>"
   echo "# run search-indexer in debug mode"
   echo "local_search_indexer.sh debug"
   echo "local_search_indexer.sh debug search_indexer.tar.gz -D${SLI_CONF}=<file> -D${SLI_ENCRYPTION_KEYSTORE}=<file>"
   echo "# extract search-indexer"
   echo "local_search_indexer.sh extract"
   echo "local_search_indexer.sh extract -D${SLI_CONF}=<file>"
   echo "# stop search-indexer"
   echo "local_search_indexer.sh stop"
   echo "local_search_indexer.sh stop -D${SLI_CONF}=<file>"
}

function run {
   if [ ${RUN_HELP} == 1 ]; then
      show_help
   elif [ ${RUN_STOP} == 1 ]; then
      if [ ${REMOTE_COMMAND_PORT} != 0 ]; then
         echo "Stopping.... accessing port ${REMOTE_COMMAND_PORT}"
         echo stop | nc 127.0.0.1 ${REMOTE_COMMAND_PORT}
      else
         echo "Could not find 'sli.search.indexer.service.port' from ${CHECK_SLI_CONF}"
      fi
   elif [ ${RUN_EXTRACT} == 1 ]; then
      if [ ${REMOTE_COMMAND_PORT} != 0 ]; then
         echo "Extracting.... accessing port ${REMOTE_COMMAND_PORT}"
         echo extract sync | nc 127.0.0.1 ${REMOTE_COMMAND_PORT}
      else
         echo "Could not find 'sli.search.indexer.service.port' from ${CHECK_SLI_CONF}"
      fi
   elif [ ${RUN_START} == 1 ]; then
      prepareJava
      if [ ${CHECK_SEARCH_INDEXER_TAR} == 0 ]; then
         echo java ${SEARCH_INDEXER_OPT} -jar ${DEFAULT_SEARCH_INDEXER_JAR} ${SEARCH_INDEXER_COMMAND_OPTIONS}
         java ${SEARCH_INDEXER_OPT} -jar ${DEFAULT_SEARCH_INDEXER_JAR} ${SEARCH_INDEXER_COMMAND_OPTIONS}
      else
         echo java ${SEARCH_INDEXER_OPT} -jar `dirname ${CHECK_SEARCH_INDEXER_TAR}`/search-indexer-1.0-SNAPSHOT.jar ${SEARCH_INDEXER_COMMAND_OPTIONS}
         java ${SEARCH_INDEXER_OPT} -jar `dirname ${CHECK_SEARCH_INDEXER_TAR}`/search-indexer-1.0-SNAPSHOT.jar ${SEARCH_INDEXER_COMMAND_OPTIONS}
      fi
   fi
}


#############
# MAIN
############

for OPT in $*
do
   readOption "$OPT"
done

isJavaReady
EVAL=$?
if [ $EVAL == 0 ]; then
   show_help
   exit
fi

#Execute
run
