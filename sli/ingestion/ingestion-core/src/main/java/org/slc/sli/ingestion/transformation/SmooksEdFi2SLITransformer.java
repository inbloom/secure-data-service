package org.slc.sli.ingestion.transformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.payload.JavaResult;
import org.milyn.payload.StringSource;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.smooks.NonSilentErrorReport;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * EdFi to SLI transformer based on Smooks
 *
 * @author okrook
 *
 */
@Component
public class SmooksEdFi2SLITransformer extends EdFi2SLITransformer implements MessageSourceAware {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static Set<String> filteredAttributes = Collections.emptySet();

    private Map<String, Smooks> smooksConfigs;
    private MessageSource messageSource;

    @Override
    public List<SimpleEntity> transform(NeutralRecord item, ErrorReport errorReport) {

        JavaResult result = new JavaResult();
        Smooks smooks = smooksConfigs.get(item.getRecordType());

        // if no smooks configured for this, then just convert the neutral record to a SimpleEntity
        // directly.
        if (smooks == null) {
            String type = item.getRecordType().replaceAll("_transformed", "");
            Map<String, Object> body = item.getAttributes();
            SimpleEntity entity = new SimpleEntity();
            entity.setType(type);
            entity.setBody(body);

            return Arrays.asList(entity);
        }

        List<SimpleEntity> sliEntities;

        try {
            StringSource source = new StringSource(MAPPER.writeValueAsString(item));

            ExecutionContext ctx = smooks.createExecutionContext();
            ctx.setEventListener(new NonSilentErrorReport(filteredAttributes, messageSource, errorReport, "Transforming phase - " + item.getRecordType()));

            smooks.filterSource(ctx, source, result);

            sliEntities = getEntityListResult(result);
            // sliEntities = result.getBean(List.class);
        } catch (IOException e) {
            sliEntities = Collections.emptyList();
        }

        return sliEntities;
    }

    /**
     * Traverse the results map to get the entity list
     */
    @SuppressWarnings("unchecked")
    private List<SimpleEntity> getEntityListResult(JavaResult result) {
        List<SimpleEntity> entityList = new ArrayList<SimpleEntity>();
        for (Entry<String, Object> resEntry : result.getResultMap().entrySet()) {
            if (resEntry.getValue() instanceof List) {
                List<?> list = (List<?>) resEntry.getValue();
                if (list.size() != 0 && list.get(0) instanceof SimpleEntity) {
                    entityList = (List<SimpleEntity>) list;
                    break;
                }
            }
        }
        return entityList;
    }

    public Map<String, Smooks> getSmooksConfigs() {
        return smooksConfigs;
    }

    public void setSmooksConfigs(Map<String, Smooks> smooksConfigs) {
        this.smooksConfigs = smooksConfigs;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
