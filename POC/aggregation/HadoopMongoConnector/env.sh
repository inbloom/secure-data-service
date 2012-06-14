#!/bin/sh

export HADOOP_HOME=`pwd`/hadoop-0.20.2-cdh3u3
export JAVA_HOME=$(/usr/libexec/java_home)
export PATH=$PATH:$HADOOP_HOME/bin
export HADOOP_OPTS="-Djava.security.krb5.realm=OX.AC.UK -Djava.security.krb5.kdc=kdc0.ox.ac.uk:kdc1.ox.ac.uk"
