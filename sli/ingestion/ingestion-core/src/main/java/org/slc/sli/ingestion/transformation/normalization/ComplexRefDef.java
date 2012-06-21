package org.slc.sli.ingestion.transformation.normalization;

import java.util.List;

/**
 * Holds definition for complex external reference such as CourseReferenceType that needs to be resolved.
 *
 * @author slee
 *
 */
public class ComplexRefDef {
    private String fieldPath;
    private String collectionName;
    private String path;
    private String valueSource;
    private List<String> complexFieldNames;

    public String getFieldPath() {
        return fieldPath;
    }

    public void setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getValueSource() {
        return valueSource;
    }

    public void setValueSource(String valueSource) {
        this.valueSource = valueSource;
    }

    public List<String> getComplexFieldNames() {
        return complexFieldNames;
    }

    public void setComplexFieldNames(List<String> complexFieldNames) {
        this.complexFieldNames = complexFieldNames;
    }
}

