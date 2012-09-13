package org.slc.sli.ingestion.transformation.normalization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.cache.CacheProvider;
import org.slc.sli.ingestion.landingzone.validation.TestErrorReport;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.AppInfo;
import org.slc.sli.validation.schema.NeutralSchema;

/**
 * Unit tests for IdNormalizer to run without spring
 *
 * @author jtully
 *
 */
public class IdNormalizerTest {
    @InjectMocks
    IdNormalizer idNormalizer;

    @Mock
    private Repository<Entity> mockRepository;

    @Mock
    private CacheProvider mockCacheProvider;

    @Mock
    private SchemaRepository mockSchemaRepository;

    private static final String COL_NAME = "col_name";
    private static final String ENTITY_TYPE = "entity_type";

    @Before
    public void setup() {
        idNormalizer = new IdNormalizer();
        MockitoAnnotations.initMocks(this);

        NeutralSchema mockSchema = Mockito.mock(NeutralSchema.class);
        AppInfo mockAppInfo = Mockito.mock(AppInfo.class);

        // mock the schema collection name derivation
        Mockito.when(mockSchemaRepository.getSchema(Mockito.eq(ENTITY_TYPE))).thenReturn(mockSchema);
        Mockito.when(mockSchema.getAppInfo()).thenReturn(mockAppInfo);
        Mockito.when(mockAppInfo.getCollectionType()).thenReturn(COL_NAME);

        // mock the cache provider
        Mockito.when(mockCacheProvider.get(Mockito.anyString())).thenReturn(null);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void shouldResolveSimpleRef() throws IOException {
        EntityConfig entityConfig = createEntityConfig("simpleRef.json");

        String resolvedId = null;
        String targetId = "target_id";

        Entity entity = createResolvedEntity(resolvedId);
        ErrorReport errorReport = new TestErrorReport();

        Mockito.when(mockRepository.findByQuery(Mockito.eq(COL_NAME), Mockito.any(Query.class),
                Mockito.anyInt(), Mockito.anyInt())).thenReturn(createTargetEntities(targetId));

        idNormalizer.resolveInternalIds(entity, "SLI", entityConfig, errorReport);

        //verify that 1 query was performed
        Mockito.verify(mockRepository, Mockito.times(1)).findByQuery(Mockito.eq(COL_NAME), Mockito.any(Query.class), Mockito.anyInt(), Mockito.anyInt());

        assertNotNull("attribute resolved_id should not be null", entity.getBody().get("resolved_id"));
        assertEquals("attribute resolved_id should be resolved to " + targetId, targetId,
                entity.getBody().get("resolved_id"));
        assertFalse("no errors should be reported from reference resolution ", errorReport.hasErrors());
    }


    @SuppressWarnings("deprecation")
    @Test
    public void shouldNotResolveDeprecatedReferences() throws IOException {
        EntityConfig entityConfig = createEntityConfig("deprecatedRef.json");

        String resolvedId = "pre_resolved_id";

        Entity entity = createResolvedEntity(resolvedId);
        ErrorReport errorReport = new TestErrorReport();

        idNormalizer.resolveInternalIds(entity, "SLI", entityConfig, errorReport);

        //verify that no queries were performed
        Mockito.verify(mockRepository, Mockito.never()).findByQuery(Mockito.anyString(), Mockito.any(Query.class), Mockito.anyInt(), Mockito.anyInt());

        assertNotNull("attribute resolved_id should not be null", entity.getBody().get("resolved_id"));
        assertEquals("attribute resolved_id should be resolved to " + resolvedId, resolvedId,
                entity.getBody().get("resolved_id"));
        assertFalse("no errors should be reported from reference resolution ", errorReport.hasErrors());
    }

    /**
     * Create an entity in which the Id has already been resolved
     */
    private Entity createResolvedEntity(String resolvedId) {

        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("src_key_field", "src_field_value");
        attributes.put("resolved_id", resolvedId);

        NeutralRecord nr = new NeutralRecord();
        nr.setAttributes(attributes);

        Entity entity = new NeutralRecordEntity(nr);

        return entity;
    }

    private List<Entity> createTargetEntities(String targetId) {
        NeutralRecord nr = new NeutralRecord();
        nr.setRecordId(targetId);
        NeutralRecordEntity entity = new NeutralRecordEntity(nr);
        List<Entity> entities = new ArrayList<Entity>();
        entities.add(entity);

        return entities;
    }

    private EntityConfig createEntityConfig(String fileName) throws IOException {
        Resource jsonFile =  new ClassPathResource("idNormalizerTestConfigs/" + fileName);
        EntityConfig entityConfig  = EntityConfig.parse(jsonFile.getInputStream());

        return entityConfig;
    }

}
