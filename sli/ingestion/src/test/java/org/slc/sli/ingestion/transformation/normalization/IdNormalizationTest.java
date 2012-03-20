package org.slc.sli.ingestion.transformation.normalization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.any;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;
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
        @SuppressWarnings("unchecked")
        Repository<Entity> repo = Mockito.mock(Repository.class);
        @SuppressWarnings("unchecked")
        Repository<Entity> repoNull = Mockito.mock(Repository.class);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("field", 5);

        MongoEntity entity = new MongoEntity("test", body);

        Entity expectedRecord = Mockito.mock(Entity.class);
        Mockito.when(expectedRecord.getEntityId()).thenReturn("123");

        Mockito.when(repo.findAll(Mockito.eq("MyCollection"), any(NeutralQuery.class))).thenReturn(Arrays.asList(expectedRecord));
        Mockito.when(repoNull.findAll(Mockito.eq("MyCollection"), any(NeutralQuery.class))).thenReturn(null);

        idNorm.setEntityRepository(repo);

        String internalId = idNorm.resolveInternalId(entity, "someNamespace", myCollectionId, new DummyErrorReport());

        Assert.assertEquals("123", internalId);

        idNorm.setEntityRepository(repoNull);

        //Testing findByQuery returns null
        internalId = idNorm.resolveInternalId(entity, "someNamespace", myCollectionId, new DummyErrorReport());
        Assert.assertEquals(null, internalId);
    }

    @Test
    public void testRefResolution2() {
        Ref myCollectionId = new Ref();
        myCollectionId.setCollectionName("MyCollection");
        Field columnField = new Field();
        columnField.setPath("column");

        List<RefDef> refDefs = new ArrayList<RefDef>();
        RefDef refDef = new RefDef();
        refDef.setRef(myCollectionId);
        refDef.setFieldPath("body.field");
        refDefs.add(refDef);
        EntityConfig entityConfig = new EntityConfig();
        entityConfig.setReferences(refDefs);
        EntityConfig entityConfig2 = new EntityConfig();
        entityConfig2.setReferences(null);

        FieldValue columnValue = new FieldValue();
        columnValue.setValueSource("body.field");
        columnField.setValues(Arrays.asList(columnValue));

        List<Field> fields = Arrays.asList(columnField);
        @SuppressWarnings("unchecked")
        List<List<Field>> choice = Arrays.asList(fields);
        myCollectionId.setChoiceOfFields(choice);

        IdNormalizer idNorm = new IdNormalizer();
        @SuppressWarnings("unchecked")
        Repository<Entity> repo = Mockito.mock(Repository.class);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("field", 5);

        MongoEntity entity = new MongoEntity("test", body);

        Entity expectedRecord = Mockito.mock(Entity.class);
        Mockito.when(expectedRecord.getEntityId()).thenReturn("123");

        Mockito.when(repo.findAll(Mockito.eq("MyCollection"), Mockito.any(NeutralQuery.class))).thenReturn(Arrays.asList(expectedRecord));

        idNorm.setEntityRepository(repo);

        idNorm.resolveInternalIds(entity, "someNamespace", entityConfig, new DummyErrorReport());

        Assert.assertEquals("123", entity.getBody().get("field"));

        //Testing entityConfig.getReference == null
        idNorm.resolveInternalIds(entity, "someNamespace", entityConfig2, new DummyErrorReport());
        Assert.assertEquals("123", entity.getBody().get("field"));
    }

    @Test
    public void testCollectionRefResolution() {
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
        @SuppressWarnings("unchecked")
        Repository<Entity> repo = Mockito.mock(Repository.class);

        Map<String, Object> body = new HashMap<String, Object>();

        List<String> value = new ArrayList<String>();

        value.add("5");
        value.add("6");
        value.add("7");
        body.put("field", value);

        MongoEntity entity = new MongoEntity("test", body);

        Entity expectedRecord = Mockito.mock(Entity.class);
        Mockito.when(expectedRecord.getEntityId()).thenReturn("123");

        Entity secondRecord = Mockito.mock(Entity.class);
        Mockito.when(secondRecord.getEntityId()).thenReturn("456");

        Entity thirdRecord = Mockito.mock(Entity.class);
        Mockito.when(thirdRecord.getEntityId()).thenReturn("789");

        ArrayList<Entity> records = new ArrayList<Entity>();
        records.add(expectedRecord);
        records.add(secondRecord);
        records.add(thirdRecord);


        Mockito.when(repo.findAll(Mockito.eq("MyCollection"), Mockito.any(NeutralQuery.class))).thenReturn(records);

        idNorm.setEntityRepository(repo);

        String internalId = idNorm.resolveInternalId(entity, "someNamespace", myCollectionId, new DummyErrorReport());

        Assert.assertEquals("123", internalId);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMultiRefResolution() {

        Ref secondCollection = new Ref();
        secondCollection.setCollectionName("secondCollection");
        Field secondCollectionField = new Field();
        secondCollectionField.setPath("column");
        FieldValue fValue = new FieldValue();
        fValue.setValueSource("body.field");
        secondCollectionField.setValues(Arrays.asList(fValue));
        secondCollection.setChoiceOfFields(Arrays.asList(Arrays.asList(secondCollectionField)));

        Ref myCollectionId = new Ref();
        myCollectionId.setCollectionName("MyCollection");
        Field columnField = new Field();
        columnField.setPath("body.secondCollectionId");

        FieldValue columnValue = new FieldValue();
        columnValue.setRef(secondCollection);
        columnField.setValues(Arrays.asList(columnValue));

        List<Field> fields = Arrays.asList(columnField);
        @SuppressWarnings("unchecked")
        List<List<Field>> choice = Arrays.asList(fields);
        myCollectionId.setChoiceOfFields(choice);



        IdNormalizer idNorm = new IdNormalizer();
        @SuppressWarnings("unchecked")
        Repository<Entity> repo = Mockito.mock(Repository.class);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("field", 5);

        MongoEntity entity = new MongoEntity("test", body);

        Entity expectedRecord = Mockito.mock(Entity.class);
        Mockito.when(expectedRecord.getEntityId()).thenReturn("123");

        Mockito.when(repo.findAll(Mockito.eq("MyCollection"), Mockito.any(NeutralQuery.class))).thenReturn(Arrays.asList(expectedRecord));

        Entity secondRecord = Mockito.mock(Entity.class);
        Mockito.when(secondRecord.getEntityId()).thenReturn("456");
        Mockito.when(repo.findAll(Mockito.eq("secondCollection"), Mockito.any(NeutralQuery.class))).thenReturn(Arrays.asList(secondRecord));

        idNorm.setEntityRepository(repo);

        String internalId = idNorm.resolveInternalId(entity, "someNamespace", myCollectionId, new DummyErrorReport());

        Assert.assertEquals("123", internalId);

        String secinternalId = idNorm.resolveInternalId(entity, "someNamespace", secondCollection, new DummyErrorReport());
        Assert.assertEquals("456", secinternalId);
    }
}
