package org.slc.sli.ingestion.transformation.normalization;

/**
 * Holds definition for external reference that needs to be resolved.
 *
 * @author okrook
 *
 */
public class RefDef {
    private String fieldPath;
    private Ref ref;

    public String getFieldPath() {
        return fieldPath;
    }

    public void setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
    }

    public Ref getRef() {
        return ref;
    }

    public void setRef(Ref ref) {
        this.ref = ref;
    }

    @Override
    public String toString() {
        return "RefDef [fieldPath=" + fieldPath + ", ref=" + ref + "]";
    }

}

