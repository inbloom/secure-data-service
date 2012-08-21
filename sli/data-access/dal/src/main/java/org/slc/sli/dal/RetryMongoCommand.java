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
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

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
    protected int totalRetries;

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
    public Object executeOperation(int noOfRetries) throws Exception {
        totalRetries = noOfRetries;
        Exception except = null;
        while (noOfRetries > 0) {
            try {
                Object result = execute();
                return result;
            } catch (MongoException me) {
                noOfRetries = handleException(me.getCode(), noOfRetries, me);
                except = me;
            } catch (DataAccessResourceFailureException ex) {
                noOfRetries = handleException(0, noOfRetries, ex);
                except = ex;
            } catch (InvalidDataAccessApiUsageException ex) {
                noOfRetries = handleException(0, noOfRetries, ex);
                except = ex;
            } catch (InvalidDataAccessResourceUsageException ex) {
                noOfRetries = handleException(0, noOfRetries, ex);
                except = ex;
            }
        }
        LOG.error("RetryMongoCommand: Retry attempts exhausted at {}", totalRetries);
        throw except;
    }

    private int handleException(int code, int noOfRetries, Exception ex) throws MongoException {
        int retryNum = (totalRetries - noOfRetries) + 1;
        LOG.debug("RetryMongoCommand: Exception caught at attempt #" + retryNum + " of " + totalRetries, ex);
        if (code == MONGO_DUPLICATE_KEY_CODE_1 | code == MONGO_DUPLICATE_KEY_CODE_2) {
            throw (MongoException) ex;
        }
        return --noOfRetries;

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
