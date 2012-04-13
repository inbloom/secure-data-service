package org.slc.sli.modeling.uml;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * A data type is a type that typically has no references to other types.
 */
public final class DataType extends AbstractModelElementWithLookup implements Type {
    private final QName name;
    private final boolean isAbstract;
    
    public DataType(final Identifier id, final QName name, final boolean isAbstract,
            final List<TaggedValue> taggedValues, final LazyLookup lookup) {
        super(id, ReferenceType.DATA_TYPE, taggedValues, lookup);
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
        this.isAbstract = isAbstract;
    }
    
    @Override
    public QName getName() {
        return name;
    }
    
    @Override
    public boolean isAbstract() {
        return isAbstract;
    }
    
    @Override
    public List<Generalization> getGeneralizationBase() {
        return lookup.getGeneralizationBase(this);
    }
    
    @Override
    public List<Generalization> getGeneralizationDerived() {
        return lookup.getGeneralizationDerived(this);
    }
    
    @Override
    public List<AssociationEnd> getAssociationEnds() {
        return lookup.getAssociationEnds(this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        // sb.append("id: " + id).append(", ");
        sb.append("name: \"" + name + "\"").append(", ");
        sb.append("isAbstract: " + isAbstract);
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}