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

package org.slc.sli.aggregation.util;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

/**
 * IdByNameLookup - collection of helper functions to look up entity Ids by name.
 */
public class IdByNameLookup {

    /**
     * getAssessmentId - Look up an assessment ID given the assessments identification code ID.
     *
     * @param assmtIDCode - ID code to look up
     * @return assessment identifier
     * @throws UnknownHostException
     */
    public static String getAssessmentId(String assmtIDCode) throws UnknownHostException {

        // TODO - parameterize these values.
        Mongo m = new Mongo("localhost");
        DB db = m.getDB("sli");
        DBCollection assessments = db.getCollection("assessment");
        DBObject assmt = assessments.findOne(new BasicDBObject("body.assessmentIdentificationCode.ID", assmtIDCode));
        String assmtId = (String) assmt.get("_id");
        return assmtId;
    }
}
