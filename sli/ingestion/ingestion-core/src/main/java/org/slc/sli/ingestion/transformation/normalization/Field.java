package org.slc.sli.ingestion.transformation.normalization;

import java.util.List;

/**
 * Holds definition for a field that is part of reference resolution.
 *
 * @author okrook
 *
 */
public class Field {
    private String path;
    private List<FieldValue> values;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<FieldValue> getValues() {
        return values;
    }

    public void setValues(List<FieldValue> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "Field [path=" + path + ", values=" + values + "]";
    }

}
