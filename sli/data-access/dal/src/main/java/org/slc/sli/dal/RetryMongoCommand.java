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

import com.mongodb.MongoException;

import org.slf4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Way to retry mongo commands
 *
 * @author tshewchuk
 *
 */
public class RetryMongoCommand {
    private final MongoTemplate template;
    private final Logger LOG;

    private static final int MONGO_DUPLICATE_KEY_ERROR = 11000;
    private static int retryInterval;

    private Object record;
    private String collectionName;

    public RetryMongoCommand(MongoTemplate template, Logger log) {
        this.template = template;
        this.LOG = log;
        retryInterval = 1000;  // msec.  FOR NOW!!
    }


    /**
     * Retry n times performing a mongo template command
     *
     * @param command
     *            enumeration of mongo template command
     * @param tries
     *            number of attempts, or 0 if unsuccessful
     */
    private void retryTemplateCommand(int tries) {
        int retries = 1;
        while (retries <= tries) {
            try {
            } catch (MongoException me) {
                if (me.getCode() == MONGO_DUPLICATE_KEY_ERROR) {
                    break;
                }
                retries++;
            }
        }
    }
}
