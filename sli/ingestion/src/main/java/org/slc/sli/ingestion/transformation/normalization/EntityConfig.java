package org.slc.sli.ingestion.transformation.normalization;

import java.util.List;

/**
 * Configuration of keys and references for an entity.
 *
 * @author okrook
 *
 */
public class EntityConfig {
    private List<String> keyFields;
    private List<RefDef> references;

    public List<String> getKeyFields() {
        return keyFields;
    }

    public void setKeyFields(List<String> keyFields) {
        this.keyFields = keyFields;
    }

    public List<RefDef> getReferences() {
        return references;
    }

    public void setReferences(List<RefDef> references) {
        this.references = references;
    }
}
