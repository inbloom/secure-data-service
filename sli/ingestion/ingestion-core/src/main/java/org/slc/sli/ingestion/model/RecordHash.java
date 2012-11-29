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

package org.slc.sli.ingestion.model;

import org.springframework.data.mongodb.core.index.Indexed;


/**
 *
 * @author unavani
 *
 * A RecordHash is calculated per entity, based on its fields in "neutral record" form,
 * early in the ingestion process -- prior to transformation -- to allow skipping of
 * processing records seen with the exact same content on the most recent upload.
 *
 */
public class RecordHash {

	// These are the fields persisted in the MongoDB recordHash collection
	
    public String _id;			// Deterministic ID = function(natural key) = a (mostly) stable ID
    public String hash;			// Record hash = SHA-1(neutral record attributes + tenant ID)
    public String created;		// Unix time stamp of creation, never updated.
    public String updated;		// Unix time stamp of update, absent for first version
    public int version;			// Number of times updated after create (== zero-origin version number), absent for first version
    
    @Indexed
    public String tenantId;		// Tenant ID, for purge purposes, will be un-needed when record hash store is moved to tenant Db

    public RecordHash() {
        this._id = "";
        this.hash = "";
        this.created = "";
        this.updated = "";
        this.version = 0;
        this.tenantId = "";
    }
}
