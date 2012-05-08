package org.slc.sli.modeling.uml;

/**
 * A polymorphic reference that includes the identifier and the type.
 */
public final class Reference {
    /**
     * The identifier part of the reference.
     */
    private final Identifier id;
    /**
     * The type part of the reference.
     */
    private final ReferenceType type;
    
    public Reference(final Identifier id, final ReferenceType type) {
        if (id == null) {
            throw new NullPointerException("id");
        }
        if (type == null) {
            throw new NullPointerException("type");
        }
        this.id = id;
        this.type = type;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id: " + id).append(", ");
        sb.append("type: " + type);
        sb.append("}");
        return sb.toString();
    }
    
    public Identifier getIdRef() {
        return id;
    }
    
    public ReferenceType getKind() {
        return type;
    }
}
