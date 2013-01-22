import pymongo
import common 
import sys 

if __name__=="__main__":
    host = sys.argv[1] if len(sys.argv) > 1 else common.MONGO_HOST

    con = pymongo.Connection(host)
    con.drop_database(common.PERF_DB)

