package org.slc.sli.dal.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.validation.schema.INaturalKeyExtractor;
import org.slc.sli.validation.schema.NaturalKeyExtractor;

/**
 * Test for sub doc accessor
 *
 * @author nbrown
 *
 */
public class SubDocAccessorTest {

    private static final String STARTDATE = "3001-09-01";
    private static final String STUDENT1 = "PhillipFry";
    private static final String SECTION1 = "HistoryOfThe20thCentury";
    private static final String STUDENT2 = "Gunther";
    private static final String SECTION2 = "MathematicsOfWontonBurritoMeals";
    private final MongoTemplate template = mock(MongoTemplate.class);
    private final DeterministicUUIDGeneratorStrategy uuidGenerator = mock(DeterministicUUIDGeneratorStrategy.class);
    INaturalKeyExtractor naturalKeyExtractor = mock(NaturalKeyExtractor.class);
    private final SubDocAccessor underTest = new SubDocAccessor(template, uuidGenerator, naturalKeyExtractor);
    private final Map<String, Object> studentSectionAssociation = new HashMap<String, Object>();
    private final DBCollection sectionCollection = mock(DBCollection.class);

    @Before
    public void setUp() {
        studentSectionAssociation.put("sectionId", SECTION1);
        studentSectionAssociation.put("studentId", STUDENT1);
        studentSectionAssociation.put("startDate", STARTDATE);
        WriteResult success = mock(WriteResult.class);
        CommandResult successCR = mock(CommandResult.class);
        when(success.getLastError()).thenReturn(successCR);
        when(successCR.ok()).thenReturn(true);
        when(sectionCollection.update(any(DBObject.class), any(DBObject.class), eq(true), eq(false))).thenReturn(
                success);
        when(template.getCollection("section")).thenReturn(sectionCollection);
        Map<String, Object> section = new HashMap<String, Object>();
        Map<String, Object> studentSectionAssociations = new HashMap<String, Object>();
        studentSectionAssociations.put(SECTION1 + "×" + STUDENT1, studentSectionAssociation);
        studentSectionAssociations.put(SECTION1 + "×" + STUDENT2, studentSectionAssociation);
        section.put("studentSectionAssociation", studentSectionAssociations);
        when(template.findOne(matchesParentId(SECTION1), eq(Map.class), eq("section"))).thenReturn(section);
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
                        Map<String, Object> set = (Map<String, Object>) updateObject.get("$pushAll");
                        List<Map<String, Object>> ssaResults = new ArrayList<Map<String, Object>>(
                                (Collection<? extends Map<String, Object>>) set.values());
                        List<String> ssaIds = new ArrayList<String>(set.keySet());
                        return ssaResults.size() == 1 && ssaResults.get(0).get("startDate").equals(STARTDATE)
                                && ssaIds.get(0).equals("studentSectionAssociation");
                    }
                }), eq(true), eq(false));

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
                        List<Map<String, Object>> studentSections = new ArrayList<Map<String, Object>>(
                                (Collection<? extends Map<String, Object>>) push.values());
                        List<String> ssaIds = new ArrayList<String>(push.keySet());
                        return studentSections.size() == 1 && studentSections.get(0).get("startDate").equals(STARTDATE)
                                && ssaIds.get(0).equals("studentSectionAssociation");
                    }
                }), eq(true), eq(false));
        // Test that both fry and gunther get enrolled in history of the 20th century
        verify(sectionCollection).update(eq(BasicDBObjectBuilder.start("_id", SECTION1).get()),
                argThat(new ArgumentMatcher<DBObject>() {

                    @Override
                    @SuppressWarnings("unchecked")
                    public boolean matches(Object argument) {
                        DBObject updateObject = (DBObject) argument;
                        Map<String, Object> push = (Map<String, Object>) updateObject.get("$pushAll");
                        List<Map<String, Object>> ssaResults = new ArrayList<Map<String, Object>>(
                                (Collection<? extends Map<String, Object>>) push.values());
                        return ssaResults.size() == 2;
                    }
                }), eq(true), eq(false));

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
        String ssaId = SECTION1 + "×" + STUDENT1;
        assertEquals(studentSectionAssociation, underTest.subDoc("studentSectionAssociation").read(ssaId, null));
    }

}
