package org.slc.sli.api.security.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * Adds context rules to the ContextResolver post construction.
 */
@Component
@Import(AssociativeContextResolver.class)
public class AssociativeContextDefinitions {

    @Autowired
    private ContextResolverStore contextStore;

    @Autowired
    private ContextResolverFactory contextFactory;

    /**
     * init() defines resolvers used to enforce context based permissions.
     * To make a new resolver, specify the source entity type, target entity,
     * and context path from the target to the source.
     * addContext(..) to the ContextResolverStore to wire the context.
     * <p/>
     * For an API request:
     * the source is SLI Principal's entity type,
     * The target is the type of entity being requested.
     * <p/>
     * The association path will be traversed from from source to target to see if the context path exists.
     */
    @PostConstruct
    public void init() {

        List<EntityContextResolver> teacherResolvers = Arrays.<EntityContextResolver>asList(

                contextFactory.makeAssoc().setSource("teacher").setTarget("teacher")
                        .setAssociationPath("teacher-school-associations", "teacher-school-associations").build(),

                contextFactory.makeAssoc().setSource("teacher").setTarget("student")
                        .setAssociationPath("teacher-section-associations", "student-section-associations").build(),

                contextFactory.makeAssoc().setSource("teacher").setTarget("school")
                        .setAssociationPath("teacher-school-associations").build(),

                contextFactory.makeAssoc().setSource("teacher").setTarget("section")
                        .setAssociationPath("teacher-section-associations").build()
        );

        for (EntityContextResolver resolver : teacherResolvers) {
            contextStore.addContext(resolver);
        }
    }

    public AssociativeContextDefinitions() {
    }

    public void setContextStore(ContextResolverStore contextStore) {
        this.contextStore = contextStore;
    }

    public void setContextFactory(ContextResolverFactory contextFactory) {
        this.contextFactory = contextFactory;
    }
}
