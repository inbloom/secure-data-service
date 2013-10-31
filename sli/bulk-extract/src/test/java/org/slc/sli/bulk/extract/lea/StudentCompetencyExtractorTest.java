package org.slc.sli.bulk.extract.lea;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.datetime.DateHelper;
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

    private StudentCompetencyExtractor entityExtract;

    private EntityToEdOrgDateCache cache;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        cache = new EntityToEdOrgDateCache();
        cache.addEntry("ssa-1", "lea-1", DateTime.parse("2009-05-01", DateHelper.getDateTimeFormat()));
        cache.addEntry("ssa-2", "lea-1", DateTime.parse("2009-05-01", DateHelper.getDateTimeFormat()));
        cache.addEntry("ssa-3", "lea-2", DateTime.parse("2009-05-01", DateHelper.getDateTimeFormat()));
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
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.STUDENT_COMPETENCY), Mockito.eq(new NeutralQuery())))
            .thenReturn(Arrays.asList(studentCompetency1, studentCompetency2, studentCompetency3).iterator());

        ExtractFile mockFile = Mockito.mock(ExtractFile.class);
        Mockito.when(mockMap.getExtractFileForEdOrg(Mockito.eq("lea-1"))).thenReturn(mockFile);
        Mockito.when(mockMap.getExtractFileForEdOrg(Mockito.eq("lea-2"))).thenReturn(mockFile);

        StudentCompetencyExtractor realExtractor = new StudentCompetencyExtractor(mockExtractor, mockMap, mockRepo);
        entityExtract = Mockito.spy(realExtractor);
        Mockito.doReturn(true).when(entityExtract).shouldExtract(Mockito.any(Entity.class), Mockito.any(DateTime.class));

        entityExtract.extractEntities(cache);

        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(studentCompetency1), Mockito.any(ExtractFile.class),
                Mockito.eq(EntityNames.STUDENT_COMPETENCY));
        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(studentCompetency2), Mockito.any(ExtractFile.class),
                Mockito.eq(EntityNames.STUDENT_COMPETENCY));
        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(studentCompetency3), Mockito.any(ExtractFile.class),
                Mockito.eq(EntityNames.STUDENT_COMPETENCY));
    }

    @Test
    public void testExtractNoEntityBecauseShouldNotExtract() {
        Entity mockEntity = Mockito.mock(Entity.class);
        Map<String, Object> mockBody = new HashMap<String, Object>();
        mockBody.put(ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID, "ssa-1");
        Mockito.when(mockEntity.getEntityId()).thenReturn("sc-1");
        Mockito.when(mockEntity.getBody()).thenReturn(mockBody);
        Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.STUDENT_COMPETENCY), Mockito.eq(new NeutralQuery())))
                .thenReturn(Arrays.asList(mockEntity).iterator());

        StudentCompetencyExtractor realExtractor = new StudentCompetencyExtractor(mockExtractor, mockMap, mockRepo);
        entityExtract = Mockito.spy(realExtractor);
        Mockito.doReturn(false).when(entityExtract).shouldExtract(Mockito.any(Entity.class), Mockito.any(DateTime.class));

        entityExtract.extractEntities(cache);

        Mockito.verify(mockExtractor, Mockito.never()).extractEntity(Mockito.eq(mockEntity), Mockito.any(ExtractFile.class),
                Mockito.eq(EntityNames.STUDENT_COMPETENCY));
    }

}
