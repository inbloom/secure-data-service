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

package org.slc.sli.sif.slcinterface;

/**
 * Mapping between SIF id and SLI ids. File implementation
 */
public interface SifIdResolver {
    
    /**
     * Given a sif reference id, returns the sli guid of the corresponding entity
     * 
     * @param sifId
     * @return The SLI guid if the entity exists in SLI, null otherwise
     */
    public String getSLIGuid(String sifId);

}
