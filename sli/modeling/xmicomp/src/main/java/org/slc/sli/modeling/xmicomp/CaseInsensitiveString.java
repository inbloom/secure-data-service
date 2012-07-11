package org.slc.sli.modeling.xmicomp;

public final class CaseInsensitiveString implements Comparable<CaseInsensitiveString> {
    
    private final String name;
    
    public CaseInsensitiveString(final String name) {
        this.name = name.toLowerCase();
    }
    
    @Override
    public int compareTo(final CaseInsensitiveString other) {
        return name.compareTo(other.name);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof CaseInsensitiveString) {
            final CaseInsensitiveString other = (CaseInsensitiveString) obj;
            return name.equals(other.name);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public String toString() {
        return name.toString();
    }
}
