package org.slc.sli.modeling.psm;

public final class PsmDocument<TYPE> {
    private final TYPE type;
    private final PsmResource graphResourceName;
    private final PsmCollection singularResourceName;

    public PsmDocument(final TYPE type, final PsmResource graphResourceName, final PsmCollection singularResourceName) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (graphResourceName == null) {
            throw new NullPointerException("graphResourceName");
        }
        if (singularResourceName == null) {
            throw new NullPointerException("collection");
        }
        this.type = type;
        this.graphResourceName = graphResourceName;
        this.singularResourceName = singularResourceName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("type : \"").append(type).append("\"");
        sb.append(", ");
        sb.append("graphResourceName : \"").append(graphResourceName).append("\"");
        sb.append(", ");
        sb.append("singularResourceName : \"").append(singularResourceName).append("\"");
        sb.append("}");
        return sb.toString();
    }

    public TYPE getType() {
        return type;
    }

    /**
     * This is the name that should be used in a graph as an association-end from a reference node
     * to the collection. This is appropriate for the top-level arcs in a REST API but is not
     * appropriate for the name of an element in a schema file.
     */
    public PsmResource getGraphAssociationEndName() {
        return graphResourceName;
    }

    /**
     * This is the name that should be used as the stem in the WXS (W3C XML Schema) file.
     */
    public PsmCollection getSingularResourceName() {
        return singularResourceName;
    }
}
