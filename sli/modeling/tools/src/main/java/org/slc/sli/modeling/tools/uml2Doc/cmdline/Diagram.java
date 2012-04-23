package org.slc.sli.modeling.tools.uml2Doc.cmdline;

public final class Diagram {
    private final String title;
    private final String source;
    private final String description;
    
    public Diagram(final String title, final String source, final String description) {
        if (title == null) {
            throw new NullPointerException("title");
        }
        if (source == null) {
            throw new NullPointerException("source");
        }
        if (description == null) {
            throw new NullPointerException("description");
        }
        this.title = title;
        this.source = source;
        this.description = description;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("title : \"").append(title).append("\"");
        sb.append(", ");
        sb.append("source : \"").append(source).append("\"");
        sb.append(", ");
        sb.append("description : \"").append(description).append("\"");
        sb.append("}");
        return sb.toString();
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getSource() {
        return source;
    }
    
    public String getDescription() {
        return description;
    }
}
