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
        DEFAULT_SEARCH_INDEXER_JAR="$ROOT/target/search-indexer-1.0-SNAPSHOT.jar"
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

        SEARCH_INDEXER_LOG="search-indexer.log"
    fi
}

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
         echo Setting check search indexer tar to: ${CHECK_SEARCH_INDEXER_TAR}
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
         elif [ ${1} == "restart" ]; then
            RUN_STOP=1
            RUN_START=1
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
      echo "Reading ${CONFIG_SETTING} ${SLI_CONF} [${DEFAULT_CHECK_SLI_CONF}]"
      if [ ! -f ${CHECK_SLI_CONF} ]; then
         echo "File does not exit ${CHECK_SLI_CONF}"
         return 0
      fi
      SEARCH_INDEXER_OPT="${SEARCH_INDEXER_OPT} -D${SLI_CONF}=${CHECK_SLI_CONF}"
      REMOTE_COMMAND_PORT=`grep sli.search.indexer.service.port ${CHECK_SLI_CONF}|cut -d '=' -f2`
   fi

   ##CHECK FOR START/DEBUG
   if [ ${RUN_START} == 1 ]; then
      if [ ${CHECK_SLI_CONF} == 0 ]; then
         CHECK_SLI_CONF=${DEFAULT_CHECK_SLI_CONF}
      fi
      echo "Reading ${CONFIG_SETTING} ${SLI_CONF} [${DEFAULT_CHECK_SLI_CONF}]"
      if [ ${CHECK_KEYSTORE} == 0 ]; then
         CHECK_KEYSTORE=${DEFAULT_CHECK_KEYSTORE}
      fi
      echo "Reading ${CONFIG_SETTING} keyStore [${DEFAULT_CHECK_KEYSTORE}]"
      for FILE_LOCATION in "${CHECK_SLI_CONF}" "${CHECK_KEYSTORE}"
      do
         if [ ! -f ${FILE_LOCATION} ]; then
            echo "File does not exit: ${FILE_LOCATION}"
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
      INDEXER_LOCK=`dirname ${DEFAULT_SEARCH_INDEXER_JAR}`/data/indexer.lock
      if [ -f ${INDEXER_LOCK} ]; then
         echo "Lock file still exist [${INDEXER_LOCK}]"
         return 0
      fi
      SEARCH_INDEXER_OPT="${SEARCH_INDEXER_OPT} -D${SLI_CONF}=${CHECK_SLI_CONF} -D${SLI_ENCRYPTION_KEYSTORE}=${CHECK_KEYSTORE}"
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
    stat=0
    if [ ${RUN_HELP} == 1 ]; then
        show_help
        return $stat
    fi

    if [ ${REMOTE_COMMAND_PORT:=0} == 0 ]; then
        REMOTE_COMMAND_PORT=${DEFAULT_REMOTE_COMMAND_PORT}
    fi

    if [ ${RUN_STOP} == 1 ]; then
        if [ ${REMOTE_COMMAND_PORT} != 0 ]; then
            echo "Stopping.... accessing port ${REMOTE_COMMAND_PORT}"
            echo stop | nc 127.0.0.1 ${REMOTE_COMMAND_PORT}
            if [ ${RUN_START} == 1 ]; then
                echo "Restarting..."
                sleep 10
            fi
        else
            echo "Could not find 'sli.search.indexer.service.port' from ${CHECK_SLI_CONF}"
        fi
    fi

    echo "logging to ${SEARCH_INDEXER_LOG}"
    if [ ${RUN_START} == 1 ]; then

        prepareJava
        SEARCH_INDEXER_LOG_DIR=`dirname ${SEARCH_INDEXER_LOG}`

        if [ -n ${SEARCH_INDEXER_LOG_DIR:=""} ]; then
            if [ ! -d ${SEARCH_INDEXER_LOG_DIR} ]; then
                mkdir -p ${SEARCH_INDEXER_LOG_DIR} 
                if [ $? != 0 ]
                then
                    echo "Failed to create logs directory ${SEARCH_INDEXER_LOG_DIR}"
                fi
            fi
        fi

        if [ ${CHECK_SEARCH_INDEXER_TAR} == 0 ]; then
            jobString="java ${SEARCH_INDEXER_OPT} -jar ${DEFAULT_SEARCH_INDEXER_JAR} ${SEARCH_INDEXER_COMMAND_OPTIONS}"
        else 
            jobString="java ${SEARCH_INDEXER_OPT} -jar `dirname ${CHECK_SEARCH_INDEXER_TAR}`/search-indexer-1.0-SNAPSHOT.jar ${SEARCH_INDEXER_COMMAND_OPTIONS}"
        fi

        # first check to see of the process is running
        searchProc=$(ps -ef | grep "search-indexer.*.jar" | grep -v "grep")
        if  [[ -z "$searchProc" ]]
        then
            echo "starting process please wait for 10 seconds"
            nohup $jobString 1>> ${SEARCH_INDEXER_LOG} 2>&1 &
            stat=$?

            sleep 10
            portListener=$(netstat -an | grep ${DEFAULT_REMOTE_COMMAND_PORT} | grep LISTEN )
            if  [[ -z "$portListener" ]]
            then
                echo "process has failed to start a listener on port ${DEFAULT_REMOTE_COMMAND_PORT}"
                stat=2
            fi
        else
            portListener=$(netstat -an | grep ${DEFAULT_REMOTE_COMMAND_PORT} | grep LISTEN )
            if  [[ -z "$portListener" ]]
            then
                echo "Process is already running however port ${DEFAULT_REMOTE_COMMAND_PORT} does not have a listener.  Please run stop and try again"
                stat=3
            else
                echo "Process is already running and listening to port ${DEFAULT_REMOTE_COMMAND_PORT}, please run stop and then start to re-start"
                stat=4
            fi
        fi
    fi

    if [ ${RUN_EXTRACT} == 1 ]; then
        if [ ${REMOTE_COMMAND_PORT} != 0 ]; then
            echo "Extracting.... accessing port ${REMOTE_COMMAND_PORT}"
            echo extract sync | nc 127.0.0.1 ${REMOTE_COMMAND_PORT}
        else
            echo "Could not find 'sli.search.indexer.service.port' from ${CHECK_SLI_CONF}"
        fi
    fi

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

isJavaReady
EVAL=$?
if [ $EVAL == 0 ]; then
   show_help
   exit -1 
fi

#Execute
run
exitVal=$?
echo exiting with $exitVal
exit $exitVal
