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

import java.util.List;

import org.slc.sli.api.client.Entity;

/**
 * Mapping between SIF id and SLI ids.
 */
public interface SifIdResolver {

    /**
     * Given a sif reference id, returns the sli guid of the corresponding entity
     *
     * @param sifId
     * @param zoneId
     *            TODO
     * @return The SLI guid if the entity exists in SLI, null otherwise
     */
    public String getSliGuid(String sifId, String zoneId);
    public List<String> getSliGuidList(String sifId, String zoneId);

    /**
     * Given a sif reference id, returns the sli guid of the entity of the corresponding sli type
     *
     * This allows to find a sli guid by using the combination of sif reference id and a sli type
     *
     * @param sifId
     * @param sliType
     * @param zoneId
     *            TODO
     * @return The SLI guid if the entity exists in SLI, null otherwise
     */
    public String getSliGuidByType(String sifId, String sliType, String zoneId);
    public List<String> getSliGuidListByType(String sifId, String sliType, String zoneId);

    /**
     * Given a sif reference id, returns corresponding sli entity
     *
     * @param sifId
     * @param zoneId
     *            TODO
     * @return The SLI entity if the entity exists in SLI, null otherwise
     */
    public Entity getSliEntity(String sifId, String zoneId);
    public List<Entity> getSliEntityList(String sifId, String zoneId);

    /**
     * Given a sif reference id, returns the sli entity of the corresponding sli type
     *
     * This allows to find a sli entity by using the combination of sif reference id and a sli type
     *
     *
     * @param sifId
     * @param sliType
     * @param zoneId
     *            TODO
     * @return The SLI entity if the entity exists in SLI, null otherwise
     */
    public Entity getSliEntityByType(String sifId, String sliType, String zoneId);
    public List<Entity> getSliEntityListByType(String sifId, String sliType, String zoneId);

    /**
     * Given a sif Zone, returns the sli guid of the corresponding SEA
     *
     * @param sifZoneId
     * @return The SLI guid of the SEA associated with the Zone, null if none exist
     */
    public String getZoneSea(String sifZoneId);

    /**
     * Given a sif reference id and the guid and type of the corresponding entity,
     * saves the association
     *
     * @param sifId
     * @param sliType
     * @param sliId
     * @param zoneId
     *            TODO
     */
    public void putSliGuid(String sifId, String sliType, String sliId, String zoneId);

    /**
     * Retrieve a sli entity from the sifId of an associated (referenced) sli entity.
     *
     *
     * @param sifId
     * @param sliType
     * @param zoneId
     */
    public Entity getSliEntityFromOtherSifId(String sifId, String sliType, String zoneId);


    /**
     * Save the association of a guid of an sli entity with a sifId of an associated (referenced)
     * sli entity.
     *
     * This allows later to find a sli guid by using the combination of sif reference id and a sli type
     *
     * @param sifId
     * @param sliType
     * @param sliId
     * @param zoneId
     *            TODO
     */
    public void putSliGuidForOtherSifId(String sifId, String sliType, String sliId, String zoneId);
}
