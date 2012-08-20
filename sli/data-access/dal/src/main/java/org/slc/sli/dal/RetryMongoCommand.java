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
/**
 * Way to retry mongo commands
 *
 * @author tshewchuk
 *
 */
public abstract class RetryMongoCommand {
    public Object executeOperation(int noOfRetries) throws Exception {
        Object result = null;
        while (noOfRetries > 0) {
            try {
               result = execute();
                break;
            } catch (MongoException me) {
                if (me.getCode() == 11000) {
                    break;
                }
                noOfRetries--;
            } catch (Exception ex) {
                throw ex;
            }
        }
        return result;
    }

    public abstract Object execute();
}
