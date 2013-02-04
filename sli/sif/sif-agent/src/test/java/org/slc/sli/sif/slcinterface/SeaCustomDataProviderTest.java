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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.impl.GenericEntity;

/**
 * Tests SeaCustomDataProvider
 *
 * @author sashton
 *
 */
public class SeaCustomDataProviderTest {

    private static final String TYPE = "TYPE";
    private static final String VALUE = "VALUE";
    private static final String FIELD = "FIELD";
    private static final String SIF_ID = "SIF_ID";

    @InjectMocks
    private SeaCustomDataProvider dataProvider = new SeaCustomDataProvider();

    @Mock
    SlcInterface mockSlcInterface;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetMap() {
        String seaGuid = "asladfalsd";
        String url = "/educationOrganizations/" + seaGuid + "/custom";

        Mockito.when(mockSlcInterface.read(url)).thenReturn(buildCustomDataList());

        Map<String, List<SliEntityLocator>> result = dataProvider.getIdMap(seaGuid);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.keySet().size());

        List<SliEntityLocator> list = result.get(SIF_ID);

        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());

        SliEntityLocator locator = list.get(0);

        Assert.assertEquals(TYPE, locator.getType());
        Assert.assertEquals(VALUE, locator.getValue());
        Assert.assertEquals(FIELD, locator.getField());

    }

    @Test
    public void testStoreMap() {
        // setup
        String seaGuid = "asladfalsd";
        String url = "/educationOrganizations/" + seaGuid + "/custom";

        Mockito.when(mockSlcInterface.read(url)).thenReturn(buildCustomDataList());

        Map<String, List<SliEntityLocator>> idMap = dataProvider.getIdMap(seaGuid);

        // execute
        dataProvider.storeIdMap(seaGuid, idMap);

        // verify
        Mockito.verify(mockSlcInterface).create(Mockito.any(GenericEntity.class), Mockito.eq(url));

    }

    private List<Entity> buildCustomDataList() {

        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, String>> subList = new ArrayList<Map<String, String>>();
        Map<String, String> subMap = new HashMap<String, String>();
        subMap.put("type", TYPE);
        subMap.put("value", VALUE);
        subMap.put("field", FIELD);
        subList.add(subMap);
        map.put(SIF_ID, subList);

        Entity entity = new GenericEntity(TYPE, map);
        return Arrays.asList(new Entity[] { entity });
    }

}
