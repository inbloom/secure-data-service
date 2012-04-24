package org.slc.sli.modeling.tools.xmi2Psm.cmdline;

import java.util.Comparator;

import org.slc.sli.modeling.psm.PsmDocument;
import org.slc.sli.modeling.uml.Type;

enum PsmDocumentComparator implements Comparator<PsmDocument<Type>> {

    SINGLETON;

    @Override
    public int compare(final PsmDocument<Type> arg0, final PsmDocument<Type> arg1) {
        return arg0.getType().getName().compareTo(arg1.getType().getName());
    }
}
