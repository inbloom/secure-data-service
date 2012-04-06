package org.slc.sli.modeling.uml;

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
        this.model = model;
    }
    
    private static final <T> T assertNotNull(final T obj, final Reference memo) {
        if (obj != null) {
            return obj;
        } else {
            throw new RuntimeException(memo.toString());
        }
    }
}
