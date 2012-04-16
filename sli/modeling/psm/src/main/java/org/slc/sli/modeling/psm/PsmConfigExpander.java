package org.slc.sli.modeling.psm;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.Mapper;

public final class PsmConfigExpander {
    private PsmConfigExpander() {
        
    }
    
    public static final PsmConfig<Type> expand(final PsmConfig<Identifier> documentation, final Mapper model) {
        final List<PsmClassType<Type>> domains = expandResourceList(documentation.getClassTypes(), model);
        return new PsmConfig<Type>(domains);
    }
    
    private static final List<PsmClassType<Type>> expandResourceList(final List<PsmClassType<Identifier>> domains,
            final Mapper model) {
        final List<PsmClassType<Type>> result = new LinkedList<PsmClassType<Type>>();
        for (final PsmClassType<Identifier> domain : domains) {
            result.add(expandClassType(domain, model));
        }
        return Collections.unmodifiableList(result);
    }
    
    private static final PsmClassType<Type> expandClassType(final PsmClassType<Identifier> classType, final Mapper model) {
        final Type type = model.getType(classType.getType());
        return new PsmClassType<Type>(type, classType.getResource(), classType.getCollection());
    }
}
