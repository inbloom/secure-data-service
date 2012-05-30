package org.slc.sli.modeling.xsdgen;

import java.util.Comparator;

import org.slc.sli.modeling.uml.Type;

enum TypeComparator implements Comparator<Type> {
    SINGLETON;
    @Override
    public int compare(final Type element1, final Type element2) {
        return element1.getName().compareTo(element2.getName());
    }
}
