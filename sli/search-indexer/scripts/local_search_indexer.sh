#!/bin/bash

DEFAULT_CHECK_SLI_CONF="../config/properties/sli.properties"
DEFAULT_CHECK_KEYSTORE="../data-access/dal/keyStore/ciKeyStore.jks"
DEFAULT_SEARCH_INDEXER_JAR="target/search-indexer-1.0-SNAPSHOT.jar"
DEFAULT_MAX_MEMORY="1024m"
DEFAULT_MIN_MEMORY="1024m"

SEARCH_INDEXER_OPT=""
SEARCH_INDEXER_COMMAND_OPTIONS=""
SYSTEM_PROPERTIES_LOCK_DIR=""

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

SEARCH_INDEXER_LOG="search-indexer.log"


#Color
BRed='\e[1;31m'
BGreen='\e[1;32m'
Color_off='\e[0m'

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
   elif [ ${1:0:2} == "-X" ]; then
      PROPERTY=${1:2:2}
      if [ ${PROPERTY} == "mx" ]; then
         DEFAULT_MAX_MEMORY=${1:4}
      elif [ ${PROPERTY} == "ms" ]; then
         DEFAULT_MIN_MEMORY=${1:4}
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
      echo -e "${BGreen}Reading default ${SLI_CONF} [${DEFAULT_CHECK_SLI_CONF}]${Color_off}"
      if [ ! -f ${CHECK_SLI_CONF} ]; then
         echo -e "${BRed}File does not exit '${CHECK_SLI_CONF}'${Color_off}"
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
      echo -e "${BGreen}Reading default ${SLI_CONF} [${DEFAULT_CHECK_SLI_CONF}]${Color_off}"
      if [ ${CHECK_KEYSTORE} == 0 ]; then
         CHECK_KEYSTORE=${DEFAULT_CHECK_KEYSTORE}
      fi
      echo -e "${BGreen}Reading default keyStore [${DEFAULT_CHECK_KEYSTORE}]${Color_off}"
      for FILE_LOCATION in "${CHECK_SLI_CONF}" "${CHECK_KEYSTORE}"
      do
         if [ ! -f ${FILE_LOCATION} ]; then
            echo -e "${BRed}File does not exit: '${FILE_LOCATION}'${Color_off}"
            return 0
         fi
      done
      if [ ${CHECK_SEARCH_INDEXER_TAR} == 0 ]; then
         if [ ! -f ${DEFAULT_SEARCH_INDEXER_JAR} ];then
            echo -e "${BRed}Please specify search_indexer.tar.gz${Color_off}"
            return 0;
         fi
      else
         if [ ! -f ${CHECK_SEARCH_INDEXER_TAR} ]; then
            echo -e "${BRed}File [${CHECK_SEARCH_INDEXER_TAR}] does not exist${Color_off}"
            return 0
         fi
      fi
      INDEXER_LOCK=`dirname ${DEFAULT_SEARCH_INDEXER_JAR}`/data/indexer.lock
      if [ -f ${INDEXER_LOCK} ]; then
         echo -e "${BRed}Lock file still exist [${INDEXER_LOCK}]${Color_off}"
         return 0
      fi
      SEARCH_INDEXER_OPT="${SEARCH_INDEXER_OPT} -D${SLI_CONF}=${CHECK_SLI_CONF} -D${SLI_ENCRYPTION_KEYSTORE}=${CHECK_KEYSTORE}"
      if [ -z ${SYSTEM_PROPERTIES_LOCK_DIR:=""} ]; then
         SYSTEM_PROPERTIES_LOCK_DIR=`grep sli.search.indexer.dir.data ${CHECK_SLI_CONF}|cut -d '=' -f2|sed 's/ *$//g'|sed 's/^ *//g'`
         SEARCH_INDEXER_OPT="${SEARCH_INDEXER_OPT} -Dlock.dir=${SYSTEM_PROPERTIES_LOCK_DIR}"
      fi
      if [ -n ${DEFAULT_MAX_MEMORY:=""} ]; then
         SEARCH_INDEXER_OPT="${SEARCH_INDEXER_OPT} -Xmx${DEFAULT_MAX_MEMORY}"
      fi
      if [ -n ${DEFAULT_MIN_MEMORY:=""} ]; then
         SEARCH_INDEXER_OPT="${SEARCH_INDEXER_OPT} -Xms${DEFAULT_MIN_MEMORY}"
      fi
      SEARCH_INDEXER_LOG_PATH=`grep sli.search.indexer.log.path ${CHECK_SLI_CONF}|cut -d '=' -f2|sed 's/ *$//g'|sed 's/^ *//g'`
      if [ -n ${SEARCH_INDEXER_LOG_PATH:=""} ]; then
         SEARCH_INDEXER_LOG="${SEARCH_INDEXER_LOG_PATH}/${SEARCH_INDEXER_LOG}"
      fi
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
         echo -e "${BGreen}Stopping.... accessing port ${REMOTE_COMMAND_PORT}${Color_off}"
         exec 3<>/dev/tcp/127.0.0.1/"${REMOTE_COMMAND_PORT}"
		 echo "stop" >&3
		 
		 read -r msg_in <&3
		 echo "$msg_in"
      else
         echo -e "${BRed}Could not find 'sli.search.indexer.service.port' from ${CHECK_SLI_CONF}${Color_off}"
      fi
   elif [ ${RUN_EXTRACT} == 1 ]; then
      if [ ${REMOTE_COMMAND_PORT} != 0 ]; then

         echo -e "${BGreen}Extracting.... accessing port ${REMOTE_COMMAND_PORT}${Color_off}"
         exec 3<>/dev/tcp/127.0.0.1/"${REMOTE_COMMAND_PORT}"
		 echo "extract sync" >&3
		 
		 read -r msg_in <&3
		 echo "$msg_in"
      else
         echo -e "${BRed}Could not find 'sli.search.indexer.service.port' from ${CHECK_SLI_CONF}${Color_off}"
      fi
   elif [ ${RUN_START} == 1 ]; then
      prepareJava
      if [ ${CHECK_SEARCH_INDEXER_TAR} == 0 ]; then
         echo java ${SEARCH_INDEXER_OPT} -jar ${DEFAULT_SEARCH_INDEXER_JAR} ${SEARCH_INDEXER_COMMAND_OPTIONS}
         nohup java ${SEARCH_INDEXER_OPT} -jar ${DEFAULT_SEARCH_INDEXER_JAR} ${SEARCH_INDEXER_COMMAND_OPTIONS} >> ${SEARCH_INDEXER_LOG} 2>&1 &
      else
         echo java ${SEARCH_INDEXER_OPT} -jar `dirname ${CHECK_SEARCH_INDEXER_TAR}`/search-indexer-1.0-SNAPSHOT.jar ${SEARCH_INDEXER_COMMAND_OPTIONS}
         nohup java ${SEARCH_INDEXER_OPT} -jar `dirname ${CHECK_SEARCH_INDEXER_TAR}`/search-indexer-1.0-SNAPSHOT.jar ${SEARCH_INDEXER_COMMAND_OPTIONS} >> ${SEARCH_INDEXER_LOG} 2>&1 &
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
