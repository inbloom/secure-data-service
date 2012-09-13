package org.slc.sli.dal.adapter;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: srupasinghe
 * Date: 9/13/12
 * Time: 11:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class SchemaMapping {
    private String type;
    private Map<String, String> attributes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}
