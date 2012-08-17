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

package org.slc.sli.dal;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Way to retry mongo commands
 *
 * @author tshewchuk
 *
 */
public class RetryMongoCommand {
    private final MongoTemplate template;
    private final Logger LOG;

    public RetryMongoCommand(MongoTemplate template, Logger log) {
        this.template = template;
        this.LOG = log;
    }

    /**
     * Retry inserting a record n times
     *
     * @param record
     *        record to insert
     * @param collectionName
     *        name of collection to insert new record
     *
     * @return int
     *        number of insert attempts, or 0 if unsuccessful
     */
    public <T> int insertWithRetries(T record, String collectionName) {
        int tries = 0;
        return tries;
    }

    /**
     * Retry inserting a records batch n times
     *
     * @param records
     *        records batch to insert
     * @param collectionName
     *        name of collection to insert new record
     *
     * @return int
     *        number of insert attempts, or 0 if unsuccessful
     */
    public <T> int insertWithRetries(List<T> records, String collectionName) {
        int tries = 0;
        return tries;
    }

    /**
     * Retry finding the first record in a collection n times
     *
     * @param record
     *        record to insert
     * @param collectionName
     *        name of collection to insert new record
     *
     * @return int
     *        number of insert attempts, or 0 if unsuccessful
     */
    public <T> int findOneWithRetries(Query query, Class<T> entityClass, String collectionName) {
        int tries = 0;
        return tries;
    }
}
