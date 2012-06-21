package org.slc.sli.ingestion.transformation.normalization;

/**
 * Holds value for a field that is part of reference resolution.
 *
 * @author okrook
 *
 */
public class FieldValue {
    private String valueSource;
    private Ref ref;

    public String getValueSource() {
        return valueSource;
    }

    public void setValueSource(String valueSource) {
        this.valueSource = valueSource;
    }

    public Ref getRef() {
        return ref;
    }

    public void setRef(Ref ref) {
        this.ref = ref;
    }

    @Override
    public String toString() {
        return "FieldValue [valueSource=" + valueSource + ", ref=" + ref + "]";
    }

}
