package org.slc.sli.spring.data.mongodb;

import com.mongodb.Mongo;
import com.mongodb.MongoURI;
import com.mongodb.WriteConcern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

/**
 * This is spring factory bean that can be used to configure spring data mongo connections.
 *
 * This class allows the use of a mongoUri, and short circuits spring data's buggy configuration.
 *
 * All sorts of configuration options are available through the mongoUri. See the mongo driver
 * source for all the options and their syntax.
 *
 * The userName, password, dbName properties will override the contents of the mongoUri (if set).
 *
 * WriteConcern can also be set.
 */
public class MongoFactoryBean implements FactoryBean<MongoDbFactory> {

    /**
     * Logger, available to subclasses.
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String mongoUri;
    private String userName;
    private String password;
    private String dbName;
    private WriteConcern writeConcern;

    public void setMongoUri(String uri) {
        this.mongoUri = uri;
    }

    public void setWriteConcern(WriteConcern writeConcern) {
        this.writeConcern = writeConcern;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDbName(String db) {
        this.dbName = db;
    }

    @Override
    public MongoDbFactory getObject() throws Exception {

        MongoURI uri = new MongoURI(this.mongoUri);

        if (this.userName == null) {
            this.userName = uri.getUsername();
        }

        if (this.password == null) {
            this.password = new String(uri.getPassword());
        }

        if (this.dbName == null) {
            this.dbName = uri.getDatabase();
        }

        Mongo mongo = new Mongo(uri);

        if (this.writeConcern != null) {
            mongo.setWriteConcern(this.writeConcern);
        }

        SimpleMongoDbFactory mongoFact;
        if (this.userName != null && this.userName.trim().length() > 0) {
            mongoFact = new SimpleMongoDbFactory(mongo, this.dbName, new UserCredentials(this.userName, this.password));
        } else {
            mongoFact = new SimpleMongoDbFactory(mongo, this.dbName);
        }

        logger.info("MongoDbFactory created for {} with {} hosts.", this.dbName, uri.getHosts().size());
        return mongoFact;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    @Override
    public Class<? extends MongoDbFactory> getObjectType() {
        return MongoDbFactory.class;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    @Override
    public boolean isSingleton() {
        return true;
    }

}