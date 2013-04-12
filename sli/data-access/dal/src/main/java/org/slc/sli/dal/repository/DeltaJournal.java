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
package org.slc.sli.dal.repository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * Provides journaling of entity updates for delta processing
 *
 * @author nbrown
 *
 */
@Component
public class DeltaJournal {

    @Value("${deltasEnabled:false}")
    private boolean isEnabled;

    public static final String DELTA_COLLECTION = "deltas";
    @Autowired
    @Qualifier("journalTemplate")
    private MongoTemplate template;

    public void journal(List<String> ids, String collection, boolean isDelete) {
        if (isEnabled) {
            Date now = new Date();
            Update update = new Update();
            update.set("t", now);
            update.set("c", collection);
            if (isDelete) {
                update.set("d", now);
            } else {
                update.set("u", now);
            }
            for (String id : ids) {
                template.upsert(Query.query(Criteria.where("_id").is(id)), update, DELTA_COLLECTION);
            }
        }
    }

    public void journal(String id, String collection, boolean isDelete) {
        journal(Arrays.asList(id), collection, isDelete);
    }

}
