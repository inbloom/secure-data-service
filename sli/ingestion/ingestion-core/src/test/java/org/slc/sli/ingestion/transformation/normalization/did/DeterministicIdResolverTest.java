/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.ingestion.transformation.normalization.did;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.landingzone.validation.TestErrorReport;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * Unit tests for DeterministicIdResolver
 *
 * @author jtully
 *
 */
public class DeterministicIdResolverTest {
    @InjectMocks
    DeterministicIdResolver didResolver;

    @Mock
    private UUIDGeneratorStrategy didGenerator;

    @Mock
    private DidEntityConfigFactory didEntityConfigs;

    @Mock
    private DidRefConfigFactory didRefConfigs;

    private static final String ENTITY_TYPE = "entity_type";
    private static final String SRC_KEY_FIELD = "key_field";
    private static final String SRC_KEY_VALUE = "key_value";
    private static final String DID_VALUE = "did_value";
    private static final String DID_TARGET_FIELD = "id_field";
    private static final String SRC_REF_FIELD = "ref_field";


    @Before
    public void setup() {
        didResolver = new DeterministicIdResolver();

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldResolveSimpleDid() throws IOException {
        Entity entity = createSourceEntity();

        DidRefConfig refConfig = createRefConfig("Simple_DID_ref_config.json");
        DidEntityConfig entityConfig = createEntityConfig("Simple_DID_entity_config.json");

        Mockito.when(didRefConfigs.getDidRefConfiguration(Mockito.eq(ENTITY_TYPE))).thenReturn(refConfig);
        Mockito.when(didEntityConfigs.getDidEntityConfiguration(Mockito.eq(ENTITY_TYPE))).thenReturn(entityConfig);
        //maybe we should implementent an equals method for NaturalKeyDescriptor to allow a better test
        Mockito.when(didGenerator.generateId(Mockito.any(NaturalKeyDescriptor.class))).thenReturn(DID_VALUE);

        ErrorReport errorReport = new TestErrorReport();

        didResolver.resolveInternalIds(entity, "TENANT", errorReport);

        Assert.assertEquals(DID_VALUE, entity.getBody().get(DID_TARGET_FIELD));
        Assert.assertFalse("no errors should be reported from reference resolution ", errorReport.hasErrors());
    }

    @Test
    public void shouldFailFastForNonDidEntities() {

    }

    @Test
    public void shouldResolveNestedDid() {

    }

    @Test
    public void shouldGenerateErrorReportWithBadData() {

    }

    private DidEntityConfig createEntityConfig(String fileName) throws IOException {
        Resource jsonFile =  new ClassPathResource("DeterministicIdResolverConfigs/" + fileName);
        DidEntityConfig entityConfig  = DidEntityConfig.parse(jsonFile.getInputStream());
        return entityConfig;
    }

    private DidRefConfig createRefConfig(String fileName) throws IOException {
        Resource jsonFile =  new ClassPathResource("DeterministicIdResolverConfigs/" + fileName);
        DidRefConfig refConfig  = DidRefConfig.parse(jsonFile.getInputStream());
        return refConfig;
    }

    /**
     * Create an entity in which the Id has already been resolved
     */
    private Entity createSourceEntity() {

        Map<String, String> refObject = new HashMap<String, String>();
        refObject.put(SRC_KEY_FIELD, SRC_KEY_VALUE);

        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(SRC_REF_FIELD, refObject);

        NeutralRecord nr = new NeutralRecord();
        nr.setAttributes(attributes);
        nr.setRecordType(ENTITY_TYPE);

        Entity entity = new NeutralRecordEntity(nr);

        return entity;
    }

}
