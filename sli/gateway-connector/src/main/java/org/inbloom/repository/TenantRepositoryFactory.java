package org.inbloom.repository;


import com.mongodb.Mongo;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;

/**
 * Used to generate a repository object for a tenant's DB
 * @author ben morgan
 */
public class TenantRepositoryFactory {

    Mongo mongo;
    MongoConverter converter;

    public TenantRepositoryFactory(Mongo mongo, MongoConverter converter)
    {
        this.mongo = mongo;
        this.converter = converter;
    }

    public EducationOrganizationRepository getEdOrgRepository(String dbName)
    {
        try {
            MongoDbFactory dbFactory = new SimpleMongoDbFactory(mongo, dbName);
            MongoOperations mongoOps = new MongoTemplate(dbFactory, converter);

            MongoRepositoryFactory tenantRepoFactory = new MongoRepositoryFactory(mongoOps);

            return tenantRepoFactory.getRepository(EducationOrganizationRepository.class);

        } catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}