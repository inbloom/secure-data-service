# use sli
# db.system.profile.drop()
# db.createCollection("system.profile", {capped:true, size:1024000000});
# db.setProfilingLevel(2)
#
# mongodump --db sli --collection system.profile
# bsondump dump/sli/system.profile.bson > system.profile.json
# python parse-profile-dump.py system.profile.json [current_sli_indexes]

import sys
import json
import re

ignore = [[], ["_id"], ["$msg"]] #indexes to ignore

indexes = []
index_strings = []
queries = []
crnt_indexes = []

if len(sys.argv) > 1:
    jsonfile = open(sys.argv[1])
    queries = jsonfile.read().splitlines()
    if len(sys.argv) > 2:
        indexfile = open(sys.argv[2])
        crnt_indexes = indexfile.read().splitlines()
else:
    print "python parse-profile-dump.py system.profile.json [current_sli_indexes]"
    exit(0)

def handle_new_index(ns, keys, query):
    if keys not in ignore:
        new_index = (ns, keys, query)
        indexes.append(new_index)

def handle_query(json_object, query):
    ns = json_object['ns']
    query_keys = json_object['query'].keys()
    # if it's an $or query, handle each disjunct
    # TODO - ordering in $or disjuncts? (currently uses the first disjunct during output below)
    if '$or' in query_keys:
        for or_query_key in json_object['query']['$or']:
            handle_new_index(ns, or_query_key.keys(), query)
    else:
        handle_new_index(ns, query_keys, query)

def handle_count(json_object, query):
    ns = json_object['command']['count']
    count_query = json_object['command']['query']
    if len(count_query.keys()) > 0:
        handle_new_index(ns, count_query.keys(), query)

# iterate through lines of json, looking for query operations, ignoring
# sli.system and sli.custom
for query in queries:
    # reformat the date, don't care about the actual value
    query = re.sub(r'Date\(\s\d*\s\)', '"date"', query)

    if 'sli.system' in query or 'sli.custom' in query:
        continue

    json_object = json.loads(query)

    if 'op' in json_object:
        if json_object['op'] == 'query':
            handle_query(json_object, query)
        elif json_object['op'] == 'command' and 'count' in json_object['command'] and 'query' in json_object['command']:
            handle_count(json_object, query)

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
