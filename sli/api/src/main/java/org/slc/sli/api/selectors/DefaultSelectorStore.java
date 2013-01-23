/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.api.selectors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.model.ModelProvider;
import org.slc.sli.api.selectors.model.SelectorParseException;
import org.slc.sli.api.selectors.model.SelectorSemanticModel;
import org.slc.sli.api.selectors.model.SemanticSelector;
import org.slc.sli.api.service.query.SelectionConverter;
import org.slc.sli.api.service.query.Selector2MapOfMaps;
import org.slc.sli.modeling.uml.ClassType;

/**
 * Stores the default selector for each resource API endpoint. Intended for situations where the
 * API user does not specify a selector. If a resource does not have a default configured, this
 * will return the default selector, which is just the base entity as it is normally read.
 *
 *
 * @author kmyers
 *
 */
@Component("defaultSelectorRepository")
public class DefaultSelectorStore implements SelectorRepository {

    public static final String DEFAULT_SELECTOR_TYPE_KEY = "type";
    public static final String DEFAULT_SELECTOR_VALUE_KEY = "selector";
    public static final String DEFAULT_SELECTOR_RESOURCE_FILENAME = "/config/defaultSelectors.json";

    private Map<String, SemanticSelector> defaultSelectors;

    @Autowired
    private SelectorSemanticModel selectorSemanticModel;

    @Autowired
    private ModelProvider modelProvider;

    public static final Map<String, Object> FALLBACK_SELECTOR = new HashMap<String, Object>();
    static {
        FALLBACK_SELECTOR.put("$", true);
    }


    @PostConstruct
    protected void init() throws IOException {
        this.defaultSelectors = readDefaultSelectorsFromFile(DEFAULT_SELECTOR_RESOURCE_FILENAME);
    }

    /**
     * Parses the specified resource file and returns a map of Strings to selectors. Selectors
     * are just maps of Strings to true/false.
     *
     * @param filename resource JSON file to load containing an array of type/selector docs
     * @return a map of the parsed file
     * @throws IOException
     * @throws SelectorParseException
     */
    protected Map<String, SemanticSelector> readDefaultSelectorsFromFile(String filename) throws IOException, SelectorParseException {

        String fileAsString = IOUtils.toString(super.getClass().getResourceAsStream(filename));

        SelectionConverter selectionConverter = new Selector2MapOfMaps(false);

        Map<String, SemanticSelector> retVal = new HashMap<String, SemanticSelector>();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode element = mapper.readValue(fileAsString, JsonNode.class);
        if (element instanceof ArrayNode) {
            ArrayNode arrayNode = (ArrayNode) element;
            Iterator<JsonNode> jsonNodeIterator = arrayNode.getElements();
            while (jsonNodeIterator.hasNext()) {
                JsonNode jsonNode = jsonNodeIterator.next();
                try {
                    String type = jsonNode.get(DEFAULT_SELECTOR_TYPE_KEY).getTextValue();
                    String selectorString = jsonNode.get(DEFAULT_SELECTOR_VALUE_KEY).getTextValue();

                    ClassType classType = modelProvider.getClassType(type);
                    SemanticSelector semanticSelector = selectorSemanticModel.parse(selectionConverter.convert(selectorString), classType);

                    retVal.put(type, semanticSelector);
                } catch (IllegalArgumentException npe) {
                    warn("Default selector entry missing 'type' or 'selector' field(s): " + jsonNode.toString());
                } catch (SelectorParseException spe) {
                    warn("Default selector failed to parse: " + spe.getMessage());
                }
            }
        }

        return retVal;
    }

    protected SemanticSelector getFallBackSelector(String type) {
        ClassType classType = modelProvider.getClassType(type);

        if (classType != null) {
            return selectorSemanticModel.parse(FALLBACK_SELECTOR, classType);
        }

        return null;
    }

    @Override
    public SemanticSelector getSelector(String type) {
        return defaultSelectors.containsKey(type) ? defaultSelectors.get(type) : getFallBackSelector(type);
    }

}
