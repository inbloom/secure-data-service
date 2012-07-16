package org.slc.sli.modeling.xmigen;

import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;

/**
 * Converts some UML Class types into UML AssociationClass types if they are Associations.
 */
final class Xsd2UmlTweaker {
    
    public static Model tweak(final Model model, final Xsd2UmlPlugin plugin) {
        
        final ModelIndex indexedModel = new DefaultModelIndex(model);
        
        final Xsd2UmlTweakerVisitor tweaker = new Xsd2UmlTweakerVisitor(indexedModel);
        
        model.accept(tweaker);
        
        return new Model(Identifier.random(), model.getName(), model.getTaggedValues(), tweaker.getOwnedElements());
    }
    
}
