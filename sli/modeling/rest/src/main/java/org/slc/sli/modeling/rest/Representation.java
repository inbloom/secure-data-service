package org.slc.sli.modeling.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * A <code>representation</code> element describes a representation of a resource's state.
 */
public final class Representation extends WadlElement {
    private final String id;
    private final QName element;
    private final String mediaType;
    private final List<String> profiles;
    private final List<Param> params;

    public Representation(final String id, final QName element, final String mediaType, final List<String> profiles,
            final List<Documentation> doc, final List<Param> params) {
        super(doc);
        if (null == mediaType) {
            throw new NullPointerException("mediaType");
        }
        if (null == profiles) {
            throw new NullPointerException("profiles");
        }
        if (null == params) {
            throw new NullPointerException("params");
        }
        this.id = id;
        this.element = element;
        this.mediaType = mediaType;
        this.profiles = Collections.unmodifiableList(new ArrayList<String>(profiles));
        this.params = Collections.unmodifiableList(new ArrayList<Param>(params));
    }

    public String getId() {
        return id;
    }

    public QName getElement() {
        return element;
    }

    public String getMediaType() {
        return mediaType;
    }

    public List<String> getProfiles() {
        return profiles;
    }

    public List<Param> getParams() {
        return params;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id").append(" : ").append(id);
        sb.append(", ");
        sb.append("element").append(" : ").append(element);
        sb.append(", ");
        sb.append("mediaType").append(" : ").append(mediaType);
        sb.append(", ");
        sb.append("profiles").append(" : ").append(profiles);
        sb.append(", ");
        sb.append("params").append(" : ").append(params);
        sb.append(", ");
        sb.append("doc").append(" : ").append(getDocumentation());
        sb.append("}");
        return sb.toString();
    }
}