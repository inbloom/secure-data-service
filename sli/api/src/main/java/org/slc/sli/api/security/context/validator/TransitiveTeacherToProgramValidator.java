package org.slc.sli.api.security.context.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Validates teacher's direct access to given programs
 * 
 */
@Component
public class TransitiveTeacherToProgramValidator extends AbstractContextValidator {
    
    @Autowired
    TeacherToStudentValidator studentValidator;
    
    @Autowired
    TeacherToProgramValidator programValidator;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;


	@Override
	public boolean canValidate(String entityType, boolean isTransitive) {
		return isTransitive && EntityNames.PROGRAM.equals(entityType) && isTeacher();
	}
	
    @Override
    public boolean validate(String entityType, Set<String> ids) {

        int validCount = 0;

        for (String programId : ids) {
            NeutralQuery nq = new NeutralQuery(0);
            nq.addCriteria(new NeutralCriteria(ParameterConstants.PROGRAM_ID, NeutralCriteria.CRITERIA_IN, ids));
            Iterable<Entity> spas = repo.findAll(EntityNames.STUDENT_PROGRAM_ASSOCIATION, nq);
            boolean foundStudent = false;
            for (Entity ent : spas) {
                String studentId = (String) ent.getBody().get(ParameterConstants.STUDENT_ID);;
                if (!isFieldExpired(ent.getBody(), ParameterConstants.END_DATE, false) && 
                        studentValidator.validate(EntityNames.STUDENT, new HashSet<String>(Arrays.asList(studentId)))) {
                    foundStudent = true;
                    break;
                }
            }
            if (foundStudent) {
                validCount++;
            } else {
                if (programValidator.validate(EntityNames.PROGRAM, new HashSet<String>(Arrays.asList(programId)))) {
                    validCount++;
                    continue;
                } else {
                    return false;
                }
            }
        }

        return validCount == ids.size();
    }

}
