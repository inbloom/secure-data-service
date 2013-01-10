# use sli
# db.system.profile.drop()
# db.createCollection("system.profile", {capped:true, size:1024000000});
# db.setProfilingLevel(2)
#
# mongodump --db sli --collection system.profile
# bsondump dump/sli/system.profile.bson > system.profile.json
# python index-misses.py system.profile.json

import sys
import json
import re

queries = []
missed = 0

if len(sys.argv) > 1:
    jsonfile = open(sys.argv[1])
    queries = jsonfile.read().splitlines()
else:
    print "python index-misses.py system.profile.json"
    exit(0)

def missed_index(json_object):
    return 'nscanned' in json_object and 'nreturned' in json_object and json_object['nscanned'] > json_object['nreturned']

# iterate through lines of json, looking for query operations, ignoring
# sli.system and sli.custom
for query in queries:
    # reformat the date, don't care about the actual value
    query = re.sub(r'Date\(\s\d*\s\)', '"date"', query)

    if 'sli.system' in query or 'sli.custom' in query:
        continue

    json_object = json.loads(query)

    if missed_index(json_object):
        missed += 1
        print query

print "\n----------------------------------------"
print "Missed index on %d of %d commands." % (missed, len(queries))
