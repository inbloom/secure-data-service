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


package org.slc.sli.dal.repository.connection;

import com.mongodb.DB;
import com.mongodb.Mongo;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author okrook
 *
 */
public class TenantAwareMongoDbFactoryTests {

    @Test
    public void testGetTenantConnection() {
        Mongo mongo = Mockito.mock(Mongo.class);
        DB db = Mockito.mock(DB.class);

        Mockito.when(db.getMongo()).thenReturn(mongo);
        Mockito.when(db.getName()).thenReturn("System");

        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);

        TenantAwareMongoDbFactory cm = new TenantAwareMongoDbFactory(mongo, "System");

        Assert.assertNotNull(cm.getDb());
        Assert.assertSame("System", cm.getDb().getName());
    }
}
