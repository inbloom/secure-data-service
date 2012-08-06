package org.slc.sli.api.selectors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;


import org.apache.commons.io.IOUtils;
import org.slc.sli.api.selectors.model.ModelProvider;
import org.slc.sli.api.selectors.model.SelectorParseException;
import org.slc.sli.api.selectors.model.SelectorSemanticModel;
import org.slc.sli.api.selectors.model.SemanticSelector;
import org.slc.sli.api.service.query.SelectionConverter;
import org.slc.sli.api.service.query.Selector2MapOfMaps;
import org.slc.sli.modeling.uml.ClassType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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
    
    public DefaultSelectorStore() {
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
    public SemanticSelector getSelector(String type) {
        return this.defaultSelectors.get(type);
    }

}
