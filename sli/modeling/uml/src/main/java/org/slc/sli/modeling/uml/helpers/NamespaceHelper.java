package org.slc.sli.modeling.uml.helpers;

import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.UmlPackage;
import org.slc.sli.modeling.uml.index.ModelIndex;

public final class NamespaceHelper {

    public static final String getNamespace(final Type type, final ModelIndex model) {
        for (final ModelElement whereUsed : model.whereUsed(type.getId())) {
            if (whereUsed instanceof UmlPackage) {
                final UmlPackage pkg = (UmlPackage) whereUsed;
                return pkg.getName();
            }
        }
        return "";
    }
}
