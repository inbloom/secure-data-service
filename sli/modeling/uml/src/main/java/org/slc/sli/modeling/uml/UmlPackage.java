package org.slc.sli.modeling.uml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A package is a collection of owned elements with a name.
 */
public final class UmlPackage extends NamespaceOwnedElement {
    
    private final List<NamespaceOwnedElement> ownedElements;
    
    public UmlPackage(final String name, final Identifier id, final List<? extends NamespaceOwnedElement> ownedElements) {
        this(name, id, EMPTY_TAGGED_VALUES, ownedElements);
    }
    
    public UmlPackage(final String name, final Identifier id, final List<TaggedValue> taggedValues,
            final List<? extends NamespaceOwnedElement> ownedElements) {
        super(id, name, taggedValues);
        if (ownedElements == null) {
            throw new NullPointerException("ownedElements");
        }
        this.ownedElements = Collections.unmodifiableList(new ArrayList<NamespaceOwnedElement>(ownedElements));
    }
    
    public UmlPackage(final String name, final List<? extends NamespaceOwnedElement> ownedElements) {
        this(name, Identifier.random(), EMPTY_TAGGED_VALUES, ownedElements);
    }
    
    public UmlPackage(final String name, final List<TaggedValue> taggedValues,
            final List<? extends NamespaceOwnedElement> ownedElements) {
        this(name, Identifier.random(), taggedValues, ownedElements);
    }
    
    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
    
    public List<NamespaceOwnedElement> getOwnedElements() {
        return ownedElements;
    }
}
