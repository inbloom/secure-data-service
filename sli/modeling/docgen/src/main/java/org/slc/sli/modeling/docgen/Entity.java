package org.slc.sli.modeling.docgen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Entity<TYPE> {
    private final String title;
    private final TYPE type;
    private final List<Diagram> diagrams;
    
    public Entity(final String title, final TYPE type, final List<Diagram> diagrams) {
        if (title == null) {
            throw new NullPointerException("title");
        }
        if (type == null) {
            throw new NullPointerException("type");
        }
        this.title = title;
        this.type = type;
        this.diagrams = Collections.unmodifiableList(new ArrayList<Diagram>(diagrams));
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("title : \"").append(title).append("\"");
        sb.append(", ");
        sb.append("type : \"").append(type).append("\"");
        sb.append(", ");
        sb.append("diagrams : \"").append(diagrams).append("\"");
        sb.append("}");
        return sb.toString();
    }
    
    public String getTitle() {
        return title;
    }
    
    public TYPE getType() {
        return type;
    }
    
    public List<Diagram> getDiagrams() {
        return diagrams;
    }
}
