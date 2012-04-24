package org.slc.sli.modeling.psm;

public final class PsmDocument<TYPE> {
    private final TYPE type;
    private final PsmResource resource;
    private final PsmCollection collection;

    public PsmDocument(final TYPE type, final PsmResource resource, final PsmCollection collection) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (resource == null) {
            throw new NullPointerException("resource");
        }
        if (collection == null) {
            throw new NullPointerException("collection");
        }
        this.type = type;
        this.resource = resource;
        this.collection = collection;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("type : \"").append(type).append("\"");
        sb.append(", ");
        sb.append("resource : \"").append(resource).append("\"");
        sb.append(", ");
        sb.append("collection : \"").append(collection).append("\"");
        sb.append("}");
        return sb.toString();
    }

    public TYPE getType() {
        return type;
    }

    public PsmResource getResource() {
        return resource;
    }

    public PsmCollection getCollection() {
        return collection;
    }
}
