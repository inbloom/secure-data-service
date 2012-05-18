package org.slc.sli.modeling.tools.xmicomp.cmdline;

import javax.xml.namespace.QName;

public final class CaseInsensitiveQName implements Comparable<CaseInsensitiveQName> {
    
    private final QName name;
    
    public CaseInsensitiveQName(final CaseInsensitiveString type, final CaseInsensitiveString feature) {
        name = new QName(type.toString(), feature.toString());
    }
    
    @Override
    public int compareTo(final CaseInsensitiveQName other) {
        return QNameComparator.SINGLETON.compare(name, other.name);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof CaseInsensitiveQName) {
            final CaseInsensitiveQName other = (CaseInsensitiveQName) obj;
            return name.equals(other.name);
        }
        return false;
    }
    
    public CaseInsensitiveString getLocalPart() {
        return new CaseInsensitiveString(name.getLocalPart());
    }
    
    public CaseInsensitiveString getNamespaceURI() {
        return new CaseInsensitiveString(name.getNamespaceURI());
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
