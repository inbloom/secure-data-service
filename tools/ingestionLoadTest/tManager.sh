
#Make sure all FS Paths end with a /
#./tManager.sh -z "/ingestion/lz/inbound/" -t 10 -i LTEST -c -s UNKNOWN-NOTUSED #Create 10 LZs
#./tManager.sh -i '.' -d                                   #Delete ALL tenants
#./tManager.sh -i LTEST -d                                 #Delete the 10 tenants that were created above
#./tManager.sh -z "/ingestion/lz/inbound/" -t 10  -i LTEST -f MediumSampleDataSet.zip #Copy files to LZs

host='localhost'
tenants=0
path=`pwd`
tenantIdPrefix='LOADTest'
server=`hostname`
while getopts "h:cz:di:t:m:s:f:j" opt
do
    case $opt in 
        h) host=$OPTARG             ;;
        z) path=$OPTARG             ;;
        c) create=1                 ;;
        d) delete=1                 ;;
        t) tenants=$OPTARG          ;;
        i) tenantIdPrefix=$OPTARG   ;;
        s) server=$OPTARG           ;;
        f) copyFiles=$OPTARG        ;;
    esac
done
echo "Host:$host Count:$count Path:$path"

if [ "$delete" == 1 ]; then 
    echo "Deleting tenants!"
    eval="var tenantIdPrefix       ='$tenantIdPrefix'";
    mongo $host:27017/sli -eval "$eval" deleteTenants.js
fi
if [ "$create" == 1 ]; then 
    mpath=`echo "$path"|sed -e 's/\\\\/\\\\\\\\/g'`
    echo "Creating tenants!"
          eval="var tenantCount          =$tenants"
    eval="$eval;var tenantIdPrefix       ='$tenantIdPrefix'";
    eval="$eval;var tenantLandingZoneRoot='$mpath';"
    eval="$eval;var server               ='$server';"
    echo "$eval"
    mongo $host:27017/sli --eval "$eval" createTenants.js
fi
if [ ! -z $copyFiles ]; then 
    if [ -f $copyFiles ]; then
        find $path/*$tenantIdPrefix* -type d -exec cp $copyFiles '{}' \;  
    else 
        echo "Cannot copy $copyFiles. Does not exist!"
    fi
fi
