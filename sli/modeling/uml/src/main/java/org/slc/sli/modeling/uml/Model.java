package org.slc.sli.modeling.uml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The collection of all entities making up the unified model
 */
public final class Model extends ModelElement implements Visitable {

    private final String name;
    private final List<NamespaceOwnedElement> ownedElements;

    public Model(final Identifier id, final String name, final List<TaggedValue> taggedValues,
            final List<NamespaceOwnedElement> ownedElements) {
        super(id, taggedValues);
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
        this.ownedElements = Collections.unmodifiableList(new ArrayList<NamespaceOwnedElement>(ownedElements));
    }

    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }

    public String getName() {
        return name;
    }

    public List<NamespaceOwnedElement> getOwnedElements() {
        return ownedElements;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id: " + getId()).append(", ");
        sb.append("name: \"" + getName() + "\"");
        sb.append(", ");
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}
