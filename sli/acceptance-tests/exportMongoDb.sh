#!/bin/bash

if [ ! $1 ]; then
        echo " Example of use: $0 database_name [dir_to_store]"
        exit 1
fi
db=$1
out_dir=$2
if [ ! $out_dir ]; then
        out_dir="./"
else
        mkdir -p $out_dir
fi
find $out_dir -type f -not -name '*custom_entities.json' -and -not -name '.gitignore' -delete
tmp_file="fadlfhsdofheinwvw.js"
echo "print('_ ' + db.getCollectionNames())" > $tmp_file
cols=`mongo $db $tmp_file | grep '_' | awk '{print $2}' | tr ',' ' '`
for c in $cols
do
    if [ "$c" != "custom_entities" ]; then
        mongoexport -d $db -c $c -o "$out_dir/exp_${db}_${c}.json"
    fi
done
rm $tmp_file
