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

package org.slc.sli.ingestion.tenant;

import java.util.List;
import java.util.Map;

/**
 * Interface for access to tenant data.
 *
 * @author jtully
 */
public interface TenantDA {
    List<String> getLzPaths();

    String getTenantId(String lzPath);

    void insertTenant(TenantRecord tenant);

    /**
     * Retrieves the list of file to preload on the given server
     * The preload status for those records will also be marked as 'started'
     *
     * @param ingestionServer
     *            server to look on
     * @return a map of landing zone paths to the list of files to preload on them
     */
    Map<String, List<String>> getPreloadFiles(String ingestionServer);

    /**
     * Checks if the database for the given tenantId is ready for data.
     *
     * @param tenantId
     * @return <code>true</code> if tenant is marked as ready for data.
     */
    boolean tenantDbIsReady(String tenantId);

    /**
     * Flag that the database for the given tenantId is ready for data.
     *
     * @param tenantId
     */
    void setTenantReadyFlag(String tenantId);

    /**
     * Locks ingestion for this tenant until onboarding is complete.
     *
     * @param tenantId
     * @return whether the lock was acquired
     */
    boolean updateAndAquireOnboardingLock(String tenantId);

    /**
     * Remove tenant with invalid characters in the landing zone path from the tenant collection
     *
     * @param lzPath
     */
    void removeInvalidTenant(String lzPath);

     /*
     * @return a map of landing zone paths to the list of files to preload on them
     */
    Map<String, List<String>> getPreloadFiles();

    /**
     *
     * @return a list of all the tenant dbs
     */
    List<String> getAllTenantDbs();
}
