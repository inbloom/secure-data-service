#!/bin/bash

while getopts "p:c:h:t:d:f:s:l:ir:" opt; do
  case $opt in
      h) host=$OPTARG        ;;
      p) port=$OPTARG        ;;
      d) db=$OPTARG          ;;
      c) collection=$OPTARG  ;;
      t) tenant=$OPTARG      ;;
      f) filter=$OPTARG      ;;
      s) select=$OPTARG      ;;
      l) limit=$OPTARG       ;;
      r) regex=$OPTARG       ;;
      i) 
          echo "$0 -c course     -s body.courseDescription"
          echo "$0 -c staffEducationOrganizationAssociation  -r 'name.firstName|nameOfInstitution'"
          echo "$0 -c assessment -f body.contentStandard=ACT -s body.revisionDate"
          echo "$0 -c student    -f _id=2012ai-bdd5790e-d6a4-11e1-beaf-e4115bf51015"
      echo "$0 -c student    -s body.oldEthnicity#list oldEthnicity for all students"
      echo "$0 -c tenant     -s body.landingZone #list all landingZone"
      echo "$0 -c student    -r updated #find update times for all students"
      echo "$0 -c staff      -r 'externalId|name'#find name and externalId for all staff"
      exit 0;
      ;;
      *)
          echo "Unknown Argument. Exiting. $0 -i for menu."
      exit 0;
      ;;
  esac
done

if [ -z "$port"         ];    then port=27017                ;fi 
if [ -z "$host"         ];    then host='localhost'          ;fi
if [ -z "$tenant"       ];    then tenant=''                 ;fi
if [ -z "$db"           ];    then db='sli'                  ;fi
if [ -z "$collection"   ];    then collections='student'     ;fi

mongo --eval "var collectionName='$collection';\
              var tenant   = '$tenant';\
          var filter   = '$filter';\
          var select   = '$select';\
          var limit    = '$limit';\
          var regex    = '$regex';"\
          $host:$port/$db _csv.js 
