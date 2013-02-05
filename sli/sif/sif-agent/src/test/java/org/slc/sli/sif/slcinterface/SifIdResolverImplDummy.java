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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.api.client.Entity;

/**
 * Mapping between SIF id and SLI ids. Dummy implementation
 */
public class SifIdResolverImplDummy implements SifIdResolver {

    private Map<String, String> sifToSliIdMap = new HashMap<String, String>();
    private Map<String, Entity> sifToSliEntityMap = new HashMap<String, Entity>();

    public void reset() {
        sifToSliIdMap = new HashMap<String, String>();
        sifToSliEntityMap = new HashMap<String, Entity>();
    }

    public void putEntity(String sifId, Entity e) {
        sifToSliEntityMap.put(sifId, e);
    }

    @Override
    public String getSliGuid(String sifId, String zoneId) {
        return sifToSliIdMap.get(sifId);
    }

    @Override
    public Entity getSliEntity(String sifId, String zoneId) {
        return sifToSliEntityMap.get(sifId);
    }

    @Override
    public String getZoneSea(String zoneId) {
        return null;
    }

    @Override
    public void putSliGuid(String sifId, String sliType, String sliId, String zoneId) {
        sifToSliIdMap.put(sifId, sliId);
    }

    @Override
    public String getSliGuidByType(String sifId, String sliType, String zoneId) {
        return sifToSliIdMap.get(sifId);
    }

    @Override
    public Entity getSliEntityFromOtherSifId(String sifId, String sliType, String zoneId) {
        return null;
    }

    @Override
    public void putSliGuidForOtherSifId(String sifId, String sliType, String sliId, String zoneId) {
    }

    @Override
    public Entity getSliEntityByType(String sifId, String sliType, String zoneId) {
        return null;
    }

    @Override
    public List<String> getSliGuidList(String sifId, String zoneId) {
        return null;
    }

    @Override
    public List<String> getSliGuidListByType(String sifId, String sliType, String zoneId) {
        return null;
    }

    @Override
    public List<Entity> getSliEntityList(String sifId, String zoneId) {
        return null;
    }

    @Override
    public List<Entity> getSliEntityListByType(String sifId, String sliType, String zoneId) {
        return null;
    }
}
