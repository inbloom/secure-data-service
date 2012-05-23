package org.slc.sli.modeling.tools.xmi2Java.cmdline;

import java.io.IOException;

import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.index.ModelIndex;

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
