#!/bin/bash
#enable this for debug
#set -x

NO_HADOOP_CONFIG=false
NO_SSH_CONFIG=false

while getopts "nskh" opt; do
  case $opt in
    n)
      echo "Will skip hadoop configuration steps!" >&2
      NO_HADOOP_CONFIG=true
      ;;
    s)
      echo "Will skip ssh configuration steps!" >&2
      NO_SSH_CONFIG=true
      ;;
    k)
      echo "stop hadoop instance..."
      stop-all.sh
      exit
      ;;
    h)
      echo "start hadoop instance..."
      start-all.sh
      exit
      ;;
  esac
done

SLI_POC_AGG_HOME=`pwd`/..
if [ ! -e ${SLI_POC_AGG_HOME}/../../sli/pom.xml ]; then
    echo "Please run this script under {SLI_HOME}/POC/aggregation/hadoop directory"
    exit
fi

HOST_TYPE=`uname`
if [ 'Darwin' != ${HOST_TYPE} ]; then
    echo "Only works on mac, exiting..."
    exit
fi

brew --version
if [ $? != 0 ]; then
    echo "homebrew not installed, exiting..." 
    exit
fi

brew info hadoop 2>/dev/null | grep "Not installed" 
if [ $? -eq 0 ]; then
    echo "install hadoop..."
    brew install hadoop 
fi
echo "hadoop already installed..." 

if [ "${NO_HADOOP_CONFIG}" == "false" ]; then

    HADOOP_DIR=`brew --cellar hadoop`
    HADOOP_VER=`brew list --versions hadoop|head -n 1|awk '{print $2}'`
    HADOOP_CORE_CONF=`find ${HADOOP_DIR}/${HADOOP_VER} -name core-site.xml | head -n 1`
    HADOOP_CONF_DIR=`dirname ${HADOOP_CORE_CONF}`
    echo "Configuration folder for hadoop: ${HADOOP_CONF_DIR}"
    echo "backup hadoop configuration folder to : ${HADOOP_CONF_DIR}_bak"
    cp -pr ${HADOOP_CONF_DIR} ${HADOOP_CONF_DIR}_bak
    
    echo "Install configuration files for hadoop"
    sed "s,{SLI_POC_HOME},$SLI_POC_AGG_HOME," templates/core-site.xml.template > $HADOOP_CORE_CONF
    cp templates/hdfs-site.xml.template ${HADOOP_CONF_DIR}/hdfs-site.xml
    cp templates/mapred-site.xml.template ${HADOOP_CONF_DIR}/mapred-site.xml
    
    test -d ../app/hadoop/tmp || mkdir -p ../app/hadoop/tmp
    
    echo "format hadoop..."
    echo "Y" | hadoop namenode -format 
fi

if [ "${NO_SSH_CONFIG}" == "false" ]; then
    #Setup ssh for passwordless localhost access
    if [ ! -e ~/.ssh/id_rsa.pub ]; then
        echo "generating ssh key pair..."
        ssh-keygen -t rsa -P ""
    fi
    
    echo "Update localhost's authorized keys to include public key..."
    cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys

    echo "update ssh's known host list to include localhost"
    ssh-keygen -R localhost
    ssh-keyscan -t rsa localhost >> ~/.ssh/known_hosts
fi

echo "starting hadoop..."
start-all.sh
