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

    public SemanticSelector parse(final Map<String, Object> selectors, final Type type) {
        if (type == null) throw new NullPointerException("type");
        if (selectors == null) throw new NullPointerException("selectors");

        final SemanticSelector selector = new SemanticSelector();
        for (final Map.Entry<String, Object> entry : selectors.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();

            if (modelProvider.isAssociation(type, key)) {
                final Type keyType = modelProvider.getType(type, entry.getKey());
                if (isMap(value)) {
                    selector.addSelector(type, parse(toMap(value), keyType));
                }
            } else if (modelProvider.isAttribute(type, key)) {
                selector.addSelector(type, key);
            } else {
                throw new AssertionError("Invalid Selectors " + entry.getKey());
            }
        }

        return selector;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toMap(Object obj) {
        return (Map<String, Object>) obj;
    }

    private boolean isMap(final Object obj) {
        return obj instanceof Map;
    }

}
