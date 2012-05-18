package org.slc.sli.modeling.tools.xmicomp.cmdline;

public final class CaseInsensitiveString {
    
    private final String value;
    
    public CaseInsensitiveString(final String value) {
        this.value = value.toLowerCase();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof CaseInsensitiveString) {
            final CaseInsensitiveString other = (CaseInsensitiveString) obj;
            return value.equals(other.value);
        } else {
            return false;
        }
    }
    
    public boolean endsWith(final CaseInsensitiveString suffix) {
        return value.endsWith(suffix.value);
    }
    
    public int length() {
        return value.length();
    }
    
    public CaseInsensitiveString substring(final int beginIndex, int endIndex) {
        return new CaseInsensitiveString(value.substring(beginIndex, endIndex));
    }
    
    public CaseInsensitiveString concat(final CaseInsensitiveString str) {
        return new CaseInsensitiveString(value.concat(str.value));
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}
