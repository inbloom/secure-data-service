package org.slc.sli.ingestion.dal;

import java.net.UnknownHostException;

import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.CommandResult;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;


/**
 *
 * Wrapper of MongoTemplate with configurable database names
 *
 * @author ifaybyshev
 *
 */
public class StagingMongoTemplate extends MongoTemplate {

    private String databasePrefix;

    private MongoConverter neutralRecordMappingConverter;

    public StagingMongoTemplate(String databaseName, MongoConverter neutralRecordMappingConverter) throws UnknownHostException, MongoException {
        super(new SimpleMongoDbFactory(new Mongo(), databaseName), neutralRecordMappingConverter);
        this.neutralRecordMappingConverter = neutralRecordMappingConverter;
        this.setDatabasePrefix(databaseName);
    }

    public StagingMongoTemplate(String databasePrefix, String batchJobId, MongoConverter neutralRecordMappingConverter) throws UnknownHostException, MongoException {
        super(new SimpleMongoDbFactory(new Mongo(), databasePrefix + "_" + removeUnsupportedChars(batchJobId)), neutralRecordMappingConverter);
        this.neutralRecordMappingConverter = neutralRecordMappingConverter;
        this.setDatabasePrefix(databasePrefix);
    }

    public static String removeUnsupportedChars(String data) {
        return data.substring(data.length() - 51, data.length()).replace("-", "");
    }

    public void dropDb() {
        this.getDb().dropDatabase();
    }

    public void setNeutralRecordMappingConverter(MongoConverter neutralRecordMappingConverter) {
        this.neutralRecordMappingConverter = neutralRecordMappingConverter;
    }

    public MongoConverter getNeutralRecordMappingConverter() {
        return this.neutralRecordMappingConverter;
    }

    public String getDatabasePrefix() {
        return databasePrefix;
    }

    public void setDatabasePrefix(String databasePrefix) {
        this.databasePrefix = databasePrefix;
    }

}
