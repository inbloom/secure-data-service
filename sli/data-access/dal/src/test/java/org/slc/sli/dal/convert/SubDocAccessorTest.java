package org.slc.sli.dal.convert;

import static org.junit.Assert.assertEquals;
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
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
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

    private static final String STARTDATE = "3001-09-01";
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
        studentSectionAssociation.put("sectionId", SECTION1);
        studentSectionAssociation.put("studentId", STUDENT1);
        studentSectionAssociation.put("startDate", STARTDATE);
        WriteResult success = mock(WriteResult.class);
        CommandResult successCR = mock(CommandResult.class);
        when(success.getLastError()).thenReturn(successCR);
        when(successCR.ok()).thenReturn(true);
        when(sectionCollection.update(any(DBObject.class), any(DBObject.class), eq(false), eq(false))).thenReturn(
                success);
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
    }

    @Test
    public void testSingleInsert() {
        MongoEntity entity = new MongoEntity("studentSectionAssociation", studentSectionAssociation);
        entity.getMetaData().put("tenantId", "TEST");
        assertTrue(underTest.subDoc("studentSectionAssociation").create(entity));
        verify(sectionCollection).update(eq(BasicDBObjectBuilder.start("_id", SECTION1).get()),
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
                        Object[] studentSectionsToPush = (Object[]) toPush.iterator().next();
                        List<String> ssaIds = new ArrayList<String>(push.keySet());
                        return ((Map<String, Map<String, Object>>) studentSectionsToPush[0]).get("body")
                                .get("startDate").equals(STARTDATE)
                                && ssaIds.get(0).equals("studentSectionAssociation");
                    }
                }), eq(false), eq(false));

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
        verify(sectionCollection).update(eq(BasicDBObjectBuilder.start("_id", SECTION2).get()),
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
                        Object[] studentSectionsToPush = (Object[]) toPush.iterator().next();
                        List<String> ssaIds = new ArrayList<String>(push.keySet());
                        return ((Map<String, Map<String, Object>>) studentSectionsToPush[0]).get("body")
                                .get("startDate").equals(STARTDATE)
                                && ssaIds.get(0).equals("studentSectionAssociation");
                    }
                }), eq(false), eq(false));
        // Test that both fry and gunther get enrolled in history of the 20th century
        verify(sectionCollection).update(eq(BasicDBObjectBuilder.start("_id", SECTION1).get()),
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
                        Object[] studentSectionsToPush = (Object[]) toPush.iterator().next();
                        return studentSectionsToPush.length == 2;
                    }
                }), eq(false), eq(false));

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
    public void testRead() {
        String ssaId = SECTION1 + STUDENT1;
//        assertTrue(underTest.subDoc("studentSectionAssociation").exists(ssaId));
        assertEquals(studentSectionAssociation, underTest.subDoc("studentSectionAssociation").read(ssaId, null));
        assertEquals(studentSectionAssociation,
                underTest.subDoc("studentSectionAssociation").read(ssaId, null));

        // test the negative case when a student does not exist
        ssaId = SECTION1 + "non-existing-student";
//        assertTrue(!underTest.subDoc("studentSectionAssociation").exists(ssaId));
        assertEquals(null,
                underTest.subDoc("studentSectionAssociation").read(ssaId, null));
    }

    @Test
    public void testMakeSubDocQuery() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Query originalQuery = new Query(Criteria.where("_id").is("parent_idchild").and("someProperty").is("someValue").and("metaData.tenantId").is("myTenant"));
        DBObject subDocQuery = underTest.subDoc("studentSectionAssociation").toSubDocQuery(originalQuery, false);
        assertEquals("someValue", subDocQuery.get("studentSectionAssociation.someProperty"));
        assertEquals("parent_idchild", subDocQuery.get("studentSectionAssociation._id"));
        assertEquals("myTenant", subDocQuery.get("metaData.tenantId"));

    }

}
