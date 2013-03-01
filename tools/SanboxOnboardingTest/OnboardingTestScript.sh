if [ $# -ne 4 ] 
then
    echo "Incorrect Arguments"
    echo "Usage sh OnboardingTestScript <Mongo Host> <No of Tenants> <Dataset> <LZ Path>"
    exit
elif [ "$3" != "small" ]  && [ "$3" != "medium" ];
then
    echo $2
    echo "Incorrect Dataset Specified"
    exit
fi
mongo $1:27017/sli --eval "var total = $2; var dataSet = \"$3\"; var lzPath = \"$4/\"" OnboardingTestScript.js