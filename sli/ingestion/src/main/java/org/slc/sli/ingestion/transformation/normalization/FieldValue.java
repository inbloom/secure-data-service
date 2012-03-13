package org.slc.sli.ingestion.transformation.normalization;

/**
 * Holds value for a field that is part of reference resolution.
 *
 * @author okrook
 *
 */
public class FieldValue {
    String sourceValue;
    Ref ref;

    public String getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(String sourceValue) {
        this.sourceValue = sourceValue;
    }

    public Ref getRef() {
        return ref;
    }

    public void setRef(Ref ref) {
        this.ref = ref;
    }
}
