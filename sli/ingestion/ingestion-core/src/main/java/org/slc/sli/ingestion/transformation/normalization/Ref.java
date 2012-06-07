package org.slc.sli.ingestion.transformation.normalization;

import java.util.List;

/**
 * Holds definition for external reference that needs to be resolved.
 *
 * @author okrook
 *
 */
public class Ref {
    private String collectionName;
    private List<List<Field>> choiceOfFields;
    private boolean isRefList = false;
    private String refObjectPath;
    private boolean optional = false;

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public List<List<Field>> getChoiceOfFields() {
        return choiceOfFields;
    }

    public void setChoiceOfFields(List<List<Field>> choiceOfFields) {
        this.choiceOfFields = choiceOfFields;
    }

    public boolean isRefList() {
        return isRefList;
    }

    public void setIsRefList(boolean isRefList) {
        this.isRefList = isRefList;
    }

    public String getRefObjectPath() {
        return refObjectPath;
    }

    public void setRefObjectPath(String refObjectPath) {
        this.refObjectPath = refObjectPath;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public boolean isOptional() {
        return optional;
    }

}
