package org.slc.sli.modeling.docgen;

public final class Diagram {
    private final String title;
    private final String source;
    private final String prolog;
    private final String epilog;

    public Diagram(final String title, final String source, final String prolog, final String epilog) {
        if (title == null) {
            throw new NullPointerException("title");
        }
        if (source == null) {
            throw new NullPointerException("source");
        }
        if (prolog == null) {
            throw new NullPointerException("prolog");
        }
        if (epilog == null) {
            throw new NullPointerException("epilog");
        }
        this.title = title;
        this.source = source;
        this.prolog = prolog;
        this.epilog = epilog;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("title : \"").append(title).append("\"");
        sb.append(", ");
        sb.append("source : \"").append(source).append("\"");
        sb.append(", ");
        sb.append("prolog : \"").append(prolog).append("\"");
        sb.append(", ");
        sb.append("epilog : \"").append(epilog).append("\"");
        sb.append("}");
        return sb.toString();
    }

    public String getTitle() {
        return title;
    }

    public String getSource() {
        return source;
    }

    public String getProlog() {
        return prolog;
    }

    public String getEpilog() {
        return epilog;
    }
}
