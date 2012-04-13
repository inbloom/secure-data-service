package org.slc.sli.modeling.uml2Doc;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.Reference;
import org.slc.sli.modeling.uml.Type;

public final class DocumentationExpander {
    private DocumentationExpander() {
        
    }
    
    /**
     * Expand the UML references into UML elements.
     * 
     * @param documentation
     *            The documentation with {@link Reference} for types.
     * @param model
     *            The UML model.
     */
    public static final Documentation<Type> expand(final Documentation<Reference> documentation, final Model model) {
        final List<Domain<Type>> domains = dereferenceDomainList(documentation.getDomains(), model);
        return new Documentation<Type>(domains);
    }
    
    private static final List<Domain<Type>> dereferenceDomainList(final List<Domain<Reference>> domains,
            final Model model) {
        final List<Domain<Type>> result = new LinkedList<Domain<Type>>();
        for (final Domain<Reference> domain : domains) {
            result.add(dereferenceDomain(domain, model));
        }
        return Collections.unmodifiableList(result);
    }
    
    private static final Domain<Type> dereferenceDomain(final Domain<Reference> domain, final Model model) {
        final List<Entity<Type>> entities = dereferenceEntityList(domain.getEntities(), model);
        return new Domain<Type>(domain.getTitle(), domain.getDescription(), entities, domain.getDiagrams());
    }
    
    private static final List<Entity<Type>> dereferenceEntityList(final List<Entity<Reference>> domains,
            final Model model) {
        final List<Entity<Type>> result = new LinkedList<Entity<Type>>();
        for (final Entity<Reference> domain : domains) {
            result.add(dereferenceEntity(domain, model));
        }
        return Collections.unmodifiableList(result);
    }
    
    private static final Entity<Type> dereferenceEntity(final Entity<Reference> entity, final Model model) {
        final Type type = model.getClassTypeMap().get(entity.getType().getId());
        return new Entity<Type>(entity.getTitle(), type, entity.getDiagrams());
    }
    
}
