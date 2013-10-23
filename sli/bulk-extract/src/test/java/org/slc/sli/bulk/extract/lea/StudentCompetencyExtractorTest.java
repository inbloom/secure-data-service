package org.slc.sli.bulk.extract.lea;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

public class StudentCompetencyExtractorTest {
	@Mock
    private Repository<Entity> mockRepo;
	@Mock
    private EntityExtractor mockExtractor; 
	@Mock
    private ExtractFileMap mockMap;
	
	private EntityExtract entityExtract;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		ExtractorFactory factory = new ExtractorFactory();
		entityExtract = factory.buildStudentCompetencyExtractor(mockExtractor, mockMap, mockRepo);
	}
	
	@Test
	public void testExtractMultipleEntities() {
		Entity studentCompetency1 = Mockito.mock(Entity.class);
		Entity studentCompetency2 = Mockito.mock(Entity.class);
		Entity studentCompetency3 = Mockito.mock(Entity.class);
		Map<String, Object> scBody1 = new HashMap<String, Object>();
		scBody1.put(ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID, "ssa-1");
		Map<String, Object> scBody2 = new HashMap<String, Object>();
		scBody2.put(ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID, "ssa-2");
		Map<String, Object> scBody3 = new HashMap<String, Object>();
		scBody3.put(ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID, "ssa-3");

		Mockito.when(studentCompetency1.getEntityId()).thenReturn("sc-1");
		Mockito.when(studentCompetency1.getBody()).thenReturn(scBody1);
		Mockito.when(studentCompetency2.getEntityId()).thenReturn("sc-2");
		Mockito.when(studentCompetency2.getBody()).thenReturn(scBody2);
		Mockito.when(studentCompetency3.getEntityId()).thenReturn("sc-3");
		Mockito.when(studentCompetency3.getBody()).thenReturn(scBody3);
		Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.STUDENT_COMPETENCY), Mockito.eq(new NeutralQuery()))).
			thenReturn(Arrays.asList(studentCompetency1, studentCompetency2, studentCompetency3).iterator());
		
		ExtractFile mockFile = Mockito.mock(ExtractFile.class);
		Mockito.when(mockMap.getExtractFileForEdOrg(Mockito.eq("lea-1"))).thenReturn(mockFile);
		Mockito.when(mockMap.getExtractFileForEdOrg(Mockito.eq("lea-2"))).thenReturn(mockFile);

		EntityToEdOrgCache cache = new EntityToEdOrgCache();
		cache.addEntry("ssa-1", "lea-1");
		cache.addEntry("ssa-2", "lea-1");
		cache.addEntry("ssa-3", "lea-2");
		
		entityExtract.extractEntities(cache);
        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(studentCompetency1), Mockito.any(ExtractFile.class),
                Mockito.eq(EntityNames.STUDENT_COMPETENCY));
        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(studentCompetency2), Mockito.any(ExtractFile.class),
                Mockito.eq(EntityNames.STUDENT_COMPETENCY));
        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(studentCompetency3), Mockito.any(ExtractFile.class),
                Mockito.eq(EntityNames.STUDENT_COMPETENCY));

	}

}
