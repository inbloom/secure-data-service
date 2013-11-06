package org.slc.sli.bulk.extract.lea;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StudentSchoolAssociationExtractorTest {

    private StudentSchoolAssociationExtractor ssaExtractor;

    @Mock
    private ExtractFileMap mockMap;
    @Mock
    private EntityExtractor mockExtractor;
    @Mock
    private EntityToEdOrgDateCache mockStudentCache;
    @Mock
    private Repository<Entity> mockRepo;
    @Mock
    private EdOrgExtractHelper mockEdOrgExtractHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(mockMap.getEdOrgs()).thenReturn(new HashSet<String>(Arrays.asList("LEA")));
        ssaExtractor = new StudentSchoolAssociationExtractor(mockExtractor, mockMap, mockRepo, mockEdOrgExtractHelper);
    }

    @Test
    public void testTwoLeasOneStudent() {
        Map<String, DateTime> edOrgDate = new HashMap<String, DateTime>();
        edOrgDate.put("LEA-1", DateTime.now());
        edOrgDate.put("LEA-2", DateTime.now());
        Mockito.when(mockStudentCache.getEntriesById(Mockito.eq("student-1"))).thenReturn(edOrgDate);

        Entity e = Mockito.mock(Entity.class);
        Map entityBody = new HashMap<String, Object>();
        entityBody.put("studentId", "student-1");
        entityBody.put(ParameterConstants.ENTRY_DATE, "2013-01-01");
        Mockito.when(e.getBody()).thenReturn(entityBody);
        Mockito.when(e.getType()).thenReturn(EntityNames.STUDENT_SCHOOL_ASSOCIATION);
        Mockito.when(mockRepo.findEach(Mockito.eq("studentSchoolAssociation"), Mockito.eq(new NeutralQuery()))).thenReturn(Arrays.asList(e).iterator());

        ssaExtractor.extractEntities(mockStudentCache);

        Mockito.verify(mockExtractor, Mockito.times(2)).extractEntity(Mockito.any(Entity.class), Mockito.any(ExtractFile.class),
                Mockito.eq("studentSchoolAssociation"));
    }


    @Test
    public void testOneStudentOneLea() {
        Map<String, DateTime> edOrgDate = new HashMap<String, DateTime>();
        edOrgDate.put("LEA-1", DateTime.now());
        Mockito.when(mockStudentCache.getEntriesById(Mockito.eq("student-1"))).thenReturn(edOrgDate);

        Entity e = Mockito.mock(Entity.class);
        Map entityBody = new HashMap<String, Object>();
        entityBody.put("studentId", "student-1");
        entityBody.put(ParameterConstants.ENTRY_DATE, "2013-01-01");
        Mockito.when(e.getBody()).thenReturn(entityBody);
        Mockito.when(e.getType()).thenReturn(EntityNames.STUDENT_SCHOOL_ASSOCIATION);
        Mockito.when(mockRepo.findEach(Mockito.eq("studentSchoolAssociation"), Mockito.eq(new NeutralQuery()))).thenReturn(Arrays.asList(e).iterator());

        ssaExtractor.extractEntities(mockStudentCache);

        Mockito.verify(mockExtractor, Mockito.times(1)).extractEntity(Mockito.any(Entity.class), Mockito.any(ExtractFile.class),
                Mockito.eq("studentSchoolAssociation"));
    }

    @Test
    public void testTwoStudentsOneLea() {
        Map<String, DateTime> edOrgDate = new HashMap<String, DateTime>();
        edOrgDate.put("LEA-1", DateTime.now());
        Mockito.when(mockStudentCache.getEntriesById(Mockito.eq("student-1"))).thenReturn(edOrgDate);
        Mockito.when(mockStudentCache.getEntriesById(Mockito.eq("student-2"))).thenReturn(edOrgDate);

        Entity e1 = Mockito.mock(Entity.class);
        Map entityBody1 = new HashMap<String, Object>();
        entityBody1.put("studentId", "student-1");
        entityBody1.put(ParameterConstants.ENTRY_DATE, "2013-01-01");
        Mockito.when(e1.getBody()).thenReturn(entityBody1);
        Mockito.when(e1.getType()).thenReturn(EntityNames.STUDENT_SCHOOL_ASSOCIATION);

        Entity e2 = Mockito.mock(Entity.class);
        Map entityBody2 = new HashMap<String, Object>();
        entityBody2.put("studentId", "student-2");
        entityBody2.put(ParameterConstants.ENTRY_DATE, "2013-01-01");
        Mockito.when(e2.getBody()).thenReturn(entityBody2);
        Mockito.when(e2.getType()).thenReturn(EntityNames.STUDENT_SCHOOL_ASSOCIATION);

        Mockito.when(mockRepo.findEach(Mockito.eq("studentSchoolAssociation"), Mockito.eq(new NeutralQuery()))).thenReturn(Arrays.asList(e1, e2).iterator());

        ssaExtractor.extractEntities(mockStudentCache);

        Mockito.verify(mockExtractor, Mockito.times(2)).extractEntity(Mockito.any(Entity.class), Mockito.any(ExtractFile.class),
                Mockito.eq("studentSchoolAssociation"));
    }

    @Test
    public void testNonCurrentDate() {
        Map<String, DateTime> edOrgDate = new HashMap<String, DateTime>();
        edOrgDate.put("LEA-1", DateTime.now());
        Mockito.when(mockStudentCache.getEntriesById(Mockito.eq("student-1"))).thenReturn(edOrgDate);
        Mockito.when(mockStudentCache.getEntriesById(Mockito.eq("student-2"))).thenReturn(edOrgDate);

        Entity e1 = Mockito.mock(Entity.class);
        Map entityBody1 = new HashMap<String, Object>();
        entityBody1.put("studentId", "student-1");
        entityBody1.put(ParameterConstants.ENTRY_DATE, "2013-01-01");
        Mockito.when(e1.getBody()).thenReturn(entityBody1);
        Mockito.when(e1.getType()).thenReturn(EntityNames.STUDENT_SCHOOL_ASSOCIATION);

        Entity e2 = Mockito.mock(Entity.class);
        Map entityBody2 = new HashMap<String, Object>();
        entityBody2.put("studentId", "student-2");
        entityBody2.put(ParameterConstants.ENTRY_DATE, "3000-01-01");
        Mockito.when(e2.getBody()).thenReturn(entityBody2);
        Mockito.when(e2.getType()).thenReturn(EntityNames.STUDENT_SCHOOL_ASSOCIATION);

        Mockito.when(mockRepo.findEach(Mockito.eq("studentSchoolAssociation"), Mockito.eq(new NeutralQuery()))).thenReturn(Arrays.asList(e1, e2).iterator());

        ssaExtractor.extractEntities(mockStudentCache);

        Mockito.verify(mockExtractor, Mockito.times(1)).extractEntity(Mockito.any(Entity.class), Mockito.any(ExtractFile.class),
                Mockito.eq("studentSchoolAssociation"));
    }

}
