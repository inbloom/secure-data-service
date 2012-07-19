package org.slc.sli.modeling.jgen.snippets;

import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaStreamWriter;

import java.io.IOException;

/**
 * @author jstokes
 */
public class Annotation implements JavaSnippet {

    private final String annotationTag;
    private final String annotationValue;

    public Annotation(final String annotationTag) {
        this.annotationTag = annotationTag;
        this.annotationValue = null;
    }

    public Annotation(final String annotationTag, final String annotationValue) {
        this.annotationTag = annotationTag;
        this.annotationValue = annotationValue;
    }
    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        jsw.write("@").write(annotationTag);
        if (annotationValue != null) {
            jsw.parenL().write(annotationValue).parenR();
        }
        jsw.write("\n");
    }
}
