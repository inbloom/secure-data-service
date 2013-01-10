db["assessment"].ensureIndex({"_id":1});
db["attendance"].ensureIndex({"metaData.edOrgs":-1});