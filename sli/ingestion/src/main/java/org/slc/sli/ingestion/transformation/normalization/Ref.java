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

}
