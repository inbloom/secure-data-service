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

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;

/**
 * Test for sub doc accessor
 *
 * @author nbrown
 *
 */
public class SubDocAccessorTest {

    private final MongoTemplate template = mock(MongoTemplate.class);
    private final SubDocAccessor underTest = new SubDocAccessor(template);
    private final Map<String, Object> assessmentResultBody = new HashMap<String, Object>();
    private final Map<String, Object> assessmentResultMetadata = new HashMap<String, Object>();
    private Entity assessmentResult;

    @Before
    public void setUp() {
        assessmentResultBody.put("scoreResult", "42");
        assessmentResultBody.put("studentId", "studentid");
        assessmentResultMetadata.put("tenantId", "TEST");
        assessmentResult = new MongoEntity("studentAssessmentAssociation", null, assessmentResultBody, assessmentResultMetadata);
    }

    @Test
    public void testCreate() {
        WriteResult success = mock(WriteResult.class);
        CommandResult successCR = mock(CommandResult.class);
        when(success.getLastError()).thenReturn(successCR);
        when(successCR.ok()).thenReturn(true);
        DBCollection studentCollection = mock(DBCollection.class);
        when(template.getCollection("student")).thenReturn(studentCollection);
        when(studentCollection.update(any(DBObject.class), any(DBObject.class), eq(true), eq(false))).thenReturn(
                success);
        assertTrue(underTest.subDoc("studentAssessmentAssociation").create(assessmentResult));
        verify(studentCollection).update(eq(BasicDBObjectBuilder.start("_id", "studentid").get()),
                argThat(new ArgumentMatcher<DBObject>() {

                    @Override
                    @SuppressWarnings("unchecked")
                    public boolean matches(Object argument) {
                        DBObject updateObject = (DBObject) argument;
                        Map<String, Object> set = (Map<String, Object>) updateObject.get("$set");
                        List<Map<String, Object>> assessmentResults = new ArrayList<Map<String, Object>>(
                                (Collection<? extends Map<String, Object>>) set.values());
                        List<String> assessmentIds = new ArrayList<String>(set.keySet());
                        return assessmentResults.size() == 1
                                && assessmentResults.get(0).get("scoreResult").equals("42")
                                && assessmentIds.get(0).startsWith("assessments.studentid×");
                    }
                }), eq(true), eq(false));

    }

    @Test
    public void testInsert() {
        WriteResult success = mock(WriteResult.class);
        CommandResult successCR = mock(CommandResult.class);
        when(success.getLastError()).thenReturn(successCR);
        when(successCR.ok()).thenReturn(true);
        DBCollection enrollmentCollection = mock(DBCollection.class);
        when(template.getCollection("student")).thenReturn(enrollmentCollection);
        when(enrollmentCollection.update(any(DBObject.class), any(DBObject.class), eq(true), eq(false))).thenReturn(
                success);

        Entity assessment1 = new MongoEntity("studentAssessmentAssociation", assessmentResultBody);
        Entity assessment2 = new MongoEntity("studentAssessmentAssociation", new HashMap<String, Object>(
                assessmentResult.getBody()));
        assessment2.getBody().put("scoreResult", "24");
        Entity assessment3 = new MongoEntity("studentAssessmentAssociation", new HashMap<String, Object>(
                assessmentResult.getBody()));
        assessment3.getBody().put("studentId", "studentid2");
        assertTrue(underTest.subDoc("studentAssessmentAssociation").insert(
                Arrays.asList(assessment1, assessment2, assessment3)));
        verify(enrollmentCollection).update(eq(BasicDBObjectBuilder.start("_id", "studentid2").get()),
                argThat(new ArgumentMatcher<DBObject>() {

                    @Override
                    @SuppressWarnings("unchecked")
                    public boolean matches(Object argument) {
                        DBObject updateObject = (DBObject) argument;
                        Map<String, Object> set = (Map<String, Object>) updateObject.get("$set");
                        List<Map<String, Object>> assessmentResults = new ArrayList<Map<String, Object>>(
                                (Collection<? extends Map<String, Object>>) set.values());
                        List<String> assessmentIds = new ArrayList<String>(set.keySet());
                        return assessmentResults.size() == 1
                                && assessmentResults.get(0).get("scoreResult").equals("42")
                                && assessmentIds.get(0).startsWith("assessments.studentid2×");
                    }
                }), eq(true), eq(false));
        verify(enrollmentCollection).update(eq(BasicDBObjectBuilder.start("_id", "studentid").get()),
                argThat(new ArgumentMatcher<DBObject>() {

                    @Override
                    @SuppressWarnings("unchecked")
                    public boolean matches(Object argument) {
                        DBObject updateObject = (DBObject) argument;
                        Map<String, Object> set = (Map<String, Object>) updateObject.get("$set");
                        List<Map<String, Object>> assessmentResults = new ArrayList<Map<String, Object>>(
                                (Collection<? extends Map<String, Object>>) set.values());
                        return assessmentResults.size() == 2;
                    }
                }), eq(true), eq(false));

    }

    private Query matchesParentId() {
        Query matchesId = argThat(new ArgumentMatcher<Query>() {

            @Override
            public boolean matches(Object argument) {
                Query query = (Query) argument;
                DBObject queryObject = query.getQueryObject();
                return queryObject.get("_id").equals("studentid");
            }
        });
        return matchesId;
    }

    @Test
    public void testRead() {
        Map<String, Object> student = new HashMap<String, Object>();
        Map<String, Object> studentAssessments = new HashMap<String, Object>();
        studentAssessments.put("studentid×1234", assessmentResult.getBody());
        student.put("assessments", studentAssessments);
        when(template.findOne(matchesParentId(), eq(Map.class), eq("student"))).thenReturn(student);
        assertEquals(assessmentResult.getBody(), underTest.subDoc("studentAssessmentAssociation").read("studentid×1234", null));
    }

}
