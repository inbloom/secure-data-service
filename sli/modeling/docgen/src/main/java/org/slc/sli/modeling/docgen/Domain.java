package org.slc.sli.modeling.docgen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Domain<TYPE> {
    private final String title;
    private final String description;
    private final List<Entity<TYPE>> entities;
    private final List<Diagram> diagrams;
    
    public Domain(final String title, final String description, final List<Entity<TYPE>> entities,
            final List<Diagram> diagrams) {
        if (title == null) {
            throw new NullPointerException("title");
        }
        if (description == null) {
            throw new NullPointerException("description");
        }
        if (entities == null) {
            throw new NullPointerException("entities");
        }
        if (diagrams == null) {
            throw new NullPointerException("diagrams");
        }
        this.title = title;
        this.description = description;
        this.entities = Collections.unmodifiableList(new ArrayList<Entity<TYPE>>(entities));
        this.diagrams = Collections.unmodifiableList(new ArrayList<Diagram>(diagrams));
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("title : \"").append(title).append("\"");
        sb.append(", ");
        sb.append("description : \"").append(description).append("\"");
        sb.append(", ");
        sb.append("entities : \"").append(entities).append("\"");
        sb.append(", ");
        sb.append("diagrams : \"").append(diagrams).append("\"");
        sb.append("}");
        return sb.toString();
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public List<Entity<TYPE>> getEntities() {
        return entities;
    }
    
    public List<Diagram> getDiagrams() {
        return diagrams;
    }
}
