package org.slc.sli.api.selectors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;


import org.apache.commons.io.IOUtils;
import org.slc.sli.api.selectors.model.SelectorParseException;
import org.slc.sli.api.service.query.SelectionConverter;
import org.slc.sli.api.service.query.Selector2MapOfMaps;
import org.springframework.stereotype.Component;

@Component
public class DefaultSelectorStore implements DefaultSelectorRepository {
    
    public static final String DEFAULT_SELECTOR_TYPE_KEY = "type";
    public static final String DEFAULT_SELECTOR_VALUE_KEY = "selector";
    public static final String DEFAULT_SELECTOR_RESOURCE_FILENAME = "/config/defaultSelectors.json";
    
    private Map<String, Map<String, Object>> defaultSelectors;
    
    public DefaultSelectorStore() {
        this(DEFAULT_SELECTOR_RESOURCE_FILENAME);
    }
    
    public DefaultSelectorStore(String defaultSelectorResourceLocation) {
        try {
            this.defaultSelectors = readDefaultSelectorsFromFile(defaultSelectorResourceLocation);
            return;
        } catch (IOException ioe) {
            warn("Default selectors failed to parse: " + ioe.getMessage());
        } catch (NullPointerException npe) {
            warn("Default selectors failed to parse: " + npe.getMessage());
        }
        
        this.defaultSelectors = new HashMap<String, Map<String, Object>>();
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
    protected Map<String, Map<String, Object>> readDefaultSelectorsFromFile(String filename) throws IOException, SelectorParseException {
        
        String fileAsString = IOUtils.toString(super.getClass().getResourceAsStream(filename));
        
        SelectionConverter selectionConverter = new Selector2MapOfMaps(false);
        
        Map<String, Map<String, Object>> retVal = new HashMap<String, Map<String, Object>>();
        
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
                    retVal.put(type, selectionConverter.convert(selectorString));
                } catch (NullPointerException npe) {
                    warn("Default selector entry missing 'type' or 'selector' field(s): " + jsonNode.toString());
                } catch (SelectorParseException spe) {
                    warn("Default selector failed to parse: " + spe.getMessage());
                } 
            }
        }
        
        return retVal;
    }
    
    @Override
    public Map<String, Object> getDefaultSelector(String type) {
        return this.defaultSelectors.get(type);
    }

}
