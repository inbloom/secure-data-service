package org.slc.sli.api.security.context.validator;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Resolves which courseTranscripts any given staff member can access.
 *
 */
@Component
public class StaffToCourseTranscriptValidator extends AbstractContextValidator {

    @Autowired
    private StaffToSubStudentEntityValidator validator;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return EntityNames.COURSE_TRANSCRIPT.equals(entityType) && isStaff();
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {

        if (!areParametersValid(EntityNames.COURSE_TRANSCRIPT, entityType, ids)) {
            return false;
        }

        info("Validating {}'s access to courseTranscripts: [{}]", SecurityUtil.getSLIPrincipal().getName(), ids);

        Set<String> studentAcademicRecords = new HashSet<String>();
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID,
                NeutralCriteria.CRITERIA_IN, new ArrayList<String>(ids)));
        Iterable<Entity> entities = repo.findAll(EntityNames.COURSE_TRANSCRIPT, query);

        for (Entity entity : entities) {
            Map<String, Object> body = entity.getBody();
            if (body.get(ParameterConstants.STUDENT_ACADEMIC_RECORD_ID) instanceof String) {
                studentAcademicRecords.add((String) body.get(ParameterConstants.STUDENT_ACADEMIC_RECORD_ID));
            } else {
                //studentacademicrecord ID was not a string, this is unexpected
                warn("Possible Corrupt Data detected at "+entityType+"/"+entity.getEntityId());
                return false;
            }
        }

        if (studentAcademicRecords.isEmpty()) {
            return false;
        }

        return validator.validate(EntityNames.STUDENT_ACADEMIC_RECORD, studentAcademicRecords);
    }
}
