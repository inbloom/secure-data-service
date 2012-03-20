package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.config.EntityNames;
import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.NeutralCriteria;

/**
 * TeacherAttendanceContextResolver
 * Determine security authorization.
 * Finds the Attendance records a user has context to access.
 */
@Component
public class TeacherAttendanceContextResolver implements EntityContextResolver {

    @Autowired
    private Repository<Entity> repository;
    @Autowired
    private EntityDefinitionStore definitionStore;

    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> ids = new ArrayList<String>(Arrays.asList(principal.getEntityId()));

        AssociationDefinition teacherSectionDef = (AssociationDefinition) definitionStore
                .lookupByResourceName(ResourceNames.TEACHER_SECTION_ASSOCIATIONS);
        AssociationDefinition sectionStudentDef = (AssociationDefinition) definitionStore
                .lookupByResourceName(ResourceNames.STUDENT_SECTION_ASSOCIATIONS);

        ids = findIdsFromAssociation(ids, EntityNames.TEACHER, EntityNames.SECTION, teacherSectionDef);
        ids = findIdsFromAssociation(ids, EntityNames.SECTION, EntityNames.STUDENT, sectionStudentDef);

        List<String> attendanceIds = new ArrayList<String>();
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(9999);
        neutralQuery.addCriteria(new NeutralCriteria("studentId", "in", ids));
        Iterable<Entity> entities = this.repository.findAll(EntityNames.ATTENDANCE, neutralQuery);

        for (Entity e : entities) {
            attendanceIds.add(e.getEntityId());
        }

        return attendanceIds;
    }

    private List<String> findIdsFromAssociation(List<String> ids, String sourceType, String targetType, AssociationDefinition definition) {
        List<String> associationIds = new ArrayList<String>();

        List<String> keys = getAssocKeys(sourceType, definition);
        String sourceKey = keys.get(0);
        String targetKey = keys.get(1);

        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(9999);
        neutralQuery.addCriteria(new NeutralCriteria(sourceKey, "in", ids));
        
        Iterable<Entity> entities = this.repository.findAll(definition.getStoredCollectionName(), neutralQuery);

        for (Entity e : entities) {
            associationIds.add((String) e.getBody().get(targetKey));
        }

        return associationIds;
    }

    private List<String> getAssocKeys(String entityType, AssociationDefinition ad) {

        if (ad.getSourceEntity().getType().equals(entityType)) {
            return Arrays.asList(ad.getSourceKey(), ad.getTargetKey());
        } else if (ad.getTargetEntity().getType().equals(entityType)) {
            return Arrays.asList(ad.getTargetKey(), ad.getSourceKey());
        } else {
            throw new IllegalArgumentException("Entity is not a member of association " + entityType + " " + ad.getType());
        }
    }

    public void setRepository(Repository<Entity> repository) {
        this.repository = repository;
    }

    public void setDefinitionStore(EntityDefinitionStore definitionStore) {
        this.definitionStore = definitionStore;
    }

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.ATTENDANCE.equals(toEntityType);
    }
}
