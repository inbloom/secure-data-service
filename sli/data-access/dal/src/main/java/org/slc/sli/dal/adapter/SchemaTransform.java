package org.slc.sli.dal.adapter;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: srupasinghe
 * Date: 9/13/12
 * Time: 11:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class SchemaTransform {
    private String type;
    private List<SchemaMapping> transforms;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<SchemaMapping> getTransforms() {
        return transforms;
    }

    public void setTransforms(List<SchemaMapping> transforms) {
        this.transforms = transforms;
    }
}
