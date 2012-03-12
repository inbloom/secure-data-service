package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.config.EntityNames;
import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.SmartQuery;
import org.slc.sli.domain.Repository;

/**
 * Resolves which teachers a given teacher is allowed to see
 *
 * @author dkornishev
 *
 */
@Component
public class TeacherSessionResolver implements EntityContextResolver {

    @Autowired
    private AssociativeContextHelper helper;

    @Autowired
    private Repository<Entity> repository;
    @Autowired
    private EntityDefinitionStore definitionStore;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.SESSION.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> ids = new ArrayList<String>(Arrays.asList(principal.getEntityId()));

        AssociationDefinition teacherSectionDef = (AssociationDefinition) definitionStore
                .lookupByResourceName(ResourceNames.TEACHER_SECTION_ASSOCIATIONS);
        AssociationDefinition sectionStudentDef = (AssociationDefinition) definitionStore
                .lookupByResourceName(ResourceNames.STUDENT_SECTION_ASSOCIATIONS);

        ids = findIdsFromAssociation(ids, EntityNames.TEACHER, EntityNames.SECTION, teacherSectionDef);
        ids = findIdsFromAssociation(ids, EntityNames.SECTION, EntityNames.STUDENT, sectionStudentDef);
        ids = findIdsFromAssociation(ids, EntityNames.STUDENT, EntityNames.SECTION, sectionStudentDef);

        StringBuilder query = new StringBuilder();
        String separator = "";

        for (String s : ids) {
            query.append(separator);
            separator = ",";
            query.append(s);
        }

        SmartQuery.SmartQueryBuilder queryBuilder = new SmartQuery.SmartQueryBuilder();
        queryBuilder.addField("_id", query.toString());

        Iterable<Entity> entities = repository.findAll(EntityNames.SECTION, queryBuilder.build());

        List<String> sessionIds = new ArrayList<String>();

        for (Entity e : entities) {
            sessionIds.add((String) e.getBody().get("sessionId"));
        }

        return sessionIds;
    }

    private List<String> findIdsFromAssociation(List<String> ids, String sourceType, String targetType, AssociationDefinition definition) {
        List<String> associationIds = new ArrayList<String>();

        List<String> keys = getAssocKeys(sourceType, definition);
        String sourceKey = keys.get(0);
        String targetKey = keys.get(1);

        Iterable<Entity> entities = this.repository.findByQuery(definition.getStoredCollectionName(), new Query(Criteria.where("body." + sourceKey).in(ids)), 0, 9999);

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
}
