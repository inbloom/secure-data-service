package org.slc.sli.bulk.extract.lea;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

public class StudentSchoolAssociationExtractorTest {
	
	private StudentSchoolAssociationExtractor ssaExtractor;
	
	@Mock
    private ExtractFileMap mockMap;
	@Mock
    private EntityExtractor mockExtractor;
	@Mock
    private EntityToEdOrgCache mockCache;
    @Mock
	private Repository<Entity> mockRepo;
    @Mock
    private EdOrgExtractHelper mockEdOrgExtractHelper;

	@Before
	public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(mockMap.getEdOrgs()).thenReturn(new HashSet<String>(Arrays.asList("LEA")));
        ssaExtractor = new StudentSchoolAssociationExtractor(mockExtractor, mockMap, mockRepo, mockCache, mockEdOrgExtractHelper);
	}
	
	@Test
	public void testTwoLeasOneStudent() {
		Mockito.when(mockCache.getEntriesById(Mockito.eq("student-1"))).thenReturn(new HashSet<String>(Arrays.asList("LEA-1", "LEA-2")));
		
        Entity e = Mockito.mock(Entity.class);
        Map entityBody = new HashMap<String, Object>();
        entityBody.put("studentId", "student-1");
        Mockito.when(e.getBody()).thenReturn(entityBody);
        Mockito.when(mockRepo.findEach(Mockito.eq("studentSchoolAssociation"), Mockito.eq(new NeutralQuery()))).thenReturn(Arrays.asList(e).iterator());

		ssaExtractor.extractEntities(null);
        Mockito.verify(mockExtractor, Mockito.times(2)).extractEntity(Mockito.any(Entity.class), Mockito.any(ExtractFile.class),
                Mockito.eq("studentSchoolAssociation"));

	}

	
	@Test
	public void testOneStudentOneLea() {
		Mockito.when(mockCache.getEntriesById(Mockito.eq("student-1"))).thenReturn(new HashSet<String>(Arrays.asList("LEA-1")));
		
        Entity e = Mockito.mock(Entity.class);
        Map entityBody = new HashMap<String, Object>();
        entityBody.put("studentId", "student-1");
        Mockito.when(e.getBody()).thenReturn(entityBody);
        Mockito.when(mockRepo.findEach(Mockito.eq("studentSchoolAssociation"), Mockito.eq(new NeutralQuery()))).thenReturn(Arrays.asList(e).iterator());

		ssaExtractor.extractEntities(null);
        Mockito.verify(mockExtractor, Mockito.times(1)).extractEntity(Mockito.any(Entity.class), Mockito.any(ExtractFile.class),
                Mockito.eq("studentSchoolAssociation"));

	}

	@Test
	public void testTwoStudentsOneLea() {
		Mockito.when(mockCache.getEntriesById(Mockito.eq("student-1"))).thenReturn(new HashSet<String>(Arrays.asList("LEA-1")));
		Mockito.when(mockCache.getEntriesById(Mockito.eq("student-2"))).thenReturn(new HashSet<String>(Arrays.asList("LEA-1")));

        Entity e1 = Mockito.mock(Entity.class);
        Map entityBody1 = new HashMap<String, Object>();
        entityBody1.put("studentId", "student-1");
        Mockito.when(e1.getBody()).thenReturn(entityBody1);
        
        Entity e2 = Mockito.mock(Entity.class);
        Map entityBody2 = new HashMap<String, Object>();
        entityBody2.put("studentId", "student-2");
        Mockito.when(e2.getBody()).thenReturn(entityBody1);

        Mockito.when(mockRepo.findEach(Mockito.eq("studentSchoolAssociation"), Mockito.eq(new NeutralQuery()))).thenReturn(Arrays.asList(e1, e2).iterator());

		ssaExtractor.extractEntities(null);
        Mockito.verify(mockExtractor, Mockito.times(2)).extractEntity(Mockito.any(Entity.class), Mockito.any(ExtractFile.class),
                Mockito.eq("studentSchoolAssociation"));

	}

}
