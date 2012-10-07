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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.validation.schema.NaturalKeyExtractor;

/**
 * Test for sub doc accessor
 *
 * @author nbrown
 *
 */
public class SubDocAccessorTest {

    private static final String STARTDATE = "3001-09-01";
    private static final String STUDENT1 = "6d386f5af98277e87f5f50daf3b352a4e859d804_id";
    private static final String SECTION1 = "ba78d5d786f263a56c9c861708461b2b852903f0_id";
    private static final String STUDENT2 = "4133baea2e902b28c8aa2edc7ba5c2d07dc2a284_id";
    private static final String SECTION2 = "261472ed910549ecff6bb731f49362ed4d3fef05_id";


    @InjectMocks
    private SubDocAccessor underTest = new SubDocAccessor();

    DeterministicUUIDGeneratorStrategy generator = new DeterministicUUIDGeneratorStrategy();

    @Mock
    NaturalKeyExtractor extractor;

    @Mock
    private MongoTemplate template;

    private final Map<String, Object> studentSectionAssociation = new HashMap<String, Object>();
    private final DBCollection sectionCollection = mock(DBCollection.class);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest.setDidGenerator(generator);

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
        studentSectionAssociations.put(SECTION1 + STUDENT1, studentSectionAssociation);
        studentSectionAssociations.put(SECTION1 + STUDENT2, studentSectionAssociation);
        section.put("studentAssociations", studentSectionAssociations);
        when(template.findOne(matchesParentId(SECTION1), eq(Map.class), eq("section"))).thenReturn(section);

    }

    @Test
    public void testSingleInsert() {
        Map<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("studentId", "value1");
        NaturalKeyDescriptor descriptor = new NaturalKeyDescriptor();
        descriptor.setNaturalKeys(hashMap);
        descriptor.setEntityType("studentSectionAssociation");
        when(extractor.getNaturalKeyDescriptor(any(Entity.class))).thenReturn(descriptor);

        MongoEntity entity = new MongoEntity("studentSectionAssociation", studentSectionAssociation);
        entity.getMetaData().put("tenantId", "TEST");

        assertTrue(underTest.subDoc("studentSectionAssociation").create(entity));
        verify(sectionCollection).update(eq(BasicDBObjectBuilder.start("_id", SECTION1).get()),
                argThat(new ArgumentMatcher<DBObject>() {

                    @Override
                    @SuppressWarnings("unchecked")
                    public boolean matches(Object argument) {
                        DBObject updateObject = (DBObject) argument;
                        Map<String, Object> set = (Map<String, Object>) updateObject.get("$set");
                        List<Map<String, Object>> ssaResults = new ArrayList<Map<String, Object>>(
                                (Collection<? extends Map<String, Object>>) set.values());
                        List<String> ssaIds = new ArrayList<String>(set.keySet());
                        return ssaResults.size() == 1 && ssaResults.get(0).get("startDate").equals(STARTDATE)
                                && ssaIds.get(0).startsWith("studentAssociations." + SECTION1);
                    }
                }), eq(true), eq(false));

    }

    @Test
    public void testMultipleInsert() {
        Map<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("studentId", "value1");
        NaturalKeyDescriptor descriptor = new NaturalKeyDescriptor();
        descriptor.setNaturalKeys(hashMap);
        descriptor.setEntityType("studentSectionAssociation");
        Entity ssa1 = new MongoEntity("studentSectionAssociation", studentSectionAssociation);
        ssa1.getMetaData().put("tenantId", "TEST");
        when(extractor.getNaturalKeyDescriptor(ssa1)).thenReturn(descriptor);

        Map<String, String> hashMap2 = new HashMap<String, String>();
        hashMap2.put("studentId", "value2");
        NaturalKeyDescriptor descriptor2 = new NaturalKeyDescriptor();
        descriptor2.setNaturalKeys(hashMap2);
        descriptor2.setEntityType("studentSectionAssociation");
        Entity ssa2 = new MongoEntity("studentSectionAssociation", new HashMap<String, Object>(
                studentSectionAssociation));
        ssa2.getBody().put("studentId", STUDENT2);
        ssa2.getMetaData().put("tenantId", "TEST");
        when(extractor.getNaturalKeyDescriptor(ssa2)).thenReturn(descriptor2);

        Map<String, String> hashMap3 = new HashMap<String, String>();
        hashMap3.put("studentId", "value3");
        NaturalKeyDescriptor descriptor3 = new NaturalKeyDescriptor();
        descriptor3.setNaturalKeys(hashMap3);
        descriptor3.setEntityType("studentSectionAssociation");
        Entity ssa3 = new MongoEntity("studentSectionAssociation", new HashMap<String, Object>(
                studentSectionAssociation));
        ssa3.getBody().put("sectionId", SECTION2);
        ssa3.getMetaData().put("tenantId", "TEST");
        when(extractor.getNaturalKeyDescriptor(ssa3)).thenReturn(descriptor3);

        assertTrue(underTest.subDoc("studentSectionAssociation").insert(Arrays.asList(ssa1, ssa2, ssa3)));

        // Test that fry gets enrolled in mathematics of wonton burrito meals
        verify(sectionCollection).update(eq(BasicDBObjectBuilder.start("_id", SECTION2).get()),
                argThat(new ArgumentMatcher<DBObject>() {

                    @Override
                    @SuppressWarnings("unchecked")
                    public boolean matches(Object argument) {
                        DBObject updateObject = (DBObject) argument;
                        Map<String, Object> set = (Map<String, Object>) updateObject.get("$set");
                        List<Map<String, Object>> sectionResults = new ArrayList<Map<String, Object>>(
                                (Collection<? extends Map<String, Object>>) set.values());
                        List<String> ssaIds = new ArrayList<String>(set.keySet());
                        return sectionResults.size() == 1 && sectionResults.get(0).get("startDate").equals(STARTDATE)
                                && ssaIds.get(0).startsWith("studentAssociations." + SECTION2);
                    }
                }), eq(true), eq(false));

        // Test that both fry and gunther get enrolled in history of the 20th century
        verify(sectionCollection).update(eq(BasicDBObjectBuilder.start("_id", SECTION1).get()),
                argThat(new ArgumentMatcher<DBObject>() {

                    @Override
                    @SuppressWarnings("unchecked")
                    public boolean matches(Object argument) {
                        DBObject updateObject = (DBObject) argument;
                        Map<String, Object> set = (Map<String, Object>) updateObject.get("$set");
                        List<Map<String, Object>> ssaResults = new ArrayList<Map<String, Object>>(
                                (Collection<? extends Map<String, Object>>) set.values());
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
        String ssaId = SECTION1 + STUDENT1;
        assertEquals(studentSectionAssociation,
                underTest.subDoc("studentSectionAssociation").read(ssaId, null));
    }

}
