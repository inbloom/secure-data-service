import pymongo
import common 

if __name__=="__main__":
    con = pymongo.Connection(common.MONGO_HOST)
    con.drop_database(common.PERF_DB)

