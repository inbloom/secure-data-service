package org.slc.sli.modeling.tools.xmicomp.cmdline;

import java.util.Comparator;

import javax.xml.namespace.QName;

enum QNameComparator implements Comparator<QName> {
    SINGLETON;
    @Override
    public int compare(final QName arg0, final QName arg1) {
        final int nsComp = arg0.getNamespaceURI().compareTo(arg1.getNamespaceURI());
        if (nsComp == 0) {
            return arg0.getLocalPart().compareTo(arg1.getLocalPart());
        } else {
            return nsComp;
        }
    }
    
}
