import pymongo

PERF_DB = "apiPerf"
PERF_COLLECTION = "apiResponse"

if __name__=="__main__":
    con = pymongo.Connection()
    con.drop_database(PERF_DB)

