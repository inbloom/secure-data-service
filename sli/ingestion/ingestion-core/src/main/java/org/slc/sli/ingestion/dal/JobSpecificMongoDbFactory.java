package org.slc.sli.ingestion.dal;

import com.mongodb.DB;
import com.mongodb.Mongo;

import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.util.BatchJobUtils;

public class JobSpecificMongoDbFactory extends SimpleMongoDbFactory {

    public JobSpecificMongoDbFactory(Mongo mongo, String databaseName) {
        super(mongo, databaseName);
    }

    @Override
    public DB getDb() throws DataAccessException {

        String jobId = TenantContext.getJobId();
        if (jobId == null) {
            return super.getDb();
        }

        String dbName = BatchJobUtils.jobIdToDbName(jobId);
        return super.getDb(dbName);
    }
}
