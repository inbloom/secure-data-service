# requires python 2.7+

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
import collections
from copy import deepcopy

IGNORE_IDX = [[], ["_id"], ["_id", "_id"], ["$msg"]] #indexes to ignore
QUERY_OPS = ['query', 'remove', 'update'] #'getmore' op ignored

TENANT_ID = "metaData.tenantId"

def is_count_cmd(json_object):
    return json_object['op'] == 'command' and 'count' in json_object['command']

def build_query_list(query_json_object):
    indexes = []
    for key in query_json_object:
        if key == '$or':
            new_indexes = []
            or_array = query_json_object[key]
            for ora in or_array:
                new_or_indexes = build_query_list(ora.keys())
                copy_of_indexes = deepcopy(indexes)
                for coi in copy_of_indexes:
                    coi.extend(new_or_indexes[0])
                new_indexes.extend(copy_of_indexes)
            indexes = new_indexes
        else:
            if len(indexes) == 0:
                indexes.append([key])
            else:
                for idx in indexes:
                    idx.append(key)

    return indexes

def format_index(ns, idx):
    if TENANT_ID in idx and idx[0] != TENANT_ID:
        idx.remove(TENANT_ID)
        idx.insert(0, TENANT_ID)

    return "db[\"%s\"].ensureIndex({\"%s\":1});" % (ns.replace('sli.',''), "\":1,\"".join(idx))

def handle_query(json_object, query):
    indexes = []
    ns = json_object['ns']
    query_object = json_object['query']
    if 'query' in query_object:
        query_object = query_object['query']
    new_indexes = build_query_list(query_object)
    for idx in new_indexes:
        if idx not in IGNORE_IDX:
            indexes.append(format_index(ns, idx))
    return indexes

def handle_count(json_object, query):
    indexes = []
    ns = json_object['command']['count']
    new_indexes = build_query_list(json_object['command']['query'])
    for idx in new_indexes:
        if idx not in IGNORE_IDX:
            indexes.append(format_index(ns, idx))
    return indexes

def remove_crnt_indexes(indexes, crnt_indexes):
    new_indexes = []
    for idx in indexes:
        if idx not in crnt_indexes:
            new_indexes.append(idx)

    return new_indexes

def parse_profile_dump(queries, crnt_indexes):
    indexes = []
    for query in queries:
        # reformat the date, don't care about the actual value
        query = re.sub(r'Date\(\s\d*\s\)', '"date"', query)
        json_object = json.loads(query, object_pairs_hook=collections.OrderedDict)

        if 'op' in json_object:
            if json_object['op'] in QUERY_OPS:
                indexes.extend(handle_query(json_object, query))
            elif is_count_cmd(json_object) and 'query' in json_object['command']:
                indexes.extend(handle_count(json_object, query))

    indexes = remove_crnt_indexes(indexes, crnt_indexes)

    indexes = sorted(set(indexes))
    for idx in indexes:
        print idx

def main():
    if len(sys.argv) > 1:
        jsonfile = open(sys.argv[1])
        queries = jsonfile.read().splitlines()
        jsonfile.close()
        crnt_indexes = []
        if len(sys.argv) > 2:
            indexfile = open(sys.argv[2])
            crnt_indexes = indexfile.read().splitlines()
            indexfile.close()
        parse_profile_dump(queries, crnt_indexes)
    else:
        print "python parse-profile-dump.py system.profile.json [current_sli_indexes]"
        exit(0)


if __name__ == "__main__":
    main()
