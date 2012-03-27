package org.slc.sli.api.resources.v1.view.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.view.AbstractOptionalFieldStrategy;

/**
 * Provides data about students and assessments to construct the custom
 * views returned by the api
 * @author srupasinghe
 *
 */
@Component
public class StudentAssessmentOptionalFieldStrategy extends AbstractOptionalFieldStrategy {

    public StudentAssessmentOptionalFieldStrategy() {
    }
    
    @Override
    public List<EntityBody> applyOptionalField(List<EntityBody> entities) {
                
        EntityBody b = new EntityBody();
        b.put("assessment", "studentassessment");
        
        entities.add(b);
        
        return entities;
    }
}
