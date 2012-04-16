package org.slc.sli.modeling.uml2Doc;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.Mapper;

public final class DocumentationExpander {
    private DocumentationExpander() {
        
    }
    
    /**
     * Expand the UML references into UML elements.
     * 
     * @param documentation
     *            The documentation with {@link Identifier} for types.
     * @param model
     *            The UML model.
     */
    public static final Documentation<Type> expand(final Documentation<Identifier> documentation, final Mapper model) {
        final List<Domain<Type>> domains = dereferenceDomainList(documentation.getDomains(), model);
        return new Documentation<Type>(domains);
    }
    
    private static final List<Domain<Type>> dereferenceDomainList(final List<Domain<Identifier>> domains,
            final Mapper model) {
        final List<Domain<Type>> result = new LinkedList<Domain<Type>>();
        for (final Domain<Identifier> domain : domains) {
            result.add(dereferenceDomain(domain, model));
        }
        return Collections.unmodifiableList(result);
    }
    
    private static final Domain<Type> dereferenceDomain(final Domain<Identifier> domain, final Mapper model) {
        final List<Entity<Type>> entities = dereferenceEntityList(domain.getEntities(), model);
        return new Domain<Type>(domain.getTitle(), domain.getDescription(), entities, domain.getDiagrams());
    }
    
    private static final List<Entity<Type>> dereferenceEntityList(final List<Entity<Identifier>> domains,
            final Mapper model) {
        final List<Entity<Type>> result = new LinkedList<Entity<Type>>();
        for (final Entity<Identifier> domain : domains) {
            result.add(dereferenceEntity(domain, model));
        }
        return Collections.unmodifiableList(result);
    }
    
    private static final Entity<Type> dereferenceEntity(final Entity<Identifier> entity, final Mapper model) {
        final Type type = model.getType(entity.getType());
        return new Entity<Type>(entity.getTitle(), type, entity.getDiagrams());
    }
    
}
