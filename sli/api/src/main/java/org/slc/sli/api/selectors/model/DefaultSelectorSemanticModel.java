package org.slc.sli.api.selectors.model;

import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.ModelElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public SemanticSelector parse(final Map<String, Object> selectors, final ClassType type) throws SelectorParseException {
        if (type == null) throw new NullPointerException("type");
        if (selectors == null) throw new NullPointerException("selectors");

        final SemanticSelector selector = new SemanticSelector();
        for (final Map.Entry<String, Object> entry : selectors.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            addEntryToSelector(type, selector, key, value);
        }
        return selector;
    }

    private void addEntryToSelector(final ClassType type, final SemanticSelector selector, final String key, final Object value)
            throws SelectorParseException {
        final SelectorElement elem;
        final ModelElement element = modelProvider.getModelElement(type, key);
        final ClassType keyType = modelProvider.getClassType(type, key);

        if (key.equals(SelectorElement.INCLUDE_ALL)) {
            elem = new IncludeAllSelectorElement(type);
        } else if (modelProvider.isAssociation(type, key) || modelProvider.isAttribute(type, key)) {
            elem = parseEntry(value, element, keyType);
        } else {
            throw new SelectorParseException("Invalid Selectors " + key);
        }
        selector.addSelector(type, elem);
    }

    private SelectorElement parseEntry(Object value, ModelElement element, ClassType keyType) throws SelectorParseException {
        if (isMap(value)) {
            return new ComplexSelectorElement(element, parse(toMap(value), keyType));
        } else {
            return new BooleanSelectorElement(element, Boolean.valueOf(value.toString()));
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toMap(Object obj) {
        return (Map<String, Object>) obj;
    }

    private boolean isMap(final Object obj) {
        return obj instanceof Map;
    }
}
