package org.slc.sli.modeling.psm;

public final class PsmDocument<TYPE> {
    private final TYPE type;
    private final PsmResource pluralResourceName;
    private final PsmCollection singularResourceName;

    public PsmDocument(final TYPE type, final PsmResource pluralResourceName, final PsmCollection singularResourceName) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (pluralResourceName == null) {
            throw new NullPointerException("resource");
        }
        if (singularResourceName == null) {
            throw new NullPointerException("collection");
        }
        this.type = type;
        this.pluralResourceName = pluralResourceName;
        this.singularResourceName = singularResourceName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("type : \"").append(type).append("\"");
        sb.append(", ");
        sb.append("pluralResourceName : \"").append(pluralResourceName).append("\"");
        sb.append(", ");
        sb.append("singularResourceName : \"").append(singularResourceName).append("\"");
        sb.append("}");
        return sb.toString();
    }

    public TYPE getType() {
        return type;
    }

    public PsmResource getPluralResourceName() {
        return pluralResourceName;
    }

    public PsmCollection getSingularResourceName() {
        return singularResourceName;
    }
}
