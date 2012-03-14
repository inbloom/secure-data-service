package org.slc.sli.ingestion.transformation.normalization;

import java.util.Arrays;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.Repository;
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
        columnValue.setValueSource("body.field");
        columnField.setValues(Arrays.asList(columnValue));

        List<Field> fields = Arrays.asList(columnField);
        @SuppressWarnings("unchecked")
        List<List<Field>> choice = Arrays.asList(fields);
        myCollectionId.setChoiceOfFields(choice);

        IdNormalizer idNorm = new IdNormalizer();
        Repository repo = Mockito.mock(Repository.class);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("field", 5);

        MongoEntity entity = new MongoEntity("test", body);

        Entity expectedRecord = Mockito.mock(Entity.class);
        Mockito.when(expectedRecord.getEntityId()).thenReturn("123");

        Mockito.when(repo.findByQuery(Mockito.eq("MyCollection"), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0))).thenReturn(Arrays.asList(expectedRecord));

        idNorm.setEntityRepository(repo);

        String internalId = idNorm.resolveInternalId(entity, "someNamespace", myCollectionId, new DummyErrorReport());

        Assert.assertEquals("123", internalId);
    }
}
