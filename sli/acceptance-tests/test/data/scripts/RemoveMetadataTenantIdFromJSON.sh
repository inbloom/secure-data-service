#!/bin/sh

if [ -z $1 ]
then
  dir="."
else
  dir=$1
fi

for file in `ls ${dir}/*.json | grep -v enant | grep -v ealm`
do
  sed "s/,[ ]*\"metaData\"[ ]*:[ ]*{[ ]*\"tenantId\"[ ]*:[ ]*\"[^\"]*\"[ ]*}//" ${file} | \
  sed "s/\"tenantId\"[ ]*:[ ]*\"[^\"]*\",[ ]*//" | \
  sed "s/,[ ]*\"tenantId\"[ ]*:[ ]*\"[^\"]*\"//" > ${file}.SED
  mv -f ${file}.SED ${file}
done
