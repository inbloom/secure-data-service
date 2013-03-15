package org.slc.sli.validation.schema;

/**
 * Created with IntelliJ IDEA.
 * User: ldalgado
 * Date: 3/15/13
 * Time: 5:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class SchemaReferenceNode {

    String name;
    String references;
    boolean isArray = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public boolean isArray() {
        return isArray;
    }

    public void setArray(boolean array) {
        isArray = array;
    }

    @Override
    public String toString() {
        String r = name;
        if(isArray) {
            r += '*';
        }
        if(references != null) {
            r += "@" + references;
        }
        return r;
    }
}
