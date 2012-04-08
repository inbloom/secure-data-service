package org.slc.sli.modeling.uml;

import javax.xml.namespace.QName;

/**
 * Provides the UML qualified name.
 */
public interface HasName {
    /**
     * The qualified name of the model element. Never <code>null</code>.
     */
    QName getName();
}
