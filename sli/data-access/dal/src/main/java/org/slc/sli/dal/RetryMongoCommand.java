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

package org.slc.sli.dal;

import com.mongodb.MongoException;
import com.mongodb.MongoException.DuplicateKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.mongodb.UncategorizedMongoDbException;

/**
 * Way to retry mongo commands
 *
 * @author Tom Shewchuk tshewchuk@wgen.net
 *
 */
public abstract class RetryMongoCommand {

    protected static final int MONGO_DUPLICATE_KEY_CODE_1 = 11000;
    protected static final int MONGO_DUPLICATE_KEY_CODE_2 = 11001;

    protected static final Logger LOG = LoggerFactory.getLogger(RetryMongoCommand.class);

    /**
     * Retry executing a mongo command until successful or retries are exhausted.
     *
     * @param noOfRetries
     *            Designated number of times to retry command.
     *
     * @return Object
     *         Return value of execute method.
     * @throws Exception
     */
    public Object executeOperation(int noOfRetries) throws MongoException, DataAccessResourceFailureException, InvalidDataAccessApiUsageException, InvalidDataAccessResourceUsageException, UncategorizedMongoDbException {
        int retryCounter = 0;

        while (retryCounter++ < noOfRetries) {
            try {
                return execute();
            }
            catch (MongoException ex) {
                if (ex instanceof DuplicateKey) {
                    throw ex;
                } 
                
                handleException(retryCounter, noOfRetries, ex);
                
            } catch (DataAccessResourceFailureException ex) {
                handleException(retryCounter, noOfRetries, ex);

            } catch (InvalidDataAccessApiUsageException ex) {
                handleException(retryCounter, noOfRetries, ex);

            } catch (InvalidDataAccessResourceUsageException ex) {
                handleException(retryCounter, noOfRetries, ex);

            } catch (UncategorizedMongoDbException ex) {
                handleException(retryCounter, noOfRetries, ex);

            }

        }
        return null;
    }

    private <T extends Exception> void handleException(int currentRetry, int totalRetries, T ex) throws T {

//        LOG.debug("Exception caught at attempt # " + currentRetry + " of " + totalRetries, ex);

        if (currentRetry >= totalRetries) {
            LOG.error("Retry attempts exhausted at {}", totalRetries);
            throw ex;
        }

    }

    /**
     * The method to be retried by executeOperation, implemented within mongo command code
     * elsewhere.
     *
     * @return Object
     *         Return value of execute method implemented within mongo command elsewhere.
     */
    public abstract Object execute();
}
