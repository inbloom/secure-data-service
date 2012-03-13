package org.slc.sli.ingestion.transformation.normalization;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class IdNormalizationTest {

    @Test
    public void TestRefResolution() {
        Ref myCollectionId = new Ref();
        myCollectionId.setCollectionName("MyCollection");
        Field columnField = new Field();
        columnField.setPath("column");

        FieldValue columnValue = new FieldValue();
        columnValue.sourceValue = "5";
        columnField.setValue(columnValue);

        //TODO: ChoiceOfFields needs to be set properly
        //myCollectionId.setChoiceOfFields(Arrays.asList(columnField));

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
       teacherRef.setCollectionName("Teacher");
       Field teacherField = new Field();
       teacherField.setPath("metaData.externalId");
       teacherField.setValue(teacher);
       //TODO: ChoiceOfFields needs to be set properly
       //teacherRef.setChoiceOfFields(Arrays.asList(teacherField));
       teacher.ref = teacherRef;

       FieldValue sectionCodeVal = new FieldValue();
       sectionCodeVal.sourceValue = "uniqueSectionCode";
       Ref sectionCodeRef = new Ref();
       sectionCodeRef.setCollectionName("section");
       Field sectionCodePath = new Field();
       sectionCodePath.setPath("body.uniqueSectionCode");
       //TODO: ChoiceOfFields needs to be set properly
       //sectionCodeRef.setChoiceOfFields(Arrays.asList(sectionCodePath));

       IdNormalizer idNorm = new IdNormalizer();

       String internalId = idNorm.resolveInternalId(teacherSecAccRef);

       //Assert.assertEquals("123", someFieldValue);

    }
}
