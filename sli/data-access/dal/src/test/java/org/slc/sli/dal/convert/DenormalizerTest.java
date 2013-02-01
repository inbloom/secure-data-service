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

package org.slc.sli.dal.convert;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests
 *
 */
public class DenormalizerTest {

    private static final String BEGINDATE = "3001-09-01";
    private static final String ENDDATE1 = "3002-09-01";
    private static final String STUDENT1 = "b64ee2bcc92805cdd8ada6b7d8f9c643c9459831_id";
    private static final String SECTION1 = "1b223f577827204a1c7e9c851dba06bea6b031fe_id";
    private static final String STUDENT2 = "9876543";
    private static final String SECTION2 = "9999999";
    private static final String ENDDATE2 = "3002-09-01";
    private static final String SSAID = "555555";


    private final MongoTemplate template = mock(MongoTemplate.class);
    private Denormalizer denormalizer = new Denormalizer(template); //class under test

    private final Map<String, Object> studentSectionAssociation = new HashMap<String, Object>();
    private final DBCollection studentCollection = mock(DBCollection.class);

    @Before
    public void setup() {
        studentSectionAssociation.put("sectionId", SECTION1);
        studentSectionAssociation.put("studentId", STUDENT1);
        studentSectionAssociation.put("beginDate", BEGINDATE);
        studentSectionAssociation.put("endDate", ENDDATE1);

        WriteResult success = mock(WriteResult.class);
        CommandResult successCR = mock(CommandResult.class);
        CommandResult failCR = mock(CommandResult.class);
        when(success.getLastError()).thenReturn(successCR);
        when(successCR.ok()).thenReturn(true);
        when(successCR.get("value")).thenReturn("updated");
        when(failCR.get("value")).thenReturn(null);
        when(failCR.get("result")).thenReturn(null);
        when(studentCollection.update(any(DBObject.class), any(DBObject.class), eq(false), eq(true), eq(WriteConcern.SAFE))).thenReturn(
                success);
        when(studentCollection.update(any(DBObject.class), any(DBObject.class), eq(true), eq(true), eq(WriteConcern.SAFE))).thenReturn(
                success);
        when(template.getCollection("student")).thenReturn(studentCollection);

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(SSAID));
        MongoEntity entity = new MongoEntity("studentSectionAssociation", studentSectionAssociation);
        when(template.findOne(eq(query), eq(Entity.class), eq("studentSectionAssociation"))).thenReturn(entity);
    }

    @Test
    public void testCreate() {
        MongoEntity entity = new MongoEntity("studentSectionAssociation", studentSectionAssociation);
        entity.getMetaData().put("tenantId", "TEST");
        assertTrue(denormalizer.denormalization("studentSectionAssociation").create(entity));
        verify(studentCollection).update(eq(BasicDBObjectBuilder.start("_id", STUDENT1).get()),
                argThat(new ArgumentMatcher<DBObject>() {

                    @Override
                    @SuppressWarnings("unchecked")
                    public boolean matches(Object argument) {
                        DBObject updateObject = (DBObject) argument;
                        Map<String, Object> push = (Map<String, Object>) updateObject.get("$pushAll");
                        if (push == null) {
                            return false;
                        }
                        Collection<Object> toPush = push.values();
                        if (toPush.size() != 1) {
                            return false;
                        }
                        Object[] sectionRefsToPush = (Object[]) toPush.iterator().next();
                        List<String> ssaIds = new ArrayList<String>(push.keySet());

                        return ((((Map<String, Object>) sectionRefsToPush[0]).get("_id").equals(SECTION1))
                                && (((Map<String, Object>) sectionRefsToPush[0]).get("endDate").equals(ENDDATE1))
                                && (((Map<String, Object>) sectionRefsToPush[0]).get("beginDate") == null)
                                && ssaIds.get(0).equals("section"));
                    }
                }), eq(true), eq(true), eq(WriteConcern.SAFE));
    }

    @Test
    public void testInsert() {
        Entity entity1 = new MongoEntity("studentSectionAssociation", new HashMap<String, Object>(studentSectionAssociation));
        entity1.getBody().put("sectionId", SECTION2);
        entity1.getBody().put("endDate", ENDDATE2);
        entity1.getMetaData().put("tenantId", "TEST");

        Entity entity2 = new MongoEntity("studentSectionAssociation", new HashMap<String, Object>(studentSectionAssociation));
        entity2.getBody().put("studentId", STUDENT2);
        entity2.getMetaData().put("tenantId", "TEST");
        assertTrue(denormalizer.denormalization("studentSectionAssociation").insert(Arrays.asList(entity1, entity2)));

        verify(studentCollection).update(eq(BasicDBObjectBuilder.start("_id", STUDENT2).get()),
                argThat(new ArgumentMatcher<DBObject>() {

                    @Override
                    @SuppressWarnings("unchecked")
                    public boolean matches(Object argument) {
                        DBObject updateObject = (DBObject) argument;
                        Map<String, Object> push = (Map<String, Object>) updateObject.get("$pushAll");
                        if (push == null) {
                            return false;
                        }
                        Collection<Object> toPush = push.values();
                        if (toPush.size() != 1) {
                            return false;
                        }
                        Object[] sectionRefsToPush = (Object[]) toPush.iterator().next();
                        List<String> ssaIds = new ArrayList<String>(push.keySet());

                        return ((((Map<String, Object>) sectionRefsToPush[0]).get("_id").equals(SECTION1))
                                && (((Map<String, Object>) sectionRefsToPush[0]).get("endDate").equals(ENDDATE2))
                                && (((Map<String, Object>) sectionRefsToPush[0]).get("beginDate") == null)
                                && ssaIds.get(0).equals("section"));
                    }
                }), eq(true), eq(true), eq(WriteConcern.SAFE));

        verify(studentCollection).update(eq(BasicDBObjectBuilder.start("_id", STUDENT1).get()),
                argThat(new ArgumentMatcher<DBObject>() {

                    @Override
                    @SuppressWarnings("unchecked")
                    public boolean matches(Object argument) {
                        DBObject updateObject = (DBObject) argument;
                        Map<String, Object> push = (Map<String, Object>) updateObject.get("$pushAll");
                        if (push == null) {
                            return false;
                        }
                        Collection<Object> toPush = push.values();
                        if (toPush.size() != 1) {
                            return false;
                        }
                        Object[] sectionRefsToPush = (Object[]) toPush.iterator().next();
                        List<String> ssaIds = new ArrayList<String>(push.keySet());

                        return ((((Map<String, Object>) sectionRefsToPush[0]).get("_id").equals(SECTION2))
                                && (((Map<String, Object>) sectionRefsToPush[0]).get("endDate").equals(ENDDATE2))
                                && (((Map<String, Object>) sectionRefsToPush[0]).get("beginDate") == null)
                                && ssaIds.get(0).equals("section"));
                    }
                }), eq(true), eq(true), eq(WriteConcern.SAFE));
    }

    @Test
    public void testDelete() {
        MongoEntity entity = new MongoEntity("studentSectionAssociation", studentSectionAssociation);
        boolean result = denormalizer.denormalization("studentSectionAssociation").delete(entity, entity.getEntityId());
        assertTrue(result);

        result = denormalizer.denormalization("studentSectionAssociation").delete(null, SSAID);
        assertTrue(result);

        result = denormalizer.denormalization("studentSectionAssociation").delete(null, "invalidID");
        assertFalse(result);
    }

}
