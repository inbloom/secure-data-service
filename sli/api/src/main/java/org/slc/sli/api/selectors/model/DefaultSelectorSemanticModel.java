package org.slc.sli.api.selectors.model;

import org.codehaus.plexus.util.StringUtils;
import org.slc.sli.modeling.uml.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
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
    private Map<String, ClassType> types;

    @PostConstruct
    public void init() throws FileNotFoundException {
        types = modelProvider.getClassTypes();
    }

    @Override
    public Map<ClassType, Object> parse(Map<String, Object> selectors, ClassType type) {
        Map<ClassType, Object> selectorsWithType = new HashMap<ClassType, Object>();
        parse(selectors, type, selectorsWithType);
        return selectorsWithType;
    }

    private void parse(Map<String, Object> selectors, ClassType type, Map<ClassType, Object> selectorsWithType) {
        for (Map.Entry<String, Object> entry : selectors.entrySet()) {
            Object value = entry.getValue();

            if (Map.class.isInstance(value)) {
                ClassType newType = getType(type, entry.getKey());

                if (newType != null) {
                    Map<ClassType, Object> newMap = new HashMap<ClassType, Object>();
                    parse((Map<String, Object>) value, newType, newMap);

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

                if (isAssociation(type, entry.getKey())) {
                    type = getType(type, entry.getKey());
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

    private boolean isAttribute(ClassType type, String attribute) {
        return false;
    }

    private boolean isAssociation(ClassType type, String attribute) {
        List<AssociationEnd> associationEnds = modelProvider.getAssociationEnds(type.getId());

        for (AssociationEnd end : associationEnds) {
            if (end.getName().equals(attribute)) {
                return true;
            }
        }

        return false;
    }

    private boolean isInModel(ClassType type, String attr) {
        List<Attribute> attributes = type.getAttributes();

        for (Attribute attribute : attributes) {
            if (attribute.getName().equals(attr)) {
                return true;
            }
        }

        return false;
    }

    private ClassType getType(ClassType type, String attr) {
        if (type.isClassType()) {
            List<AssociationEnd> associationEnds = modelProvider.getAssociationEnds(type.getId());

            for (AssociationEnd end : associationEnds) {
                if (end.getName().equals(attr)) {
                    String name = attr + "<=>" + StringUtils.uncapitalise(type.getName());
                    //String name = type.getName();
                    ClassType newType = types.get(name);
                    return newType;
                }
            }
        } else if (type.isAssociation()) {
            TagDefinition l = modelProvider.getTagDefinition(type.getId());
            Type r = modelProvider.getType(type.getRHS().getId());
            System.out.println(l + " " + r);
        }

        return null;
    }

    protected Map<String, ClassType> getTypes() {
        return types;
    }
}
