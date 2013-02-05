/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
package org.slc.sli.sif.slcinterface;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.client.impl.GenericEntity;
import org.slc.sli.api.client.util.Query;

/**
 * Test for SifIdResolverCustomData
 *
 */
public class SifIdResolverCustomDataTest {

    private static final String SIF_REF_ID_EMPTY_LIST = "SIF_REF_ID_EMPTY_LIST";
    private static final String SIF_REF_ID_NULL_LIST = "SIF_REF_ID_NULL_LIST";

    private static final String SEA_ID1 = "SEA_ID1";
    private static final String ZONE_ID1 = "ZONE_ID1";
    private static final String SLI_TYPE_SEA1 = "SLI_TYPE_SEA1";
    private static final String SLI_FIELD_SEA1 = "SLI_FIELD_SEA1";
    private static final String SLI_VALUE_SEA1 = "SLI_VALUE_SEA1";

    private static final String SEA_ID2 = "SEA_ID2";
    private static final String ZONE_ID2 = "ZONE_ID2";
    private static final String SLI_TYPE_SEA2 = "SLI_TYPE_SEA2";
    private static final String SLI_FIELD_SEA2 = "SLI_FIELD_SEA2";
    private static final String SLI_VALUE_SEA2 = "SLI_VALUE_SEA2";

    private static final String SIF_REF_ID1 = "SIF_REF_ID1";
    private static final String SLI_TYPE1 = "SLI_TYPE1";
    private static final String SLI_FIELD1 = ParameterConstants.ID; // for the putGuid test
    private static final String SLI_VALUE1 = "SLI_VALUE1";
    private static final String SLI_ID1 = "SLI_ID1";

    private static final String SIF_REF_ID2 = "SIF_REF_ID2";
    private static final String SLI_TYPE2 = "SLI_TYPE2";
    private static final String SLI_FIELD2 = "SLI_FIELD2";
    private static final String SLI_VALUE2 = "SLI_VALUE2";

    private static final String SIF_REF_ID3 = "SIF_REF_ID3";
    private static final String SLI_TYPE3A = "SLI_TYPE3A";
    private static final String SLI_FIELD3A = "SLI_FIELD3A";
    private static final String SLI_VALUE3A = "SLI_VALUE3A";
    private static final String SLI_ID3A = "SLI_ID3A";
    private static final String SLI_TYPE3B = "SLI_TYPE3B";
    private static final String SLI_FIELD3B = "SLI_FIELD3B";
    private static final String SLI_VALUE3B = "SLI_VALUE3B";
    private static final String SLI_ID3B = "SLI_ID3B";

    private static final String SIF_REF_ID4 = "SIF_REF_ID4";
    private static final String SLI_TYPE4 = "SLI_TYPE4";
    private static final String SLI_FIELD4 = "SLI_FIELD4";
    private static final String SLI_VALUE4 = "SLI_VALUE4";
    private static final String SLI_ID4 = "SLI_ID4";

    private static final String SIF_REF_ID_STAFF_EDORG = "SIF_REF_ID_STAFF_EDORG";

    @InjectMocks
    private SifIdResolverCustomData resolver = new SifIdResolverCustomData();

    @Mock
    ZoneMapProvider mockZoneMapProvider;

    @Mock
    SlcInterface mockSlcInterface;

    @Mock
    SeaCustomDataProvider mockSeaCustomDataProvider;

    @Captor
    ArgumentCaptor<Query> queryCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(mockSeaCustomDataProvider.getIdMap(SEA_ID1)).thenReturn(getDummyIdMap());
    }

    @Test
    public void getSliEntityShouldLookupSliEntities() throws Exception {
        setupCustomDataMocking();

        Entity expected = new GenericEntity(SLI_TYPE1, new HashMap<String, Object>());
        List<Entity> queryResult = Arrays.asList(new Entity[] { expected });
        when(mockSlcInterface.read(eq(SLI_TYPE1), any(Query.class))).thenReturn(queryResult);

        Entity result = resolver.getSliEntity(SIF_REF_ID1, ZONE_ID1);

        assertEquals(expected, result);
    }

    @Test
    public void getSliEntityShouldHandleEmptyQueryList() throws Exception {
        setupCustomDataMocking();

        List<Entity> queryResult = Arrays.asList(new Entity[] {});
        when(mockSlcInterface.read(eq(SLI_TYPE1), any(Query.class))).thenReturn(queryResult);

        Entity result = resolver.getSliEntity(SIF_REF_ID1, ZONE_ID1);

        Assert.assertNull("Result should be null", result);
    }

    @Test
    public void getSliEntityShouldUseCorrectType() throws Exception {
        setupCustomDataMocking();

        Entity expected = new GenericEntity(SLI_TYPE2, new HashMap<String, Object>());
        List<Entity> queryResult = Arrays.asList(new Entity[] { expected });

        when(mockSlcInterface.read(eq(SLI_TYPE2), any(Query.class))).thenReturn(queryResult);

        Entity result = resolver.getSliEntity(SIF_REF_ID2, ZONE_ID1);

        Mockito.verify(mockSlcInterface).read(eq(SLI_TYPE2), any(Query.class));
        assertEquals(expected, result);
    }

    @Test
    public void getSliEntityShouldUseQueryFilter() throws Exception {
        setupCustomDataMocking();

        Entity expected = new GenericEntity(SLI_TYPE2, new HashMap<String, Object>());
        List<Entity> queryResult = Arrays.asList(new Entity[] { expected });

        when(mockSlcInterface.read(eq(SLI_TYPE2), queryCaptor.capture())).thenReturn(queryResult);

        resolver.getSliEntity(SIF_REF_ID2, ZONE_ID1);

        Assert.assertEquals(SLI_VALUE2, queryCaptor.getAllValues().get(0).getParameters().get(SLI_FIELD2));
        Assert.assertEquals(1, queryCaptor.getAllValues().get(0).getParameters().get("limit"));
    }

    @Test
    public void getSliEntityShouldHandleEmptyLocatorList() throws Exception {
        setupCustomDataMocking();

        Entity result = resolver.getSliEntity(SIF_REF_ID_EMPTY_LIST, ZONE_ID1);

        Assert.assertNull("Entity should be null", result);
    }

    @Test
    public void getSliEntityShouldHandleNullLocatorList() throws Exception {
        setupCustomDataMocking();

        Entity result = resolver.getSliEntity(SIF_REF_ID_NULL_LIST, ZONE_ID1);

        Assert.assertNull("Entity should be null", result);
    }

    @Test
    public void getSliEntityShouldLookupSeaGuid() throws Exception {

        // setup
        when(mockZoneMapProvider.getZoneToSliIdMap()).thenReturn(getDummySeaIdMap());

        Entity seaEntity = Mockito.mock(Entity.class);
        when(seaEntity.getId()).thenReturn(SEA_ID2);
        List<Entity> queryResult = Arrays.asList(new Entity[] { seaEntity });

        when(mockSlcInterface.read(eq(SLI_TYPE_SEA2), queryCaptor.capture())).thenReturn(queryResult);

        when(mockSeaCustomDataProvider.getIdMap(SEA_ID2)).thenReturn(getDummyIdMap());

        // excute
        resolver.getSliEntity(SIF_REF_ID_NULL_LIST, ZONE_ID2);

        // verify
        Assert.assertEquals(SLI_VALUE_SEA2, queryCaptor.getAllValues().get(0).getParameters().get(SLI_FIELD_SEA2));
        verify(mockZoneMapProvider, times(1)).getZoneToSliIdMap();
        verify(mockSeaCustomDataProvider, times(1)).getIdMap(Mockito.anyString());
        verify(mockSeaCustomDataProvider, times(1)).getIdMap(SEA_ID2);
    }

    @Test
    public void getSliEntityShouldHandleNullSeaEntityList() throws Exception {

        // setup
        when(mockZoneMapProvider.getZoneToSliIdMap()).thenReturn(getDummySeaIdMap());
        when(mockSlcInterface.read(eq(SLI_TYPE_SEA2), queryCaptor.capture())).thenReturn(null);

        // excute
        Entity result = resolver.getSliEntity(SIF_REF_ID_NULL_LIST, ZONE_ID2);

        // verify
        Assert.assertNull(result);
    }

    @Test
    public void getSliEntityShouldHandleEmptySeaEntityList() throws Exception {

        // setup
        when(mockZoneMapProvider.getZoneToSliIdMap()).thenReturn(getDummySeaIdMap());
        when(mockSlcInterface.read(eq(SLI_TYPE_SEA2), queryCaptor.capture())).thenReturn(new ArrayList<Entity>());

        // excute
        Entity result = resolver.getSliEntity(SIF_REF_ID_NULL_LIST, ZONE_ID2);

        // verify
        Assert.assertNull(result);
    }

    @Test
    public void getZoneSeaShouldLookupSeaGuid() throws Exception {

        // setup
        when(mockZoneMapProvider.getZoneToSliIdMap()).thenReturn(getDummySeaIdMap());

        Entity seaEntity = Mockito.mock(Entity.class);
        when(seaEntity.getId()).thenReturn(SEA_ID2);
        List<Entity> queryResult = Arrays.asList(new Entity[] { seaEntity });

        when(mockSlcInterface.read(eq(SLI_TYPE_SEA2), queryCaptor.capture())).thenReturn(queryResult);

        // excute
        String seaId = resolver.getZoneSea(ZONE_ID2);

        // verify
        Assert.assertEquals(1, queryCaptor.getAllValues().size());
        Assert.assertEquals(SLI_VALUE_SEA2, queryCaptor.getAllValues().get(0).getParameters().get(SLI_FIELD_SEA2));
        Assert.assertEquals(SEA_ID2, seaId);
    }

    @Test
    public void getSliEntityListShouldLookupSliEntities() throws Exception {

        // setup
        setupCustomDataMocking();

        List<Entity> expected = new ArrayList<Entity>();

        Entity entity = new GenericEntity(SLI_TYPE3A, new HashMap<String, Object>());
        List<Entity> queryResult = Arrays.asList(new Entity[] { entity });
        when(mockSlcInterface.read(eq(SLI_TYPE3A), any(Query.class))).thenReturn(queryResult);
        expected.add(entity);

        entity = new GenericEntity(SLI_TYPE3B, new HashMap<String, Object>());
        queryResult = Arrays.asList(new Entity[] { entity });
        when(mockSlcInterface.read(eq(SLI_TYPE3B), any(Query.class))).thenReturn(queryResult);
        expected.add(entity);

        // execute
        List<Entity> result = resolver.getSliEntityList(SIF_REF_ID3, ZONE_ID1);

        // verify
        verify(mockSlcInterface, times(3)).read(Mockito.anyString(), any(Query.class));
        verify(mockSlcInterface, times(1)).read(eq(SLI_TYPE3A), any(Query.class));
        verify(mockSlcInterface, times(1)).read(eq(SLI_TYPE3B), any(Query.class));
        Assert.assertEquals(expected.size(), result.size());
        Assert.assertEquals(expected.get(0), result.get(0));
        Assert.assertEquals(expected.get(1), result.get(1));

    }

    @Test
    public void getSliEntityFromOtherSifIdShouldLookupSliEntities() throws Exception {
        setupCustomDataMocking();

        Entity expected = new GenericEntity(SLI_TYPE4, new HashMap<String, Object>());
        List<Entity> queryResult = Arrays.asList(new Entity[] { expected });
        when(mockSlcInterface.read(eq(SLI_TYPE4), any(Query.class))).thenReturn(queryResult);

        Entity result = resolver.getSliEntityFromOtherSifId(SIF_REF_ID4, SLI_TYPE4, ZONE_ID1);

        assertEquals(expected, result);
    }

    @Test
    public void getSliGuidShouldFindGuid() throws Exception {
        setupCustomDataMocking();

        Entity expected = Mockito.mock(Entity.class);
        when(expected.getId()).thenReturn(SLI_ID1);
        List<Entity> queryResult = Arrays.asList(new Entity[] { expected });

        when(mockSlcInterface.read(eq(SLI_TYPE1), any(Query.class))).thenReturn(queryResult);

        String result = resolver.getSliGuid(SIF_REF_ID1, ZONE_ID1);

        assertEquals(SLI_ID1, result);
    }

    @Test
    public void getSliGuidShouldHandleNull() throws Exception {
        setupCustomDataMocking();

        List<Entity> queryResult = Collections.emptyList();
        when(mockSlcInterface.read(eq(SLI_TYPE1), any(Query.class))).thenReturn(queryResult);

        String result = resolver.getSliGuid(SIF_REF_ID1, ZONE_ID1);

        Assert.assertNull(result);
    }

    @Test
    public void getSliGuidListShouldLookupSliEntities() throws Exception {

        // setup
        setupCustomDataMocking();

        List<Entity> expected = new ArrayList<Entity>();
        Entity entity = Mockito.mock(Entity.class);
        when(entity.getId()).thenReturn(SLI_ID3A);
        List<Entity> queryResult = Arrays.asList(new Entity[] { entity });
        when(mockSlcInterface.read(eq(SLI_TYPE3A), any(Query.class))).thenReturn(queryResult);
        expected.add(entity);

        entity = Mockito.mock(Entity.class);
        when(entity.getId()).thenReturn(SLI_ID3B);
        queryResult = Arrays.asList(new Entity[] { entity });
        when(mockSlcInterface.read(eq(SLI_TYPE3B), any(Query.class))).thenReturn(queryResult);
        expected.add(entity);

        // execute
        List<String> result = resolver.getSliGuidList(SIF_REF_ID3, ZONE_ID1);

        // verify
        Assert.assertEquals(expected.size(), result.size());
        Assert.assertEquals(SLI_ID3A, result.get(0));
        Assert.assertEquals(SLI_ID3B, result.get(1));

    }

    @Test
    public void getSliEntityListByTypeShouldLookupSliEntities() throws Exception {

        // setup
        setupCustomDataMocking();

        List<Entity> expected = new ArrayList<Entity>();

        Entity entity = Mockito.mock(Entity.class);
        when(entity.getId()).thenReturn(SLI_ID3A);
        List<Entity> queryResult = Arrays.asList(new Entity[] { entity });
        when(mockSlcInterface.read(eq(SLI_TYPE3A), any(Query.class))).thenReturn(queryResult);
        expected.add(entity);

        entity = Mockito.mock(Entity.class);
        when(entity.getId()).thenReturn(SLI_ID3B);
        queryResult = Arrays.asList(new Entity[] { entity });
        when(mockSlcInterface.read(eq(SLI_TYPE3B), any(Query.class))).thenReturn(queryResult);
        expected.add(entity);

        // execute
        List<Entity> result = resolver.getSliEntityListByType(SIF_REF_ID3, SLI_TYPE3A, ZONE_ID1);

        // verify
        verify(mockSlcInterface, times(3)).read(Mockito.anyString(), any(Query.class));
        verify(mockSlcInterface, times(1)).read(eq(SLI_TYPE3A), any(Query.class));
        verify(mockSlcInterface, times(1)).read(eq(SLI_TYPE3B), any(Query.class));
        Assert.assertEquals(expected.size(), result.size());
        Assert.assertEquals(expected.get(0), result.get(0));
        Assert.assertEquals(expected.get(1), result.get(1));

    }

    @Test
    public void getSliEntityByTypeShouldLookupSliEntities() throws Exception {
        setupCustomDataMocking();

        Entity expected = new GenericEntity(SLI_TYPE4, new HashMap<String, Object>());
        List<Entity> queryResult = Arrays.asList(new Entity[] { expected });
        when(mockSlcInterface.read(eq(SLI_TYPE4), any(Query.class))).thenReturn(queryResult);

        Entity result = resolver.getSliEntityByType(SIF_REF_ID4, SLI_TYPE4, ZONE_ID1);

        assertEquals(expected, result);
    }

    @Test
    public void getSliGuidListByTypeShouldLookupSliGuids() throws Exception {

        // setup
        setupCustomDataMocking();

        List<Entity> expected = new ArrayList<Entity>();

        Entity entity = Mockito.mock(Entity.class);
        when(entity.getId()).thenReturn(SLI_ID3A);
        List<Entity> queryResult = Arrays.asList(new Entity[] { entity });
        when(mockSlcInterface.read(eq(SLI_TYPE3A), any(Query.class))).thenReturn(queryResult);
        expected.add(entity);

        entity = Mockito.mock(Entity.class);
        when(entity.getId()).thenReturn(SLI_ID3B);
        queryResult = Arrays.asList(new Entity[] { entity });
        when(mockSlcInterface.read(eq(SLI_TYPE3B), any(Query.class))).thenReturn(queryResult);
        expected.add(entity);

        // execute
        List<String> result = resolver.getSliGuidListByType(SIF_REF_ID3, SLI_TYPE3A, ZONE_ID1);

        // verify
        Assert.assertEquals(expected.size(), result.size());
        Assert.assertEquals(SLI_ID3A, result.get(0));
        Assert.assertEquals(SLI_ID3B, result.get(1));

    }

    @Test
    public void getSliGuidByTypeShouldLookupSliGuid() throws Exception {
        setupCustomDataMocking();

        Entity entity = Mockito.mock(Entity.class);
        when(entity.getId()).thenReturn(SLI_ID4);
        List<Entity> queryResult = Arrays.asList(new Entity[] { entity });
        when(mockSlcInterface.read(eq(SLI_TYPE4), any(Query.class))).thenReturn(queryResult);

        String result = resolver.getSliGuidByType(SIF_REF_ID4, SLI_TYPE4, ZONE_ID1);

        assertEquals(SLI_ID4, result);
    }

    @Test
    public void getSliGuidByTypeShouldHandleNull() throws Exception {
        setupCustomDataMocking();

        Entity entity = Mockito.mock(Entity.class);
        when(entity.getId()).thenReturn(SLI_ID4);
        List<Entity> queryResult = Arrays.asList(new Entity[] {});
        when(mockSlcInterface.read(eq(SLI_TYPE4), any(Query.class))).thenReturn(queryResult);

        String result = resolver.getSliGuidByType(SIF_REF_ID4, SLI_TYPE4, ZONE_ID1);

        Assert.assertNull(result);
    }

    @Test
    public void putSliGuidStoreNewId() throws Exception {

        Map<String, List<SliEntityLocator>> idMap = setupCustomDataMocking();

        when(mockSeaCustomDataProvider.getIdMap(SEA_ID1)).thenReturn(idMap);

        resolver.putSliGuid("newSifId", "sliType", "sliId", ZONE_ID1);

        List<SliEntityLocator> resultList = idMap.get("newSifId");
        Assert.assertNotNull(resultList);
        Assert.assertEquals(1, resultList.size());

        SliEntityLocator locator = resultList.get(0);
        Assert.assertEquals("sliType", locator.getType());
        Assert.assertEquals("sliId", locator.getValue());
        Assert.assertEquals(ParameterConstants.ID, locator.getField());
    }

    @Test
    public void putSliGuidStoreAdditionalId() throws Exception {

        Map<String, List<SliEntityLocator>> idMap = setupCustomDataMocking();

        when(mockSeaCustomDataProvider.getIdMap(SEA_ID1)).thenReturn(idMap);

        resolver.putSliGuid(SIF_REF_ID1, SLI_TYPE1, "someNewId", ZONE_ID1);

        List<SliEntityLocator> resultList = idMap.get(SIF_REF_ID1);
        Assert.assertNotNull(resultList);
        Assert.assertEquals(2, resultList.size());

        SliEntityLocator locator = resultList.get(1);
        Assert.assertEquals(SLI_TYPE1, locator.getType());
        Assert.assertEquals("someNewId", locator.getValue());
        Assert.assertEquals(ParameterConstants.ID, locator.getField());
    }

    @Test
    public void putSliGuidUpdatelId() throws Exception {

        Map<String, List<SliEntityLocator>> idMap = setupCustomDataMocking();

        when(mockSeaCustomDataProvider.getIdMap(SEA_ID1)).thenReturn(idMap);

        resolver.putSliGuid(SIF_REF_ID1, SLI_TYPE1, SLI_VALUE1, ZONE_ID1);

        List<SliEntityLocator> resultList = idMap.get(SIF_REF_ID1);
        Assert.assertNotNull(resultList);
        Assert.assertEquals(1, resultList.size());

    }

    @Test
    public void putSliGuidForOtherSifIdStoreNewId() throws Exception {

        Map<String, List<SliEntityLocator>> idMap = setupCustomDataMocking();

        when(mockSeaCustomDataProvider.getIdMap(SEA_ID1)).thenReturn(idMap);

        resolver.putSliGuidForOtherSifId("newSifId", "sliType", "sliId", ZONE_ID1);

        List<SliEntityLocator> resultList = idMap.get("newSifId-sliType");
        Assert.assertNotNull(resultList);
        Assert.assertEquals(1, resultList.size());

        SliEntityLocator locator = resultList.get(0);
        Assert.assertEquals("sliType", locator.getType());
        Assert.assertEquals("sliId", locator.getValue());
        Assert.assertEquals(ParameterConstants.ID, locator.getField());
    }

    private Map<String, List<SliEntityLocator>> setupCustomDataMocking() throws Exception {
        when(mockZoneMapProvider.getZoneToSliIdMap()).thenReturn(getDummySeaIdMap());

        Entity seaEntity = Mockito.mock(Entity.class);
        when(seaEntity.getId()).thenReturn(SEA_ID1);
        List<Entity> queryResult = Arrays.asList(new Entity[] { seaEntity });

        when(mockSlcInterface.read(eq(SLI_TYPE_SEA1), any(Query.class))).thenReturn(queryResult);

        Map<String, List<SliEntityLocator>> idMap = getDummyIdMap();

        when(mockSeaCustomDataProvider.getIdMap(SEA_ID1)).thenReturn(idMap);

        return idMap;
    }

    /**
     * Creates a dummy map to be used in tests
     */
    private Map<String, List<SliEntityLocator>> getDummyIdMap() {
        Map<String, List<SliEntityLocator>> map = new HashMap<String, List<SliEntityLocator>>();

        List<SliEntityLocator> locators = new ArrayList<SliEntityLocator>();
        locators.add(new SliEntityLocator(SLI_TYPE1, SLI_VALUE1, SLI_FIELD1));
        map.put(SIF_REF_ID1, locators);

        locators = new ArrayList<SliEntityLocator>();
        locators.add(new SliEntityLocator(SLI_TYPE2, SLI_VALUE2, SLI_FIELD2));
        map.put(SIF_REF_ID2, locators);

        locators = new ArrayList<SliEntityLocator>();
        locators.add(new SliEntityLocator(SLI_TYPE3A, SLI_VALUE3A, SLI_FIELD3A));
        locators.add(new SliEntityLocator(SLI_TYPE3B, SLI_VALUE3B, SLI_FIELD3B));
        map.put(SIF_REF_ID3, locators);

        locators = new ArrayList<SliEntityLocator>();
        locators.add(new SliEntityLocator(SLI_TYPE3A, SLI_VALUE3A, SLI_FIELD3A));
        locators.add(new SliEntityLocator(SLI_TYPE3B, SLI_VALUE3B, SLI_FIELD3B));
        map.put(SIF_REF_ID3 + "-" + SLI_TYPE3A, locators);

        locators = new ArrayList<SliEntityLocator>();
        map.put(SIF_REF_ID_EMPTY_LIST, locators);

        locators = new ArrayList<SliEntityLocator>();
        locators.add(new SliEntityLocator(SLI_TYPE4, SLI_VALUE4, SLI_FIELD4));
        map.put(SIF_REF_ID4 + "-" + SLI_TYPE4, locators);

        // for the getSliEntityShouldFixStaffEdOrgAssociationType() test
        locators = new ArrayList<SliEntityLocator>();
        locators.add(new SliEntityLocator("staffEducationOrgAssignmentAssociation", SLI_VALUE4, SLI_FIELD4));
        map.put(SIF_REF_ID_STAFF_EDORG, locators);

        return map;
    }

    /**
     * Creates a dummy sea locator map to be used in tests
     */
    private Map<String, SliEntityLocator> getDummySeaIdMap() {
        Map<String, SliEntityLocator> map = new HashMap<String, SliEntityLocator>();

        map.put(ZONE_ID1, new SliEntityLocator(SLI_TYPE_SEA1, SLI_VALUE_SEA1, SLI_FIELD_SEA1));
        map.put(ZONE_ID2, new SliEntityLocator(SLI_TYPE_SEA2, SLI_VALUE_SEA2, SLI_FIELD_SEA2));

        return map;
    }

}
