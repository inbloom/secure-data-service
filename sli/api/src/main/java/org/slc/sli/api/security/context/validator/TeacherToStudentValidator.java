package org.slc.sli.api.security.context.validator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TeacherToStudentValidator implements IContextValidator {
    
    @Autowired
    private PagingRepositoryDelegate<Entity> repo;
    
    @Value("${sli.security.gracePeriod}")
    private String gracePeriod;

    @Override
    public boolean canValidate(String entityType) {
        return EntityNames.STUDENT.equals(entityType)
                && SecurityUtil.getSLIPrincipal().getEntity().getType().equals(EntityNames.TEACHER);
    }
    
    @Override
    public boolean validate(Set<String> ids) {
        Set<String> teacherSections = new HashSet<String>();
        Set<String> studentSections = new HashSet<String>();
        
        NeutralCriteria endDateCriteria = new NeutralCriteria(ParameterConstants.END_DATE,
                NeutralCriteria.CRITERIA_GTE, getFilterDate(gracePeriod));

        NeutralQuery basicQuery = new NeutralQuery(
                new NeutralCriteria(ParameterConstants.TEACHER_ID, NeutralCriteria.OPERATOR_EQUAL, SecurityUtil
                        .getSLIPrincipal().getEntity().getEntityId()));
        basicQuery.addCriteria(endDateCriteria);
        Iterable<Entity> tsas = repo.findAll(EntityNames.TEACHER_SECTION_ASSOCIATION, basicQuery);
        for (Entity tsa : tsas) {
            teacherSections.add((String) tsa.getBody().get(ParameterConstants.SECTION_ID));
        }
        
        basicQuery = new NeutralQuery(
                new NeutralCriteria(ParameterConstants.STUDENT_ID, NeutralCriteria.CRITERIA_IN, new ArrayList<String>(
                        ids)));
        basicQuery.addCriteria(endDateCriteria);
        Iterable<Entity> ssas = repo.findAll(EntityNames.STUDENT_SECTION_ASSOCIATION, basicQuery);
        for (Entity ssa : ssas) {
            studentSections.add((String) ssa.getBody().get(ParameterConstants.SECTION_ID));
        }
        
        for (String section : studentSections) {
            if (!teacherSections.contains(section)) {
                return false;
            }
        }

        return true;
    }
    
    public String getFilterDate(String gracePeriod) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (gracePeriod != null && !gracePeriod.equals("")) {
            int numDays = Integer.parseInt(gracePeriod) * -1;
            calendar.add(Calendar.DATE, numDays);
        }
        
        return String.format("%1$tY-%1$tm-%1$td", calendar);
    }

    public void setRepo(PagingRepositoryDelegate<Entity> repo) {
        this.repo = repo;
    }
}
