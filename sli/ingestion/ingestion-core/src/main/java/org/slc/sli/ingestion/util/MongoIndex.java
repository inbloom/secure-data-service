/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

import java.util.Map;

import com.mongodb.DBObject;

/**
 * Structure for Mongo indexes
 *
 * @author tke
 *
 */
public class MongoIndex {
    private String collection;
    private boolean unique;
    private IndexObject keys;

    public MongoIndex(String collection, boolean unique, DBObject keys) {
        this.collection = collection;
        this.unique = unique;
        this.keys = new IndexObject(keys);
    }

    /**
     * constructor has been provided
     */
    public MongoIndex() {
        keys = new IndexObject();
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

    public Map<String, Integer> getKeys() {
        return keys.getKeys();
    }

    public void setKeys(IndexObject keys) {
        this.keys = keys;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((collection == null) ? 0 : collection.hashCode());
        result = prime * result + ((keys == null) ? 0 : keys.hashCode());
        result = prime * result + (unique ? 1231 : 1237);
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MongoIndex other = (MongoIndex) obj;
        if (collection == null) {
            if (other.collection != null) {
                return false;
            }
        } else if (!collection.equals(other.collection)) {
            return false;
        }
        if (keys == null) {
            if (other.keys != null) {
                return false;
            }
        } else  if (!keys.equals(other.keys)) {
            return false;
        }
        if (unique != other.unique) {
            return false;
        }
        return true;
    }
}
