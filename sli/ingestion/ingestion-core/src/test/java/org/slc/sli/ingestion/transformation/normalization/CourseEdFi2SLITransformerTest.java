package org.slc.sli.ingestion.transformation.normalization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.Repository;
import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.transformation.CourseEdFi2SLITransformer;
import org.slc.sli.ingestion.transformation.SimpleEntity;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * Course transformer implementation unit tests.
 *
 * @author bsuzuki
 *
 */
public class CourseEdFi2SLITransformerTest {

    /* Abstraction of database where data can be resolved */
    private Repository<Entity> repo;

    private ErrorReport errorReport;

    private CourseEdFi2SLITransformer courseTransformer;

    private static final String COURSE_CODE = "courseCode";

    private static final String EXTERNAL_ID = "externalId1234";
    private static final String SCHOOL_ID = "schoolId1234";
    private static final String COURSE_TITLE = "Demon Fighting 101";

    private static final String TEST_KEY = "test";
    private static final String TEST_VALUE = "testValue";

    private static final Object TENANT_ID = "IL_TEST";

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        this.errorReport = new FaultsReport();

        this.repo = Mockito.mock(Repository.class);

        EntityConfigFactory entityConfigurations = new EntityConfigFactory();
        entityConfigurations.setResourceLoader(new DefaultResourceLoader());
        entityConfigurations.setSearchPath("classpath:smooksEdFi2SLI/");

        courseTransformer = new CourseEdFi2SLITransformer();
        courseTransformer.setEntityRepository(repo);
        courseTransformer.setEntityConfigurations(entityConfigurations);
}

    @SuppressWarnings("deprecation")
    @Test
    public void testCourseMatch() {

        /* Entity being ingested which needs to be matched against
         * existing Course documents in the DB and updated */
        SimpleEntity courseEntity = createCourseEntity(EXTERNAL_ID, COURSE_TITLE + "_new", SCHOOL_ID);
        List<Map<String, Object>> newCourseCode = new ArrayList<Map<String, Object>>();
        newCourseCode.add(createCourseCodeEntry("indentificationSystem1", "id1"));
        courseEntity.getBody().put(COURSE_CODE, newCourseCode);

        /* Mocked return matching course entity from the DB */
        Entity existingCourse = createCourseEntity(EXTERNAL_ID, COURSE_TITLE, SCHOOL_ID);
        existingCourse.getMetaData().put(TEST_KEY, TEST_VALUE);

        List<Entity> matches = new ArrayList<Entity>();
        matches.add(existingCourse);

        Mockito.when(repo.findByQuery(Mockito.eq(courseEntity.getType()), Mockito.any(Query.class),
                Mockito.eq(0), Mockito.eq(0))).thenReturn(matches);

        this.courseTransformer.matchEntity(courseEntity, errorReport);
        // All we test is that metadata is updated
        Assert.assertEquals("Course metadata not updated.", courseEntity.getMetaData().get(TEST_KEY), TEST_VALUE);
    }

    SimpleEntity createCourseEntity(String externalId, String courseTitle, String schoolId) {
        SimpleEntity entity = new SimpleEntity();

        entity.setType("course");

        Map<String, Object> metadata = new HashMap<String, Object>();
        metadata.put(EntityMetadataKey.TENANT_ID.getKey(), TENANT_ID);
        metadata.put("externalId", externalId);
        entity.setMetaData(metadata);

        Map<String, Object> field = new HashMap<String, Object>();
        field.put("courseTitle", courseTitle);
        field.put("schoolId", schoolId);
        entity.setBody(field);

        return entity;
    }

    Map<String, Object> createCourseCodeEntry(String indentificationSystem, String id) {

        Map<String, Object> courseCodeEntry = new HashMap<String, Object>();
        courseCodeEntry.put("identificationSystem", indentificationSystem);
        courseCodeEntry.put("ID", id);

        return courseCodeEntry;
    }

}
