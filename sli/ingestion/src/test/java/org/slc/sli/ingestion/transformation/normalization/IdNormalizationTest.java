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

    @Test
    public void TestConfigRead() {
       Ref  teacherSecAccRef = new Ref();

       FieldValue teacher = new FieldValue();
       teacher.sourceValue = "Teacher";
       Ref teacherRef = new Ref();
       teacherRef.collectionName = "Teacher";
       Field teacherField = new Field();
       teacherField.setPath("metaData.externalId");
       teacherField.setValue(teacher);
       teacherRef.fields = Arrays.asList(teacherField);
       teacher.ref = teacherRef;

       FieldValue sectionCodeVal = new FieldValue();
       sectionCodeVal.sourceValue = "uniqueSectionCode";
       Ref sectionCodeRef = new Ref();
       sectionCodeRef.collectionName = "section";
       Field sectionCodePath = new Field();
       sectionCodePath.setPath("body.uniqueSectionCode");
       sectionCodeRef.fields = Arrays.asList(sectionCodePath);

       IdNormalizer idNorm = new IdNormalizer();

       String internalId = idNorm.resolveInternalId(teacherSecAccRef);

       //Assert.assertEquals("123", someFieldValue);

    }
}
