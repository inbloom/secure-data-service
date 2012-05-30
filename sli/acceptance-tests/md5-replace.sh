#!/bin/sh

##############################################################
# This will replace the md5 checksum for each interchange file
# specified in the control file.
# Usage:
# > sh md5-replace.sh {path_to_control_file}
##############################################################

if test -z $1
then
  echo "No control file specified."
  exit
fi

ctl_file=$1
path=`dirname $ctl_file`
touch $path/new_file.ctl

for line in `cat $ctl_file`
do
  IFS=","
  set $line
  in_checksum=$4
  file=$path/$3
  checksum=`md5 $file | cut -d = -f 2`
  out_checksum=${checksum:1:${#checksum}}
  if [ "$in_checksum" = "$out_checksum" ]
  then
    echo "     MD5 for $3 is correct."
    echo $1,$2,$3,$in_checksum >> $path/new_file.ctl
  else
    echo ">>>> MD5 for $3 is incorrect. Replacing..."
    echo $1,$2,$3,$out_checksum >> $path/new_file.ctl
  fi
done

echo "Backing up old control file...$ctl_file -> $ctl_file.backup"
mv $ctl_file $ctl_file.backup
echo "Writing new control file..."
mv $path/new_file.ctl $ctl_file
echo "Done."
