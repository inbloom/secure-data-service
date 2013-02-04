/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

package org.slc.sli.api.selectors.model;

import org.slc.sli.api.model.ModelProvider;
import org.slc.sli.api.selectors.model.elem.BooleanSelectorElement;
import org.slc.sli.api.selectors.model.elem.ComplexSelectorElement;
import org.slc.sli.api.selectors.model.elem.EmptySelectorElement;
import org.slc.sli.api.selectors.model.elem.IncludeAllSelectorElement;
import org.slc.sli.api.selectors.model.elem.IncludeDefaultSelectorElement;
import org.slc.sli.api.selectors.model.elem.IncludeXSDSelectorElement;
import org.slc.sli.api.selectors.model.elem.SelectorElement;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.ModelElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Default implementation of semantic model of the selectors
 *
 * @author jstokes
 *
 */
@Component
public class DefaultSelectorSemanticModel implements SelectorSemanticModel {

    @Autowired
    private ModelProvider modelProvider;

    public SemanticSelector parse(final Map<String, Object> selectors, final ClassType type) throws SelectorParseException {
        if (type == null) {
            throw new IllegalArgumentException("type");
        }
        if (selectors == null) {
            throw new IllegalArgumentException("selectors");
        }

        final SemanticSelector selector = new SemanticSelector();
        if (selectors.isEmpty()) {
            selector.addSelector(type, new EmptySelectorElement(type));
        }

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
        } else if (key.equals(SelectorElement.INCLUDE_XSD)) {
            elem = new IncludeXSDSelectorElement(type);
        } else if (key.equals(SelectorElement.INCLUDE_DEFAULT)) {
            elem = new IncludeDefaultSelectorElement(type);
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

    protected void setModelProvider(final ModelProvider provider) {
        this.modelProvider = provider;
    }
}
