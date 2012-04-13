package org.slc.sli.modeling.psm;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.Reference;
import org.slc.sli.modeling.uml.Type;

public final class PsmConfigExpander {
    private PsmConfigExpander() {
        
    }
    
    public static final PsmConfig<Type> expand(final PsmConfig<Reference> documentation, final Model model) {
        final List<PsmClassType<Type>> domains = expandResourceList(documentation.getClassTypes(), model);
        return new PsmConfig<Type>(domains);
    }
    
    private static final List<PsmClassType<Type>> expandResourceList(final List<PsmClassType<Reference>> domains,
            final Model model) {
        final List<PsmClassType<Type>> result = new LinkedList<PsmClassType<Type>>();
        for (final PsmClassType<Reference> domain : domains) {
            result.add(expandClassType(domain, model));
        }
        return Collections.unmodifiableList(result);
    }
    
    private static final PsmClassType<Type> expandClassType(final PsmClassType<Reference> classType, final Model model) {
        final Type type = model.getClassTypeMap().get(classType.getType().getId());
        return new PsmClassType<Type>(type, classType.getResource(), classType.getCollection());
    }
}
