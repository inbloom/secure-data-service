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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.impl.GenericEntity;

/**
 * Provides access to the custom data for a given SEA
 * @author sashton
 *
 */
@Component
public class SeaCustomDataProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SeaCustomDataProvider.class);

    @Autowired
    SlcInterface slcInterface;

    public Map<String, List<SliEntityLocator>> getIdMap(String seaGuid) {

        LOG.info("Attempting to pull id map from SEA custom data, will cause exception if doesn't exist");
        List<Entity> list = slcInterface.read("/educationOrganizations/" + seaGuid + "/custom");
        if (list.size() > 0) {
            Map<String, Object> rawMap = list.get(0).getData();
            return toSpecificMap(rawMap);
        }
        return new HashMap<String, List<SliEntityLocator>>();
    }

    public void storeIdMap(String seaGuid, Map<String, List<SliEntityLocator>> idMap) {
        GenericEntity entity = new GenericEntity("custom", toGenericMap(idMap));
        slcInterface.create(entity, "/educationOrganizations/" + seaGuid + "/custom");
    }

    private Map<String, Object> toGenericMap(Map<String, List<SliEntityLocator>> map) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (Entry<String, List<SliEntityLocator>> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<SliEntityLocator>> toSpecificMap(Map<String, Object> map) {
        Map<String, List<SliEntityLocator>> result = new HashMap<String, List<SliEntityLocator>>();
        for (Entry<String, Object> entry : map.entrySet()) {
            List<Map<String, String>> idList = (List<Map<String, String>>) entry.getValue();
            List<SliEntityLocator> locators = new ArrayList<SliEntityLocator>(idList.size());

            for (Map<String, String> subMap : idList) {
                locators.add(new SliEntityLocator(subMap));
            }

            result.put(entry.getKey(), locators);
        }
        return result;
    }

}
