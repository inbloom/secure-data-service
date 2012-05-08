package org.slc.sli.domain;

import com.mongodb.BasicDBObject;

/**
 * User: wscott
 */
public interface MongoEntityDecorator {
    public void decorate(BasicDBObject dbObj);
}
