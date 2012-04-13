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
    public Type getType(final HasIdentity reference) {
        if (model != null) {
            if (model.getClassTypeMap().containsKey(reference.getId())) {
                return model.getClassTypeMap().get(reference.getId());
            } else if (model.getDataTypeMap().containsKey(reference.getId())) {
                return model.getDataTypeMap().get(reference.getId());
            } else if (model.getEnumTypeMap().containsKey(reference.getId())) {
                return model.getEnumTypeMap().get(reference.getId());
            } else {
                throw new IllegalArgumentException(reference.getKind().toString());
            }
        } else {
            throw new IllegalStateException(
                    "Attempting to lookup a type before all forward references have been resolved.");
        }
    }
    
    @Override
    public TagDefinition getTagDefinition(final HasIdentity reference) {
        if (model != null) {
            // This is just an assertion of kind.
            switch (reference.getKind()) {
                case TAG_DEFINITION: {
                    return assertNotNull(model.getTagDefinitionMap().get(reference.getId()), reference);
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
    public List<Generalization> getGeneralizationBase(final HasIdentity derived) {
        // It might be a good idea to cache this when the model is known.
        final List<Generalization> base = new LinkedList<Generalization>();
        final Map<Identifier, Generalization> generalizationMap = model.getGeneralizationMap();
        for (final Generalization generalization : generalizationMap.values()) {
            final Type child = generalization.getChild();
            if (child.getId().equals(derived.getId())) {
                base.add(generalization);
            }
        }
        return Collections.unmodifiableList(base);
    }
    
    @Override
    public List<Generalization> getGeneralizationDerived(final HasIdentity base) {
        // It might be a good idea to cache this when the model is known.
        final List<Generalization> derived = new LinkedList<Generalization>();
        final Map<Identifier, Generalization> generalizationMap = model.getGeneralizationMap();
        for (final Generalization generalization : generalizationMap.values()) {
            final Type parent = generalization.getParent();
            if (parent.getId().equals(base.getId())) {
                derived.add(generalization);
            }
        }
        return Collections.unmodifiableList(derived);
    }
    
    @Override
    public List<AssociationEnd> getAssociationEnds(final HasIdentity type) {
        // It might be a good idea to cache this when the model is known.
        final List<AssociationEnd> ends = new LinkedList<AssociationEnd>();
        final Map<Identifier, Association> associationMap = model.getAssociationMap();
        for (final Association candidate : associationMap.values()) {
            {
                final AssociationEnd candidateEnd = candidate.getLHS();
                final Type endType = candidateEnd.getType();
                if (endType.getId().equals(type.getId())) {
                    ends.add(candidate.getRHS());
                }
            }
            {
                final AssociationEnd candidateEnd = candidate.getRHS();
                final Type endType = candidateEnd.getType();
                if (endType.getId().equals(type.getId())) {
                    ends.add(candidate.getLHS());
                }
            }
        }
        return Collections.unmodifiableList(ends);
    }
    
    private static final <T> T assertNotNull(final T obj, final HasIdentity memo) {
        if (obj != null) {
            return obj;
        } else {
            throw new RuntimeException(memo.toString());
        }
    }
    
    @Override
    public boolean isEnabled() {
        return model != null;
    }
}
