package org.slc.sli.api.selectors.model;

import org.slc.sli.modeling.uml.Type;
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

    public SemanticSelector parse(final Map<String, Object> selectors, final Type type) throws SelectorParseException {
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

    private void addEntryToSelector(final Type type, final SemanticSelector selector, final String key, final Object value)
            throws SelectorParseException {
        final SelectorElement elem;
        if (modelProvider.isAssociation(type, key)) {
            final Type keyType = modelProvider.getType(type, key);
            if (isMap(value)) {
                elem = new ComplexSelectorElement(keyType, parse(toMap(value), keyType));
            } else if (value.equals(SelectorElement.INCLUDE_ALL)) {
                elem = new IncludeAllSelectorElement(keyType);
            } else {
                elem = new BooleanSelectorElement(keyType, Boolean.valueOf(value.toString()));
            }
        } else if (modelProvider.isAttribute(type, key)) {
            elem = new BooleanSelectorElement(key, Boolean.valueOf(value.toString()));
        } else {
            throw new SelectorParseException("Invalid Selectors " + key);
        }
        selector.addSelector(type, elem);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toMap(Object obj) {
        return (Map<String, Object>) obj;
    }

    private boolean isMap(final Object obj) {
        return obj instanceof Map;
    }
}
