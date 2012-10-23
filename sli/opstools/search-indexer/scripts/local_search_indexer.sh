#!/bin/bash

SEARCH_INDEXER_OPT=""
SEARCH_INDEXER_TAR=""
SEARCH_INDEXER_COMMAND_OPTIONS=""

CHECK_SLI_CONF=0
CHECK_KEYSTORE=0

RUN_EXTRACT=0
RUN_HELP=0
RUN_STOP=0

REMOTE_COMMAND_PORT=0

function readOption {
   if [ ${1:0:2} == "-D" ]; then
      PROPERTY=`echo ${1:2} |cut -d'=' -f1`
      FILE_LOCATION=`echo ${1:2} |cut -d'=' -f2`
      if [ ${PROPERTY} == "sli.conf" ]; then
         CHECK_SLI_CONF=${FILE_LOCATION}
      elif [ ${PROPERTY} == "sli.encryption.keyStore" ]; then
         CHECK_KEYSTORE=${FILE_LOCATION}
      fi
      SEARCH_INDEXER_OPT="${SEARCH_INDEXER_OPT} ${1}"
   else
      FILEEXT=${1#*.}
      if [ "${FILEEXT}" == "tar.gz" -o "{$FILEEXT}" == "tgz" ]; then
         SEARCH_INDEXER_TAR=$1
      else
         if [ ${1} == "extract" ]; then
            RUN_EXTRACT=1
         elif [ ${1} == "help" ]; then
            RUN_HELP=1
         elif [ ${1} == "stop" ]; then
            RUN_STOP=1
         elif [ ${1} == "debug" ]; then
            SEARCH_INDEXER_OPT="${SEARCH_INDEXER_OPT} -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"
         else
            SEARCH_INDEXER_COMMAND_OPTIONS="${SEARCH_INDEXER_COMMAND_OPTIONS} $1"
         fi
      fi
   fi
}

function isJavaReady {

   ##CHECK FOR STOP
   if [ ${RUN_STOP} == 1 ]; then
      if [ ${CHECK_SLI_CONF} == 0 ]; then
         return 0;
      fi
      if [ ! -f ${CHECK_SLI_CONF} ]; then
         echo "File does not exit '${CHECK_SLI_CONF}'"
         return 0
      fi
      REMOTE_COMMAND_PORT=`grep sli.search.indexer.service.port ${CHECK_SLI_CONF}|cut -d '=' -f2`
      return 1
   fi

   ##CHECK FOR EXTRACT
   if [ ${RUN_EXTRACT} == 1 ]; then
      if [ -z ${SEARCH_INDEXER_TAR} ]; then
         echo "Please specify search_indexer.tar.gz"
         return 0;
      elif [ ! -f ${SEARCH_INDEXER_TAR} ]; then
         echo "File [${SEARCH_INDEXER_TAR}] does not exist"
         return 0
      fi
      return 1
   fi

   ##CHECK FOR START/DEBUG
   if [ ${CHECK_SLI_CONF} == 0 ]; then
      return 0;
   elif [ ${CHECK_KEYSTORE} == 0 ]; then
      return 0;
   fi
   for FILE_LOCATION in ${CHECK_SLI_CONF} ${CHECK_KEYSTORE};
   do
      if [ ! -f ${FILE_LOCATION} ]; then
         echo "File does not exit '${FILE_LOCATION}'"
         return 0
      fi
   done
   if [ -z ${SEARCH_INDEXER_TAR} ]; then
      echo "Please specify search_indexer.tar.gz"
      return 0;
   elif [ ! -f ${SEARCH_INDEXER_TAR} ]; then
      echo "File [${SEARCH_INDEXER_TAR}] does not exist"
      return 0
   fi
   return 1;
}

function prepareJava {
   tar -C `dirname ${SEARCH_INDEXER_TAR}` -zx${1}f ${SEARCH_INDEXER_TAR}
}

function show_help {
   echo "# run search-indexer"
   echo "local_search_indexer.sh search_indexer.tar.gz -Dsli.conf=<file> -Dsli.encryption.keyStore=<file>"
   echo "# run search-indexer widh debug mode"
   echo "local_search_indexer.sh debug search_indexer.tar.gz -Dsli.conf=<file> -Dsli.encryption.keyStore=<file>"
   echo "# extract search-indexer bundle"
   echo "local_search_indexer.sh extract search_indexer.tar.gz"
   echo "# stop search-indexer"
   echo "local_search_indexer.sh stop -Dsli.conf=<file>"
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
      prepareJava "v"
      echo "Extracted in `dirname ${SEARCH_INDEXER_TAR}`"
   else
      prepareJava
      echo java ${SEARCH_INDEXER_OPT} -jar `dirname ${SEARCH_INDEXER_TAR}`/search-indexer-1.0-SNAPSHOT.jar ${SEARCH_INDEXER_COMMAND_OPTIONS}
      java ${SEARCH_INDEXER_OPT} -jar `dirname ${SEARCH_INDEXER_TAR}`/search-indexer-1.0-SNAPSHOT.jar ${SEARCH_INDEXER_COMMAND_OPTIONS}
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
