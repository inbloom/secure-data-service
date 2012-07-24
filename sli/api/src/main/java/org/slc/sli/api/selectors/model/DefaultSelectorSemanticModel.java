package org.slc.sli.api.selectors.model;

import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of semantic model of the selectors
 *
 * @author srupasinghe
 *
 */
@Component
public class DefaultSelectorSemanticModel implements SelectorSemanticModel {

    @Autowired
    private ModelProvider modelProvider;

    @Override
    public Map<Type, Object> parse(Map<String, Object> selectors, Type type) {
        Map<Type, Object> selectorsWithType = new HashMap<Type, Object>();
        parse(selectors, type, selectorsWithType);
        return selectorsWithType;
    }

    private void parse(Map<String, Object> selectors, Type type, Map<Type, Object> selectorsWithType) {
        for (Map.Entry<String, Object> entry : selectors.entrySet()) {
            Object value = entry.getValue();

            if (Map.class.isInstance(value)) {
                Type newType = modelProvider.getType((ClassType)type, entry.getKey());

                if (newType != null && newType.isClassType()) {
                    Map<Type, Object> newMap = new HashMap<Type, Object>();
                    parse((Map<String, Object>) value, (ClassType) newType, newMap);

                    if (selectorsWithType.containsKey(type)) {
                        ((List<Object>) selectorsWithType.get(type)).add(newMap);
                    } else {
                        List<Object> attrs = new ArrayList<Object>();
                        attrs.add(newMap);
                        selectorsWithType.put(type, attrs);
                    }

                } else {
                    throw new RuntimeException("Invalid Selectors " + entry.getKey());
                }
            } else {

                if (modelProvider.isAssociation(type, entry.getKey())) {
                    Type r = modelProvider.getType((ClassType)type, entry.getKey());
                    if (r.isClassType()) type = (ClassType) r;
                }

                //if (isInModel(type, entry.getKey())) {
                //AttributeType attributeType = new AttributeType(entry.getKey(), type);
                if (selectorsWithType.containsKey(type)) {
                    ((List<Object>) selectorsWithType.get(type)).add(entry.getKey());
                } else {
                    List<Object> attrs = new ArrayList<Object>();
                    attrs.add(entry.getKey());
                    selectorsWithType.put(type, attrs);
                }

                //    selectorsWithType.put(type, value);
                //} else {
                //    throw new RuntimeException("Invalid Selectors " + entry.getKey());
                //}

            }
        }
    }

}
