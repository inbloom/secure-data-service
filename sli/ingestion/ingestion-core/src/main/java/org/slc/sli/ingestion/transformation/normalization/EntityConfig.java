package org.slc.sli.ingestion.transformation.normalization;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Configuration of keys and references for an entity.
 *
 * @author okrook
 *
 */
public class EntityConfig {
    private List<String> keyFields;
    private List<RefDef> references;
    private Map<String, Object> piiFields;
    private Map<String, String> subEntities;

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

    public void setPiiFields(Map<String, Object> piiFields) {
        this.piiFields = piiFields;
    }

    public Map<String, Object> getPiiFields() {
        return piiFields;
    }

    public Map<String, String> getSubEntities() {
        return subEntities;
    }

    public void setSubEntities(Map<String, String> subEntities) {
        this.subEntities = subEntities;
    }

    public static EntityConfig parse(InputStream inputStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(inputStream, EntityConfig.class);
    }

}
