package org.slc.sli.api.security.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Adds context rules to the ContextResolver post construction.
 */
@Component
@Import(AssociativeContextResolver.class)
public class AssociativeContextDefinitions {

    @Autowired
    private ContextResolver contextResolver;

    @Autowired
    private ContextResolverFactory contextFactory;

    @PostConstruct
    public void init() {

        contextResolver.addContext(contextFactory.makeAssoc().setSource("teacher").setTarget("teacher")
                .setAssociationPath("teacher-school-associations", "teacher-school-associations").build());

        contextResolver.addContext(contextFactory.makeAssoc().setSource("teacher").setTarget("student")
                .setAssociationPath("teacher-section-associations", "student-section-associations").build());

        contextResolver.addContext(contextFactory.makeAssoc().setSource("teacher").setTarget("school")
                .setAssociationPath("teacher-school-associations").build());

        contextResolver.addContext(contextFactory.makeAssoc().setSource("teacher").setTarget("section")
                .setAssociationPath("teacher-section-associations").build());
    }

    public AssociativeContextDefinitions() {
    }

    public void setContextResolver(ContextResolver contextResolver) {
        this.contextResolver = contextResolver;
    }

    public void setContextFactory(ContextResolverFactory contextFactory) {
        this.contextFactory = contextFactory;
    }
}
