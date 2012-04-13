package org.slc.sli.modeling.uml;

/**
 * A polymorphic reference that includes the identifier and the type.
 */
public final class Reference implements HasIdentity {
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
    
    public Identifier getId() {
        return id;
    }
    
    public ReferenceType getKind() {
        return type;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Reference) {
            final Reference that = (Reference) obj;
            return id.equals(that.id) && type.equals(that.type);
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
