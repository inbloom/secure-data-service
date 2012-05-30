package org.slc.sli.ingestion.transformation;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.ingestion.transformation.normalization.EntityConfig;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * An implementation of SmooksEdFi2SLITransformer that has special logic to match the Course
 * entities
 *
 * @author bsuzuki
 *
 */
public class CourseEdFi2SLITransformer extends SmooksEdFi2SLITransformer {

    private static final String BODY_BLOCK = "body";
    private static final String COURSECODE = "courseCode";
    private static final String COURSECODE_IDENTIFICATIONSYSTEM = "identificationSystem";
    private static final String COURSECODE_ID = "ID";
    private static final String COURSECODE_ASSIGNINGORGANIZATIONCODE = "assigningOrganizationCode";

    /**
     * Matches a Course entity with a Course in the database
     * We query for courses matching schoolId and courseCode
     *
     * @param entity
     *            The entity that needs to be matched in the database. In this case Course.
     * @param errorReport
     *            The errorReport used to report errors
     */
    @Override
    public void matchEntity(SimpleEntity entity, ErrorReport errorReport) {

        @SuppressWarnings("unchecked")
        List<Map<String, String>> courseCodes = (List<Map<String, String>>) entity.getBody().get("courseCode");

        // query for a match of each courseCode entry return the first match
        for (Map<String, String> courseCode : courseCodes) {

            Query query = createCourseLookupQuery(entity, courseCode, errorReport);

            @SuppressWarnings("deprecation")
            // TODO change this to use a query which is filled by createEntityLookupFilter
            // use limits and handle cursor timeout
            Iterable<Entity> matches = getEntityRepository().findByQuery(entity.getType(), query, 0, 0);

            if (matches != null && matches.iterator().hasNext()) {
                // Entity exists in data store.
                Entity matched = matches.iterator().next();
                entity.setEntityId(matched.getEntityId());
                entity.getMetaData().putAll(matched.getMetaData());

                // Update the externalId
                // TODO this won't be necessary once DE608 has been addressed
                entity.getMetaData().remove(EntityMetadataKey.EXTERNAL_ID.getKey());
                entity.getMetaData().put(EntityMetadataKey.EXTERNAL_ID.getKey(),
                        entity.getBody().get(EntityMetadataKey.EXTERNAL_ID.getKey()));

                // TODO warn if multiple matches are found
                return;
            }
        }
        // TODO error if there are further matches
    }

    private Query createCourseLookupQuery(SimpleEntity entity, Map<String, String> courseCode, ErrorReport errorReport) {

        Criteria courseCodeElemMatch = null;

        EntityConfig entityConfig = getEntityConfigurations().getEntityConfiguration(entity.getType());

        // query for courses matching the schoolId - based on course.json keyFields
        Query query = createEntityLookupQuery(entity, entityConfig, errorReport);

        // add a courseCode match to the match criteria
        String identificationSystem = courseCode.get(COURSECODE_IDENTIFICATIONSYSTEM);
        String id = courseCode.get(COURSECODE_ID);
        String assigningOrgCode = courseCode.get(COURSECODE_ASSIGNINGORGANIZATIONCODE);
        if (assigningOrgCode == null || assigningOrgCode.isEmpty()) {
            courseCodeElemMatch = Criteria.where(BODY_BLOCK + "." + COURSECODE).elemMatch(
                    where(COURSECODE_IDENTIFICATIONSYSTEM).is(identificationSystem).and(COURSECODE_ID).is(id));
        } else {
            courseCodeElemMatch = Criteria.where(BODY_BLOCK + "." + COURSECODE).
                    elemMatch(where(COURSECODE_IDENTIFICATIONSYSTEM).is(identificationSystem).
                            and(COURSECODE_ASSIGNINGORGANIZATIONCODE).is(assigningOrgCode).
                            and(COURSECODE_ID).is(id));
        }
        query.addCriteria(courseCodeElemMatch);

        return query;
    }
}
