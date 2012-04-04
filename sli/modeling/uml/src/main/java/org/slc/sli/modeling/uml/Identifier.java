package org.slc.sli.modeling.uml;

import java.util.UUID;

/**
 * Encapsulation of the identifier implementation and for type safety.
 */
public final class Identifier {
    
    public static final Identifier fromString(final String id) {
        return new Identifier(id);
    }
    
    public static final Identifier random() {
        return new Identifier(UUID.randomUUID().toString());
    }
    
    private final String id;
    
    private Identifier(final String id) {
        if (id == null) {
            throw new NullPointerException("id");
        }
        this.id = id;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Identifier) {
            final Identifier other = (Identifier) obj;
            return id.equals(other.id);
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public String toString() {
        return id.toString();
    }
}
