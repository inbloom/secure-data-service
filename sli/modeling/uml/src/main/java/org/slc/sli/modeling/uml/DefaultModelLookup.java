package org.slc.sli.modeling.uml;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A default implementation of {@link LazyLookup} that uses {@link Model}.
 * 
 * Usage: When parsing into a {@link Model} is complete, the model should be set on this class. The
 * object contained in the model will then be available as a graph.
 */
public final class DefaultModelLookup implements LazyLookup {
    
    private Model model;
    
    @Override
    public Type getType(final Reference reference) {
        if (model != null) {
            switch (reference.getKind()) {
                case CLASS_TYPE: {
                    return assertNotNull(model.getClassTypeMap().get(reference.getIdRef()), reference);
                }
                case DATA_TYPE: {
                    return assertNotNull(model.getDataTypeMap().get(reference.getIdRef()), reference);
                }
                case ENUM_TYPE: {
                    return assertNotNull(model.getEnumTypeMap().get(reference.getIdRef()), reference);
                }
                default: {
                    throw new AssertionError(reference.getKind());
                }
            }
        } else {
            throw new IllegalStateException(
                    "Attempting to lookup a type before all forward references have been resolved.");
        }
    }
    
    @Override
    public TagDefinition getTagDefinition(final Reference reference) {
        if (model != null) {
            switch (reference.getKind()) {
                case TAG_DEFINITION: {
                    return assertNotNull(model.getTagDefinitionMap().get(reference.getIdRef()), reference);
                }
                default: {
                    throw new AssertionError(reference.getKind());
                }
            }
        } else {
            throw new IllegalStateException(
                    "Attempting to lookup a tag definition before all forward references have been resolved.");
        }
    }
    
    public void setModel(final Model model) {
        if (model == null) {
            throw new NullPointerException("model");
        }
        this.model = model;
    }
    
    @Override
    public List<Generalization> getGeneralizationBase(final Reference derived) {
        // It might be a good idea to cache this when the model is known.
        final List<Generalization> base = new LinkedList<Generalization>();
        final Map<Identifier, Generalization> generalizationMap = model.getGeneralizationMap();
        for (final Generalization generalization : generalizationMap.values()) {
            final Type child = generalization.getChild();
            final Reference childReference = child.getReference();
            if (childReference.equals(derived)) {
                base.add(generalization);
            }
        }
        return Collections.unmodifiableList(base);
    }
    
    private static final <T> T assertNotNull(final T obj, final Reference memo) {
        if (obj != null) {
            return obj;
        } else {
            throw new RuntimeException(memo.toString());
        }
    }
}
