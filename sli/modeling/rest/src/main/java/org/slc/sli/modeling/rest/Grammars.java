package org.slc.sli.modeling.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The <code>grammars</code> element acts as a container for definitions of the format of data
 * exchanged during execution of the protocol described by the WADL document. Such definitions may
 * be included in-line or by reference using the <code>include</code> element. No particular data
 * format definition language is mandated.
 */
public final class Grammars extends WadlElement {
    private final List<Include> includes;

    public Grammars(final List<Documentation> doc, final List<Include> includes) {
        super(doc);
        if (null == includes) {
            throw new NullPointerException("includes");
        }
        this.includes = Collections.unmodifiableList(new ArrayList<Include>(includes));
    }

    public List<Include> getIncludes() {
        return includes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("includes").append(" : ").append(includes);
        sb.append(", ");
        sb.append("doc").append(" : ").append(getDocumentation());
        sb.append("}");
        return sb.toString();
    }
}