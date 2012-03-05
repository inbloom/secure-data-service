package org.slc.sli.api.security.context;

import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.config.EntityNames;
import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityExistsException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores context based permission resolvers.
 * Can determine if a principal entity has permission to access a request entity.
 */
@Component
public class ContextResolverStore {

    private Map<String, EntityContextResolver> contexts = new HashMap<String, EntityContextResolver>();

    @Autowired
    private DefaultEntityContextResolver defaultEntityContextResolver;

    @Autowired
    private EntityDefinitionStore definitionStore;

    @Autowired
    private EntityRepository repository;

    /* Educator context */
    private List<EntityContextResolver> buildTeacherResolvers() {

        List<EntityContextResolver> teacherResolvers = Arrays.<EntityContextResolver>asList(

                makeAssoc().setSource(EntityNames.TEACHER).setTarget(EntityNames.TEACHER)
                        .setAssociationPath(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS, ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS).build(),
                makeAssoc().setSource(EntityNames.TEACHER).setTarget(EntityNames.STUDENT)
                        .setAssociationPath(ResourceNames.TEACHER_SECTION_ASSOCIATIONS, ResourceNames.STUDENT_SECTION_ASSOCIATIONS).build(),
                makeAssoc().setSource(EntityNames.TEACHER).setTarget(EntityNames.SCHOOL)
                        .setAssociationPath(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS).build(),
                makeAssoc().setSource(EntityNames.TEACHER).setTarget(EntityNames.SECTION)
                        .setAssociationPath(ResourceNames.TEACHER_SECTION_ASSOCIATIONS).build(),
                makeAssoc().setSource(EntityNames.TEACHER).setTarget(EntityNames.ASSESSMENT)
                        .setAssociationPath(ResourceNames.TEACHER_SECTION_ASSOCIATIONS, ResourceNames.SECTION_ASSESSMENT_ASSOCIATIONS).build(),
                makeAssoc().setSource(EntityNames.TEACHER).setTarget(EntityNames.SESSION)
                        .setAssociationPath(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS, ResourceNames.SCHOOL_SESSION_ASSOCIATIONS).build()

        );

        return teacherResolvers;
    }


    /* Leader and IT Admin context */
    private List<EntityContextResolver> buildStaffResolvers() {

        final List<EntityContextResolver> staffResolvers = Arrays.<EntityContextResolver>asList(


        );
        return staffResolvers;
    }

    /* aggregate context */
    private List<EntityContextResolver> buildAggregateResolvers() {

        final List<EntityContextResolver> aggregateResolvers = Arrays.<EntityContextResolver>asList(

        );
        return aggregateResolvers;
    }

    /* SLI Admin context */
    private List<EntityContextResolver> buildSliAdminResolvers() {

        final List<EntityContextResolver> sliAdminResolvers = Arrays.<EntityContextResolver>asList(

        );
        return sliAdminResolvers;
    }


    /**
     * init() defines resolvers used to enforce context based permissions.
     * To make a new resolver, specify the source entity type, target entity,
     * and context path from the target to the source.
     * addContext(..) to the ContextResolverStore to wire the context.
     * <p/>
     * For an API request: the source is SLI Principal's entity type, The target is the type of entity being requested.
     * <p/>
     * The association path will be traversed from from source to target to see if the context path exists.
     */
    public void init() {

        loadResolvers(buildTeacherResolvers());
        loadResolvers(buildStaffResolvers());
        loadResolvers(buildAggregateResolvers());
        loadResolvers(buildSliAdminResolvers());

    }

    private synchronized void loadResolvers(List<EntityContextResolver> resolvers) {
        for (EntityContextResolver resolver : resolvers) {
            EntityContextResolver putResult = contexts.put(this.getContextKey(resolver), resolver);
            if (putResult != null) {
                throw new EntityExistsException();
            }
        }
    }

    public synchronized EntityContextResolver getContextResolver(String sourceType, String targetType) {

        if (contexts.isEmpty()) {
            init();
        }

        EntityContextResolver resolver = contexts.get(getContextKey(sourceType, targetType));
        return resolver == null ? defaultEntityContextResolver : resolver;
    }

    public EntityContextResolver getContextResolver(Entity principalEntity, Entity requestEntity) {
        return getContextResolver(principalEntity.getType(), requestEntity.getType());
    }

    public void clearContexts() {
        contexts.clear();
    }

    private String getContextKey(EntityContextResolver resolver) {
        return getContextKey(resolver.getSourceType(), resolver.getTargetType());
    }

    private String getContextKey(String sourceType, String targetType) {
        return sourceType + targetType;
    }

    public AssociativeContextBuilder makeAssoc() {
        return new AssociativeContextBuilder();
    }

    public Map<String, EntityContextResolver> getContexts() {
        return contexts;
    }

    /**
     * Builder pattern for Associative Context
     */
    public class AssociativeContextBuilder {
        private String source;
        private String target;
        private List<AssociationDefinition> associationPath = new ArrayList<AssociationDefinition>();
        private EntityDefinitionStore entityDefs;
        private EntityRepository repo;

        public AssociativeContextBuilder() {
            entityDefs = definitionStore;
            repo = repository;
        }

        public AssociativeContextBuilder setSource(String source) {
            this.source = source;
            return this;
        }

        public AssociativeContextBuilder setTarget(String target) {
            this.target = target;
            return this;
        }

        public AssociativeContextBuilder setAssociationPath(String... associationNames) {
            for (String assocName : associationNames) {
                EntityDefinition entityDefinition = entityDefs.lookupByResourceName(assocName);
                if (entityDefinition != null && entityDefinition instanceof AssociationDefinition) {
                    associationPath.add((AssociationDefinition) entityDefinition);
                } else {
                    throw new InvalidParameterException("cannot find associationType in definition store" + assocName);
                }
            }
            return this;
        }

        public AssociativeContextResolver build() {
            AssociativeContextResolver assocContext = new AssociativeContextResolver();
            assocContext.setSourceType(source);
            assocContext.setTargetType(target);
            assocContext.setAssociativeContextPath(associationPath);
            assocContext.setRepository(repo);
            return assocContext;
        }
    }
}
