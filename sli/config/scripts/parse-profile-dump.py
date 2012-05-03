# set profiling level to 2 in mongo (log everything)
# mongodump --db sli --collection system.profile
# bsondump dump/sli/system.profile.bson > dump/sli/system.profile.json
# python parse-profile-dump.py dump/sli/system.profile.json [current_sli_indexes]

import sys
import json
import re

ignore = [[], ["_id"], ["$msg"]] #indexes to ignore

indexes = []
index_strings = []
queries = []
crnt_indexes = []

if len(sys.argv) == 3:
    jsonfile = open(sys.argv[1])
    queries = jsonfile.read().splitlines()
    indexfile = open(sys.argv[2])
    crnt_indexes = indexfile.read().splitlines()
else:
    exit(0)

# iterate through lines of json, looking for query operations, ignoring
# sli.system and sli.custom
for query in queries:
    # reformat the date, don't care about the actual value
    query = re.sub(r'Date\(\s\d*\s\)', '"date"', query)

    if 'sli.system' in query or 'sli.custom' in query:
        continue

    json_object = json.loads(query)

    if 'op' not in json_object or json_object['op'] != 'query':
        continue

    if 'query' in json_object:
        query_keys = json_object['query'].keys()
        if query_keys not in ignore:
            new_index = (json_object['ns'], json_object['query'].keys(), query)
            indexes.append(new_index)

# order the indexes based on their order in the query, and generate a javascript
# command to create the index
for index in indexes:
    index_fields_sort = []
    table, index_fields, query = index
    table = table.replace('sli.','')

    for field in index_fields:
        index_fields_sort.append((query.find(field), field))

    index_fields_sort = sorted(index_fields_sort)
    order, index_fields = zip(*index_fields_sort)

    new_index_string = "db[\"%s\"].ensureIndex({\"%s\":1});" % (table, "\":1,\"".join(index_fields))
    if new_index_string not in index_strings:
        index_strings.append(new_index_string)

# sort the strings alphabetically, print
for str in sorted(index_strings):
    # do not print if index is already in index file
    if str not in crnt_indexes:
        print str
