package org.slc.sli.modeling.xmicomp;

public final class CaseInsensitiveName implements Comparable<CaseInsensitiveName> {
    
    private final String name;
    
    public CaseInsensitiveName(final String name) {
        this.name = name.toLowerCase();
    }
    
    @Override
    public int compareTo(final CaseInsensitiveName other) {
        return name.compareTo(other.name);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof CaseInsensitiveName) {
            final CaseInsensitiveName other = (CaseInsensitiveName) obj;
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
