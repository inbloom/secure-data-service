package org.slc.sli.ingestion.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junitx.util.PrivateAccessor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.Repository;
import org.slc.sli.ingestion.util.InternalIdNormalizer;
import org.slc.sli.ingestion.validation.ErrorReport;
/**
 * Tests for EntityPersistHandler
 *
 * @author ablum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class IdNormalizerTest {

    private MongoEntityRepository mockedEntityRepository;

    //Mock input data
    private Map<String, Object> complexReference;

    //Mock entityRepository data
    private LinkedList<Entity> schoolList;

    private InternalIdNormalizer idNormalizer = new InternalIdNormalizer();

    //private ErrorReport errorReport;

    private static final String REGION_ID = "dc=slidev,dc=net";
    private static final String COLLECTION = "section";

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        //mock complex reference source data
        Map<String, Object> schoolId = new HashMap<String, Object>();
        schoolId.put("School#metaData.externalId", "aSchoolId");

        complexReference = new HashMap<String, Object>();
        complexReference.put("Section#metaData.externalId", "aSectionId");
        complexReference.put("School#body.schoolId" , schoolId);

        //mock school collection in entity repository
        mockedEntityRepository = mock(MongoEntityRepository.class);
        schoolList = new LinkedList<Entity>();
        Entity school = mock(Entity.class);
        when(school.getEntityId()).thenReturn("aSchoolId");
        schoolList.add(school);

        when(mockedEntityRepository.findByPaths(Mockito.eq("school"), Mockito.any(Map.class))).thenReturn(schoolList);

    }

    @Test
    @Ignore // Find by paths is no longer used in the code
    public void testSimpleReference() {

        //mock section collection in entity repository
        List<Entity> sectionList = new LinkedList<Entity>();
        Entity section = mock(Entity.class);
        when(section.getEntityId()).thenReturn("aSectionId");
        sectionList.add(section);

        Map<String, String> simpleSectionFilter = new HashMap<String, String>();
        simpleSectionFilter.put("metaData.externalId", "aSectionId");
        simpleSectionFilter.put("metaData." + EntityMetadataKey.TENANT_ID.getKey() , REGION_ID);
        when(mockedEntityRepository.findByPaths("section", simpleSectionFilter)).thenReturn(sectionList);

        String internalId = idNormalizer.resolveInternalId(mockedEntityRepository, COLLECTION, REGION_ID, "aSectionId", mock(ErrorReport.class));
        Assert.assertEquals("aSectionId", internalId);

    }

    @Test
    @Ignore
    public void testComplexReference() {
        //mock section collection in entity repository
        List<Entity> sectionList = new LinkedList<Entity>();
        Entity section = mock(Entity.class);
        when(section.getEntityId()).thenReturn("aSectionId");
        sectionList.add(section);

        when(mockedEntityRepository.findByQuery(Mockito.eq("section"), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(1))).thenReturn(sectionList);

        String internalId = idNormalizer.resolveInternalId(mockedEntityRepository, COLLECTION, REGION_ID, complexReference, mock(ErrorReport.class));

        Assert.assertEquals("aSectionId", internalId);
    }

    @Test
    @Ignore // code being tested refactored BAR
    public void testQueryGeneration() throws Throwable {
        //mock query in entity repository
        Query expectedQuery = new Query();

        List<Query> queries = new ArrayList<Query>();
        queries.add(new Query(Criteria.where("body.schoolId").is("aSchoolId")));
        expectedQuery.or(queries.toArray(new Query[0]));

        Criteria criteria = Criteria.where("metaData.tenantId").is(REGION_ID);
        expectedQuery.addCriteria(criteria);
        criteria = Criteria.where("metaData.externalId").is("aSectionId");
        expectedQuery.addCriteria(criteria);

        Query actualQuery = new Query();
        Map<String, String> filterFields = new HashMap<String, String>();
        filterFields.put("metaData.tenantId", REGION_ID);

        PrivateAccessor.invoke(InternalIdNormalizer.class, "resolveSearchCriteria",
                new Class[]{Repository.class, String.class, Map.class, Map.class, Query.class, String.class, ErrorReport.class},
                new Object[]{mockedEntityRepository, "section", filterFields , complexReference, actualQuery, REGION_ID, mock(ErrorReport.class)});

        Assert.assertEquals(expectedQuery.getQueryObject().toString(), actualQuery.getQueryObject().toString());
    }


}
