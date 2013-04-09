package org.slc.sli.validation.schema;


public class SchemaReferenceNode {

    String name;
    String references;
    Long minOccurs = null;
    Long maxOccurs = null;

    public SchemaReferenceNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public boolean isArray() {
        return (minOccurs != null && minOccurs > 1) || (maxOccurs !=null && maxOccurs > 1);
    }

    public Long getMinOccurs() {
        return minOccurs;
    }

    public void setMinOccurs(Long minOccurs) {
        this.minOccurs = minOccurs;
    }

    public Long getMaxOccurs() {
        return maxOccurs;
    }

    public void setMaxOccurs(Long maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    @Override
    public String toString() {
        return String.format("%-37s %-4d %-4d", name, minOccurs, maxOccurs);
    }
}
