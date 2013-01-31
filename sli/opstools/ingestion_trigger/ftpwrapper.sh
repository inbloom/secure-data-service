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

# Ensure file is a valid zip file in user's home directory before sending.
if [ "${FILESPEC}" = "/`basename ${FILESPEC}`" ] && \
   [ "`echo ${FILESPEC} | awk -F . '{if (NF > 1) print $NF}'`" = "zip" ] && \
   [ -n "`echo ${FILESPEC} | awk -F . '{if (NF > 1) print $(NF - 1)}'`" ] && \
   ( zip -T ${CHROOTDIR}${FILESPEC} >/dev/null )
then
  exec ${PUBLISH} ${FTPCOMMAND} ${CHROOTDIR}${FILESPEC} ${AMQSERVER}
else
  echo "Error: ${CHROOTDIR}${FILESPEC} is not a valid landing_zone/zip_file."
fi
