package org.slc.sli.ingestion.transformation.normalization.did;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Contains the per-referenceType configuration for deterministic Id resolution.
 *
 * @author jtully
 *
 */
public class DidRefConfig {
    private String entityType;
    private List<KeyFieldDef> keyFields;

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public List<KeyFieldDef> getKeyFields() {
        return keyFields;
    }

    public void setKeyFields(List<KeyFieldDef> keyFields) {
        this.keyFields = keyFields;
    }

    public static DidRefConfig parse(InputStream inputStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(inputStream, DidRefConfig.class);
    }
}
