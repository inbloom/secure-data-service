package org.slc.sli.ingestion.transformation.normalization;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class IdNormalizationTest {

    @Test
    public void TestRefResolution() {
        Ref myCollectionId = new Ref();
        myCollectionId.collectionName = "MyCollection";
        Field columnField = new Field();
        columnField.setPath("column");

        FieldValue columnValue = new FieldValue();
        columnValue.sourceValue = "5";
        columnField.setValue(columnValue);
        myCollectionId.fields = Arrays.asList(columnField);

        IdNormalizer idNorm = new IdNormalizer();

        String internalId = idNorm.resolveInternalId(myCollectionId);

        Assert.assertEquals("123", internalId);
    }
}
