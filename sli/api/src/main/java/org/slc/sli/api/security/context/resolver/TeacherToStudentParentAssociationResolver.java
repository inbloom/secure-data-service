package org.slc.sli.api.security.context.resolver;

import org.slc.sli.api.config.EntityNames;
import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Resolves which StudentParentAssociation a given teacher is allowed to see.
 */
@Component
public class TeacherToStudentParentAssociationResolver implements EntityContextResolver {

    @Autowired
    private AssociativeContextHelper helper;

    @Autowired
    private EntityRepository repository;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.STUDENT_PARENT_ASSOCIATION.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {

        List<String> studentIds = helper.findAccessible(principal, Arrays.asList(
                ResourceNames.TEACHER_SECTION_ASSOCIATIONS, ResourceNames.STUDENT_SECTION_ASSOCIATIONS));

        Iterable<Entity> entities = this.repository.findByQuery(EntityNames.STUDENT_PARENT_ASSOCIATION,
                new Query(Criteria.where("body.studentId").in(studentIds)), 0, 9999);

        List<String> studentParentAssociationIds = new ArrayList<String>();
        for (Entity e : entities) {
            studentParentAssociationIds.add(e.getEntityId());
        }
        return studentParentAssociationIds;
    }

}
