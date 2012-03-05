package org.slc.sli.ingestion.transformation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.milyn.Smooks;
import org.milyn.payload.JavaResult;
import org.milyn.payload.StringSource;

import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * EdFi to SLI transformer based on Smooks
 *
 * @author okrook
 *
 */
public class SmooksEdFi2SLITransformer extends EdFi2SLITransformer {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Map<String, Smooks> smooksConfigs;

    @SuppressWarnings("unchecked")
    @Override
    public List<? extends Entity> handle(NeutralRecord item, ErrorReport errorReport) {

        JavaResult result = new JavaResult();
        Smooks smooks = smooksConfigs.get(item.getRecordType());

        try {
            StringSource source = new StringSource(MAPPER.writeValueAsString(item));

            smooks.filterSource(source, result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.getBean(List.class);
    }

    public Map<String, Smooks> getSmooksConfigs() {
        return smooksConfigs;
    }

    public void setSmooksConfigs(Map<String, Smooks> smooksConfigs) {
        this.smooksConfigs = smooksConfigs;
    }

}
