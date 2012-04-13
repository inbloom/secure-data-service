package org.slc.sli.modeling.uml;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * A package is a collection of owned elements with a name.
 */
public final class Package extends AbstractModelElement implements HasName {
    
    private final QName name;
    private final Model ownedElements;
    
    public Package(final QName name, final Identifier id, final List<TaggedValue> taggedValues,
            final Model ownedElements) {
        super(id, ReferenceType.PACKAGE, taggedValues);
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (ownedElements == null) {
            throw new NullPointerException("ownedElements");
        }
        this.name = name;
        this.ownedElements = ownedElements;
    }
    
    @Override
    public QName getName() {
        return name;
    }
    
    public Model getOwnedElements() {
        return ownedElements;
    }
}
