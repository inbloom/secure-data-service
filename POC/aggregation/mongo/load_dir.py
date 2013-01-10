#!/usr/bin/env python 

import os
import subprocess
import sys

def drop_database(database): 
    cmd = "mongo sli --eval 'db.dropDatabase();'"
    subprocess.check_output(cmd, shell=True)
    print "Database %s dropped." % database 

def run_mongo_import(database, collection, fname):
    cmd = "mongoimport -d %s -c %s %s" % (database, collection, fname)
    subprocess.check_output(cmd, shell=True)
    print "File %s imported." % fname 

def main(database, src_dir):
    # drop the database 
    drop_database(database)
    for fname in os.listdir(src_dir):
        if fname.endswith(".json"):
            collection = fname[:-len(".json")]
            run_mongo_import(database, collection, os.path.join(src_dir, fname))

if __name__=="__main__":
    if len(sys.argv) != 3:
        print "Usage: %s database input_directory" % sys.argv[0]
        sys.exit(1)
    main(sys.argv[1], sys.argv[2])

