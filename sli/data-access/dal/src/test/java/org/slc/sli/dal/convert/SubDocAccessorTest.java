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

package org.slc.sli.dal.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.validation.NoNaturalKeysDefinedException;
import org.slc.sli.validation.schema.INaturalKeyExtractor;

/**
 * Test for sub doc accessor
 *
 * @author nbrown
 *
 */
public class SubDocAccessorTest {

    private static final String BEGINDATE = "3001-09-01";
    private static final String STUDENT1 = "b64ee2bcc92805cdd8ada6b7d8f9c643c9459831_id";
    private static final String SECTION1 = "1b223f577827204a1c7e9c851dba06bea6b031fe_id";
    private static final String STUDENT2 = "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id";
    private static final String SECTION2 = "067198fd6da91e1aa8d67e28e850f224d6851713_id";
    private final MongoTemplate template = mock(MongoTemplate.class);
    private final DeterministicUUIDGeneratorStrategy uuidGenerator = mock(DeterministicUUIDGeneratorStrategy.class);
    private INaturalKeyExtractor naturalKeyExtractor = mock(INaturalKeyExtractor.class);
    private NaturalKeyDescriptor naturalKeyDescriptor = mock(NaturalKeyDescriptor.class);
    private final SubDocAccessor underTest = new SubDocAccessor(template, uuidGenerator, naturalKeyExtractor);
    private final Map<String, Object> studentSectionAssociation = new HashMap<String, Object>();
    private final DBCollection sectionCollection = mock(DBCollection.class);

    @Before
    public void setUp() throws NoNaturalKeysDefinedException {
        TenantContext.setTenantId("myTenant");
        studentSectionAssociation.put("sectionId", SECTION1);
        studentSectionAssociation.put("studentId", STUDENT1);
        studentSectionAssociation.put("beginDate", BEGINDATE);
        WriteResult success = mock(WriteResult.class);
        CommandResult successCR = mock(CommandResult.class);
        CommandResult failCR = mock(CommandResult.class);
        CommandResult countCR = mock(CommandResult.class);
        CommandResult countFailCR = mock(CommandResult.class);
        when(success.getLastError()).thenReturn(successCR);
        when(success.getN()).thenReturn(1);
        when(successCR.ok()).thenReturn(true);
        when(successCR.get("value")).thenReturn("updated");
        when(failCR.get("value")).thenReturn(null);
        when(failCR.get("result")).thenReturn(null);

        DBObject r = new BasicDBObject();
        r.put("count", 1);
        List<DBObject> l = new ArrayList<DBObject>();
        l.add(r);
        when(countCR.get("result")).thenReturn(l);

        when(countFailCR.get("result")).thenReturn(new ArrayList<DBObject>());

        when(
                sectionCollection.update(any(DBObject.class), any(DBObject.class), eq(false), eq(false),
                        eq(WriteConcern.SAFE))).thenReturn(success);
        when(
                sectionCollection.update(any(DBObject.class), any(DBObject.class), eq(true), eq(false),
                        eq(WriteConcern.SAFE))).thenReturn(success);
        when(template.getCollection("section")).thenReturn(sectionCollection);
        Map<String, Object> section = new HashMap<String, Object>();
        Map<String, Object> studentSectionAssociations = new HashMap<String, Object>();
        studentSectionAssociations.put(SECTION1 + STUDENT1, studentSectionAssociation);
        studentSectionAssociations.put(SECTION1 + STUDENT2, studentSectionAssociation);
        section.put("studentSectionAssociation", studentSectionAssociations);
        when(template.findOne(matchesParentId(SECTION1), eq(Map.class), eq("section"))).thenReturn(section);

        DBObject q = new BasicDBObject();
        q.put("_id", SECTION1);
        q.put("sectionId." + SECTION1 + STUDENT1, new BasicDBObject("$exists", true));
        when(sectionCollection.count(q)).thenReturn(1L);

        when(naturalKeyExtractor.getNaturalKeyDescriptor(argThat(new ArgumentMatcher<Entity>() {

            @Override
            public boolean matches(Object argument) {
                Entity entity = (Entity) argument;
                // TODO Auto-generated method stub
                return entity.getType().equals("studentSectionAssociation");
            }

        }))).thenReturn(naturalKeyDescriptor);
        when(uuidGenerator.generateId(naturalKeyDescriptor)).thenReturn("subdocid");
        String updateCommand = "{findAndModify:\"section\",query:{ \"_id\" : \"parent_id\" , \"studentSectionAssociation._id\" : \"parent_idchild\""
                + " , \"studentSectionAssociation.someProperty\" : \"someValue\" "
                + ", \"studentSectionAssociation\" : { \"$elemMatch\" : { \"_id\" : \"parent_idchild\" , \"someProperty\" : \"someValue\""
                + "}}},update:{ \"$set\" : { \"studentSectionAssociation.$.someProperty\" : \"someNewValue\"}}}";

        when(template.executeCommand(updateCommand)).thenReturn(successCR);

        String failUpdateCommand = "{findAndModify:\"section\",query:{ \"_id\" : \"parent_id\" , \"studentSectionAssociation._id\" : \"parent_idchild\""
                + " , \"studentSectionAssociation.nonExistProperty\" : \"someValue\" "
                + ", \"studentSectionAssociation\" : { \"$elemMatch\" : { \"_id\" : \"parent_idchild\" , \"nonExistProperty\" : \"someValue\""
                + "}}},update:{ \"$set\" : { \"studentSectionAssociation.$.nonExistProperty\" : \"someNewValue\"}}}";
        when(template.executeCommand(failUpdateCommand)).thenReturn(failCR);

        String queryCommand = "{aggregate : \"section\", pipeline:[{$match : { \"_id\" : \"parent_id\"}}"
                + ",{$project : {\"studentSectionAssociation\":1,\"_id\":0 } }"
                + ",{$unwind: \"$studentSectionAssociation\"},{$match:{ \"studentSectionAssociation._id\" : \"parent_idchild\""
                + ", \"studentSectionAssociation.someProperty\" : \"someValue\"}}]}";
        DBObject subDocQueryResult = new BasicDBObject();
        DBObject subDocEntity = new BasicDBObject();
        subDocEntity.put("_id", "parent_idchild");
        DBObject subDocBody = new BasicDBObject();
        subDocBody.put("someProperty", "someValue");
        DBObject subDocMetaData = new BasicDBObject();

        subDocEntity.put("body", subDocBody);
        subDocEntity.put("metaData", subDocMetaData);
        subDocQueryResult.put("studentSectionAssociation", subDocEntity);
        List<DBObject> subDocQueryResults = new ArrayList<DBObject>();
        subDocQueryResults.add(subDocQueryResult);
        when(successCR.get("result")).thenReturn(subDocQueryResults);
        when(template.executeCommand(queryCommand)).thenReturn(successCR);

        String findByID = "{aggregate : \"section\", pipeline:[{$match : { \"_id\" : \"parent_id\"}},"
                + "{$project : {\"studentSectionAssociation\":1,\"_id\":0 } },"
                + "{$unwind: \"$studentSectionAssociation\"},{$match : { \"studentSectionAssociation._id\" : \"parent_idchild\"}}]}";
        when(template.executeCommand(findByID)).thenReturn(successCR);

        String nonExistIdForDelete = "{aggregate : \"section\", pipeline:[{$match : { \"studentSectionAssociation._id\" : \"nonExistId\"}}"
                + ",{$project : {\"studentSectionAssociation\":1,\"_id\":0 } },{$unwind: \"$studentSectionAssociation\"},"
                + "{$match : { \"studentSectionAssociation._id\" : \"nonExistId\"}}]}";

        when(template.executeCommand(nonExistIdForDelete)).thenReturn(failCR);

        String findAllQueryCommand = "{aggregate : \"section\", pipeline:[{$match : { \"_id\" : \"parent_id\"}},"
                + "{$project : {\"studentSectionAssociation\":1,\"_id\":0 } },{$unwind: \"$studentSectionAssociation\"},"
                + "{$match : { \"studentSectionAssociation._id\" : \"parent_idchild\" , \"studentSectionAssociation.someProperty\" : \"someValue\"}},{$limit:1}]}";
        when(template.executeCommand(findAllQueryCommand)).thenReturn(successCR);

        String nonExistQueryCommand = "{aggregate : \"section\", pipeline:[{$match : { \"studentSectionAssociation._id\" : \"nonExistId\"}}"
                + ",{$project : {\"studentSectionAssociation\":1,\"_id\":0 } },{$unwind: \"$studentSectionAssociation\"},"
                + "{$match:{ \"studentSectionAssociation._id\" : \"nonExistId\"}}]}";
        when(template.executeCommand(nonExistQueryCommand)).thenReturn(failCR);

        String countQueryCommand = "{aggregate : \"section\", pipeline:[{$match : { \"_id\" : \"parent_id\"}},"
                + "{$project : {\"studentSectionAssociation\":1,\"_id\":0 } },{$unwind: \"$studentSectionAssociation\"},"
                + "{$match : { \"studentSectionAssociation._id\" : \"parent_idchild\" , \"studentSectionAssociation.someProperty\" : \"someValue\"}}, "
                + "{$group: { _id: null, count: {$sum: 1}}}]}";
        when(template.executeCommand(countQueryCommand)).thenReturn(countCR);

        String nonExistCountCommand = "{aggregate : \"section\", pipeline:[{$match : { \"_id\" : \"parent_id\"}},"
                + "{$project : {\"studentSectionAssociation\":1,\"_id\":0 } },{$unwind: \"$studentSectionAssociation\"},"
                + "{$match : { \"studentSectionAssociation._id\" : \"parent_idchild\" , \"studentSectionAssociation.nonExistProperty\" : \"someValue\"}}, "
                + "{$group: { _id: null, count: {$sum: 1}}}]}";
        when(template.executeCommand(nonExistCountCommand)).thenReturn(countFailCR);
    }

    @Test
    public void testSingleInsert() {
        MongoEntity entity = new MongoEntity("studentSectionAssociation", studentSectionAssociation);

        assertTrue(underTest.subDoc("studentSectionAssociation").create(entity));
        verify(sectionCollection).update(argThat(new ArgumentMatcher<DBObject>() {

            @Override
            public boolean matches(Object argument) {
                DBObject query = (DBObject) argument;
                DBObject sectionQuery = (DBObject) query.get("studentSectionAssociation._id");
                return query.get("_id").equals(SECTION1) && sectionQuery != null
                        && sectionQuery.get("$nin").equals(Arrays.asList("subdocid"));
            }

        }), argThat(new ArgumentMatcher<DBObject>() {

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
                Object[] studentSectionsToPush = (Object[]) toPush.iterator().next();
                List<String> ssaIds = new ArrayList<String>(push.keySet());
                return ((Map<String, Map<String, Object>>) studentSectionsToPush[0]).get("body").get("beginDate")
                        .equals(BEGINDATE)
                        && ssaIds.get(0).equals("studentSectionAssociation");
            }
        }), eq(true), eq(false), eq(WriteConcern.SAFE));

    }

    @Test
    public void testMultipleInsert() {
        Entity ssa1 = new MongoEntity("studentSectionAssociation", studentSectionAssociation);
        Entity ssa2 = new MongoEntity("studentSectionAssociation", new HashMap<String, Object>(
                studentSectionAssociation));
        ssa2.getBody().put("studentId", STUDENT2);
        Entity ssa3 = new MongoEntity("studentSectionAssociation", new HashMap<String, Object>(
                studentSectionAssociation));
        ssa3.getBody().put("sectionId", SECTION2);
        assertTrue(underTest.subDoc("studentSectionAssociation").insert(Arrays.asList(ssa1, ssa2, ssa3)));
        // Test that fry gets enrolled in mathematics of wonton burrito meals
        verify(sectionCollection).update(argThat(new ArgumentMatcher<DBObject>() {

            @Override
            public boolean matches(Object argument) {
                DBObject query = (DBObject) argument;
                DBObject sectionQuery = (DBObject) query.get("studentSectionAssociation._id");
                return query.get("_id").equals(SECTION2) && sectionQuery != null
                        && sectionQuery.get("$nin").equals(Arrays.asList("subdocid"));
            }

        }), argThat(new ArgumentMatcher<DBObject>() {

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
                Object[] studentSectionsToPush = (Object[]) toPush.iterator().next();
                List<String> ssaIds = new ArrayList<String>(push.keySet());
                return ((Map<String, Map<String, Object>>) studentSectionsToPush[0]).get("body").get("beginDate")
                        .equals(BEGINDATE)
                        && ssaIds.get(0).equals("studentSectionAssociation");
            }
        }), eq(true), eq(false), eq(WriteConcern.SAFE));
        // Test that both fry and gunther get enrolled in history of the 20th century
        verify(sectionCollection).update(argThat(new ArgumentMatcher<DBObject>() {

            @Override
            public boolean matches(Object argument) {
                DBObject query = (DBObject) argument;
                DBObject sectionQuery = (DBObject) query.get("studentSectionAssociation._id");
                return query.get("_id").equals(SECTION1) && sectionQuery != null
                        && sectionQuery.get("$nin").equals(Arrays.asList("subdocid", "subdocid"));
            }

        }), argThat(new ArgumentMatcher<DBObject>() {

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
                        Object[] studentSectionsToPush = (Object[]) toPush.iterator().next();
                        return studentSectionsToPush.length == 2;
                    }
                }), eq(true), eq(false), eq(WriteConcern.SAFE));

    }

    private Query matchesParentId(final String id) {
        Query matchesId = argThat(new ArgumentMatcher<Query>() {

            @Override
            public boolean matches(Object argument) {
                Query query = (Query) argument;
                DBObject queryObject = query.getQueryObject();
                return queryObject.get("_id").equals(id);
            }
        });
        return matchesId;
    }

    @Test
    public void testMakeSubDocQuery() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Query originalQuery = new Query(Criteria.where("_id").is("parent_idchild").and("someProperty").is("someValue"));
        DBObject parentQuery = underTest.subDoc("studentSectionAssociation").toSubDocQuery(originalQuery, true);
        DBObject childQuery = underTest.subDoc("studentSectionAssociation").toSubDocQuery(originalQuery, false);
        assertEquals("someValue", parentQuery.get("studentSectionAssociation.someProperty"));
        assertEquals("parent_id", parentQuery.get("_id"));
        assertEquals("parent_idchild", parentQuery.get("studentSectionAssociation._id"));
        assertEquals("someValue", childQuery.get("studentSectionAssociation.someProperty"));
        assertEquals("parent_idchild", childQuery.get("studentSectionAssociation._id"));
        assertNotNull(childQuery.get("_id"));
    }

    @Test
    public void testSearchByParentId() {
        Query originalQuery = new Query(Criteria.where("body.sectionId").in("parentId1", "parentId2"));
        DBObject parentQuery = underTest.subDoc("studentSectionAssociation").toSubDocQuery(originalQuery, true);
        assertEquals(new BasicDBObject("$in", Arrays.asList("parentId1", "parentId2")), parentQuery.get("_id"));
    }

    // test the doUpdate(Query query, Update update) which is used for patch support
    @Test
    public void testdoUpdate() {
        Query originalQuery = new Query(Criteria.where("_id").is("parent_idchild").and("someProperty").is("someValue"));
        Update update = new Update();
        update.set("someProperty", "someNewValue");
        boolean result = underTest.subDoc("studentSectionAssociation").doUpdate(originalQuery, update);
        assertTrue(result);

        originalQuery = new Query(Criteria.where("_id").is("parent_idchild").and("nonExistProperty").is("someValue"));
        update = new Update();
        update.set("nonExistProperty", "someNewValue");
        result = underTest.subDoc("studentSectionAssociation").doUpdate(originalQuery, update);
        assertFalse(result);

    }

    @Test
    public void testFindById() {
        Entity resultEntity = underTest.subDoc("studentSectionAssociation").findById("parent_idchild");
        assertNotNull(resultEntity);
        assertEquals("parent_idchild", resultEntity.getEntityId());
        assertEquals("someValue", resultEntity.getBody().get("someProperty"));
    }

    @Test
    public void testFindAll() {

        Query originalQuery = new Query(Criteria.where("_id").is("parent_idchild").and("someProperty").is("someValue"))
                .skip(0).limit(1);
        List<Entity> entityResults = underTest.subDoc("studentSectionAssociation").findAll(originalQuery);
        assertNotNull(entityResults);
        assertEquals(1, entityResults.size());
        assertEquals("parent_idchild", entityResults.get(0).getEntityId());
        assertEquals("someValue", entityResults.get(0).getBody().get("someProperty"));
    }

    @Test
    public void testDelete() {
        Entity entity = underTest.subDoc("studentSectionAssociation").findById("parent_idchild");
        boolean result = underTest.subDoc("studentSectionAssociation").delete(entity);
        assertTrue(result);

        entity = underTest.subDoc("studentSectionAssociation").findById("nonExistId");
        result = underTest.subDoc("studentSectionAssociation").delete(entity);
        assertFalse(result);
    }

    @Test
    public void testCount() {
        Query originalQuery = new Query(Criteria.where("_id").is("parent_idchild").and("someProperty").is("someValue"));
        long count = underTest.subDoc("studentSectionAssociation").count(originalQuery);
        assertEquals(1L, count);

        originalQuery = new Query(Criteria.where("_id").is("parent_idchild").and("nonExistProperty").is("someValue"));
        count = underTest.subDoc("studentSectionAssociation").count(originalQuery);
        assertEquals(0L, count);
    }

    @Test
    public void testExists() {
        boolean exists = underTest.subDoc("studentSectionAssociation").exists("parent_idchild");
        assertTrue(exists);
        boolean nonExists = underTest.subDoc("studentSectionAssociation").exists("nonExistId");
        assertFalse(nonExists);

    }
}
