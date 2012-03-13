package org.slc.sli.ingestion.transformation.normalization;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import org.slc.sli.domain.MongoEntity;
import org.slc.sli.ingestion.validation.DummyErrorReport;

/**
 * ID Normalizer unit tests.
 *
 * @author okrook
 *
 */
public class IdNormalizationTest {

    @Test
    public void testRefResolution() {
        Ref myCollectionId = new Ref();
        myCollectionId.setCollectionName("MyCollection");
        Field columnField = new Field();
        columnField.setPath("column");

        FieldValue columnValue = new FieldValue();
        columnValue.setValueSource("5");
        columnField.setValues(Arrays.asList(columnValue));

        //TODO: ChoiceOfFields needs to be set properly
        //myCollectionId.setChoiceOfFields(Arrays.asList(columnField));

        IdNormalizer idNorm = new IdNormalizer();

        Map<String, Object> body = new HashMap<String, Object>();
        MongoEntity entity = new MongoEntity("test", body);

        String internalId = idNorm.resolveInternalId(entity, myCollectionId, new DummyErrorReport());

        Assert.assertEquals("123", internalId);
    }

    @Ignore
    @Test
    public void testConfigRead() {
/*       Ref teacherSecAccRef = new Ref();

       FieldValue teacher = new FieldValue();
       teacher.valueSource = "Teacher";
       Ref teacherRef = new Ref();
       teacherRef.setCollectionName("Teacher");
       Field teacherField = new Field();
       teacherField.setPath("metaData.externalId");
       teacherField.setValue(teacher);
       //TODO: ChoiceOfFields needs to be set properly
       //teacherRef.setChoiceOfFields(Arrays.asList(teacherField));
       teacher.ref = teacherRef;

       FieldValue sectionCodeVal = new FieldValue();
       sectionCodeVal.valueSource = "uniqueSectionCode";
       Ref sectionCodeRef = new Ref();
       sectionCodeRef.setCollectionName("section");
       Field sectionCodePath = new Field();
       sectionCodePath.setPath("body.uniqueSectionCode");
       //TODO: ChoiceOfFields needs to be set properly
       //sectionCodeRef.setChoiceOfFields(Arrays.asList(sectionCodePath));

       IdNormalizer idNorm = new IdNormalizer();

       String internalId = idNorm.resolveInternalId(teacherSecAccRef, new DummyErrorReport());

       Assert.assertEquals("123", internalId);
*/
    }
}
