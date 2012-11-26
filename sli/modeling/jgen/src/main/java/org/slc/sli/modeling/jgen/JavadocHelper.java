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


package org.slc.sli.modeling.jgen;

import java.io.IOException;

import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.index.ModelIndex;

/**
 * Model Java Javadoc comments.
 */
public final class JavadocHelper {

    public static final void writeJavadoc(final ModelElement element, final ModelIndex model, final JavaStreamWriter jsw)
            throws IOException {
        for (final TaggedValue taggedValue : element.getTaggedValues()) {
            final TagDefinition tagDefinition = model.getTagDefinition(taggedValue.getTagDefinition());
            if (tagDefinition.getName().equals("documentation")) {
                jsw.writeComment(taggedValue.getValue());
            }
        }
    }

}
