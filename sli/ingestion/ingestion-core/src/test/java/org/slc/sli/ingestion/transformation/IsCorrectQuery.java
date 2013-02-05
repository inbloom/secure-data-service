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
package org.slc.sli.ingestion.transformation;

import java.util.List;

import com.mongodb.BasicDBObject;

import org.mockito.ArgumentMatcher;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Util class for Query match
 *
 * @author slee
 *
 */
public class IsCorrectQuery extends ArgumentMatcher<Query> {
    private Query query;

    public IsCorrectQuery(Query query) {
        this.query = query;
    }

    @Override
    public boolean matches(Object argument) {
        if (query != null) {
            return matches((Query) argument);
        }
        return false;
    }

    private boolean matches(Query arg) {
        String queryKey;
        String argKey;

        for (String key : query.getQueryObject().keySet()) {
            queryKey = query.getQueryObject().get(key).getClass().getSimpleName();
            argKey = arg.getQueryObject().get(key).getClass().getSimpleName();

            if (!queryKey.equals(argKey)) {
                return false;
            } else if (queryKey.equals("BasicDBObject")) {
                BasicDBObject queryObj = (BasicDBObject) query.getQueryObject().get(key);
                BasicDBObject argObj = (BasicDBObject) arg.getQueryObject().get(key);

//                System.out.println(key);
//                System.out.print("\t"+queryObj);
//                System.out.println(" "+argObj);

                for (String key2 : queryObj.keySet()) {
                    if (queryObj.get(key2).getClass() != argObj.get(key2).getClass())
                    {
                        return false;
                    } else if (key2.equals("$in")) {
                        List<?> queryVal = (List<?>) queryObj.get(key2);
                        List<?> argVal = (List<?>) argObj.get(key2);
                        if (queryVal.size() != argVal.size()) {
                            return false;
                        }
                        for (int i = 0; i < queryVal.size(); ++i) {
                            if (!(queryVal.get(i).equals(argVal.get(i)))) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            } else if (!query.getQueryObject().get(key).equals(arg.getQueryObject().get(key))) {
                return false;
            }
        }

        return true;
    }

}
