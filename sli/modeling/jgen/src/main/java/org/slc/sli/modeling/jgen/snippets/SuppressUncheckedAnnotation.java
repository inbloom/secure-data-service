package org.slc.sli.modeling.jgen.snippets;

/**
 * @author jstokes
 */
public class SuppressUncheckedAnnotation extends Annotation {
    public SuppressUncheckedAnnotation() {
        super("SuppressWarnings", "\"unchecked\"");
    }
}
