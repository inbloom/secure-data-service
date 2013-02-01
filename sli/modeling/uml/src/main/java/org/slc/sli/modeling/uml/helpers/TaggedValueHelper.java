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


package org.slc.sli.modeling.uml.helpers;

import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.Taggable;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.index.ModelIndex;

/**
 * Convenience functions for accessing tagged values.
 */
public class TaggedValueHelper {

    public static final boolean getBooleanTag(final String name, final Taggable element, final ModelIndex lookup,
            final boolean defaultValue) {
        for (final TaggedValue taggedValue : element.getTaggedValues()) {
            final TagDefinition tagDefinition = lookup.getTagDefinition(taggedValue.getTagDefinition());
            if (name.equals(tagDefinition.getName())) {
                return Boolean.valueOf(taggedValue.getValue());
            }
        }
        return defaultValue;
    }

    public static final String getStringTag(final String name, final Taggable element, final ModelIndex lookup,
            final String defaultValue) {
        for (final TaggedValue taggedValue : element.getTaggedValues()) {
            final TagDefinition tagDefinition = lookup.getTagDefinition(taggedValue.getTagDefinition());
            if (name.equals(tagDefinition.getName())) {
                return taggedValue.getValue();
            }
        }
        return defaultValue;
    }

    public static final boolean hasTag(final String name, final Taggable element, final ModelIndex lookup) {
        for (final TaggedValue taggedValue : element.getTaggedValues()) {
            final TagDefinition tagDefinition = lookup.getTagDefinition(taggedValue.getTagDefinition());
            if (name.equals(tagDefinition.getName())) {
                return true;
            }
        }
        return false;
    }
}
