/*
* Copyright 2012 Shared Learning Collaborative, LLC
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.slc.sli.ingestion.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * @author tke
 *
 */
public class MongoIndex {
    private String collection;
    private boolean unique;
    private DBObject keys = new BasicDBObject();

    public MongoIndex(String collection, boolean unique, DBObject keys) {
        this.collection = collection;
        this.unique = unique;
        this.keys = keys;
    }

    /**
     * constructor has been provided
     */
    public MongoIndex() {

    }

    public String getCollection() {
        return collection;
    }
    public void setCollection(String collection) {
        this.collection = collection;
    }
    public boolean isUnique() {
        return unique;
    }
    public void setUnique(boolean unique) {
        this.unique = unique;
    }
    public DBObject getKeys() {
        return keys;
    }
    public void setKeys(DBObject keys) {
        this.keys = keys;
    }

}
