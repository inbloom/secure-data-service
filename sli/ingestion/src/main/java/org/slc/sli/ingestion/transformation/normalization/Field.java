package org.slc.sli.ingestion.transformation.normalization;

public class Field {
    private String path;
    private FieldValue value;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public FieldValue getValue() {
        return value;
    }

    public void setValue(FieldValue value) {
        this.value = value;
    }
}
