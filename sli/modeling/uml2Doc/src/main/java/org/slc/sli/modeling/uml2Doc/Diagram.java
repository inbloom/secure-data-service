package org.slc.sli.modeling.uml2Doc;

public final class Diagram {
    private final String title;
    private final String source;
    
    public Diagram(final String title, final String source) {
        if (title == null) {
            throw new NullPointerException("title");
        }
        if (source == null) {
            throw new NullPointerException("source");
        }
        this.title = title;
        this.source = source;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("title : \"").append(title).append("\"");
        sb.append(", ");
        sb.append("source : \"").append(source).append("\"");
        sb.append("}");
        return sb.toString();
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getSource() {
        return source;
    }
}
