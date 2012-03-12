package org.slc.sli.ingestion.dal;

import java.net.UnknownHostException;

import com.mongodb.Mongo;
import com.mongodb.MongoException;

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

    private MongoConverter neutralRecordMappingConverter;
    
    public StagingMongoTemplate(String databaseName, MongoConverter neutralRecordMappingConverter) throws UnknownHostException, MongoException {
        super(new SimpleMongoDbFactory(new Mongo(), databaseName), neutralRecordMappingConverter);
    }
    
    public void setNeutralRecordMappingConverter(MongoConverter neutralRecordMappingConverter) {
        this.neutralRecordMappingConverter = neutralRecordMappingConverter;
    }
    
    public MongoConverter getNeutralRecordMappingConverter() {
        return this.neutralRecordMappingConverter;
    }
    
}
