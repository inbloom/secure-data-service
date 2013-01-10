#!/usr/bin/env bash

PUBLISH="/opt/sli/bin/publish_file_uploaded.rb"

# ExecOnCommand STOR /opt/sli/bin/publish_file_uploaded.rb %m %f #ACTIVEMQSERVENAME#

# USERNAME is passed through mod_exec's %U key
USERNAME=$1 

# FTPCOMMAND is passed through mod_exec's %m key
FTPCOMMAND=$2

# FILESPEC is relative to the user's chroot, passed by mod_exec %f key
FILESPEC=$3

# AMQSERVER is needed to tell the publisher where to publish to
AMQSERVER=$4


# Derive the user's chroot path
CHROOTDIR=`/usr/bin/getent passwd ${USERNAME} | /usr/bin/cut -d ":" -f 6`

exec ${PUBLISH} ${FTPCOMMAND} ${CHROOTDIR}${FILESPEC} ${AMQSERVER}