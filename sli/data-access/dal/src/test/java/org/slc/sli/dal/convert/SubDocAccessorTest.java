package org.slc.sli.dal.convert;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.CommandResult;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * Test for sub doc accessor
 *
 * @author nbrown
 *
 */
public class SubDocAccessorTest {

    private final MongoTemplate template = mock(MongoTemplate.class);
    private final SubDocAccessor underTest = new SubDocAccessor(template);

    @Test
    public void testCreate() {
        final Map<String, Object> assessmentResult = new HashMap<String, Object>();
        assessmentResult.put("scoreResult", "42");
        assessmentResult.put("studentId", "studentid");
        WriteResult success = mock(WriteResult.class);
        CommandResult successCR = mock(CommandResult.class);
        when(success.getLastError()).thenReturn(successCR);
        when(successCR.ok()).thenReturn(true);
        when(template.updateFirst(argThat(new ArgumentMatcher<Query>() {

            @Override
            public boolean matches(Object argument) {
                Query query = (Query) argument;
                DBObject queryObject = query.getQueryObject();
                return queryObject.get("_id").equals("studentid");
            }
        }), argThat(new ArgumentMatcher<Update>() {

            @Override
            @SuppressWarnings("unchecked")
            public boolean matches(Object argument) {
                Update update = (Update) argument;
                DBObject updateObject = update.getUpdateObject();
                Map<String, Object> set = (Map<String, Object>) updateObject.get("$set");
                List<Map<String, Object>> assessmentResults = new ArrayList<Map<String, Object>>(
                        (Collection<? extends Map<String, Object>>) set.values());
                return assessmentResults.size() == 1 && assessmentResults.get(0).get("scoreResult").equals("42");
            }
        }), eq("student"))).thenReturn(success);
        assertTrue(underTest.subDoc("studentAssessmentAssociation").create(assessmentResult));
    }

}
