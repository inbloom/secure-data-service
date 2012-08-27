package org.slc.sli.api.resources.v1.view;

import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.representation.EntityBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Adds the optional views to a given list of entities
 *
 * @author srupasinghe
 */

@Component
public class OptionalView implements View {

    @Autowired
    private OptionalFieldAppenderFactory factory;

    @Override
    public List<EntityBody> add(List<EntityBody> entities, final String resource, MultivaluedMap<String, String> queryParams) {
        if (factory == null) {
            return entities;
        }

        List<String> optionalFields = queryParams.get(ParameterConstants.OPTIONAL_FIELDS);

        if (optionalFields != null) {
            for (String type : optionalFields) {
                for (String appenderType : type.split(",")) {
                    Map<String, String> values = extractOptionalFieldParams(appenderType);

                    OptionalFieldAppender appender = factory.getOptionalFieldAppender(resource + "_"
                            + values.get(OptionalFieldAppenderFactory.APPENDER_PREFIX));

                    if (appender != null) {
                        entities = appender.applyOptionalField(entities,
                                values.get(OptionalFieldAppenderFactory.PARAM_PREFIX));
                    }

                }
            }
        }

        return entities;
    }

    /**
     * Extract the parameters from the optional field value
     *
     * @param optionalFieldValue
     *            The optional field value
     * @return
     */
    protected Map<String, String> extractOptionalFieldParams(String optionalFieldValue) {
        Map<String, String> values = new HashMap<String, String>();
        String appender = null, params = null;

        if (optionalFieldValue.contains(".")) {
            StringTokenizer st = new StringTokenizer(optionalFieldValue, ".");

            int index = 0;
            String token = null;
            while (st.hasMoreTokens()) {
                token = st.nextToken();
                switch (index) {
                    case 0:
                        appender = token;
                        break;
                    case 1:
                        params = token;
                        break;
                }
                ++index;
            }
        } else {
            appender = optionalFieldValue;
        }

        values.put(OptionalFieldAppenderFactory.APPENDER_PREFIX, appender);
        values.put(OptionalFieldAppenderFactory.PARAM_PREFIX, params);

        return values;
    }
}
