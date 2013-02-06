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

package org.slc.sli.ingestion.landingzone;

/**
 *
 * @author npandey
 *
 */
public enum AttributeType {
    PURGE("purge"),
    PURGE_KEEP_EDORGS("purge-keep-edorgs"),  // Keep edorg apps accessible to tenant.
    DRYRUN("dry-run"),

    /* Various modes for the duplicate detect/discard optimization (a.k.a. the recordHash
     * optimizaiton a.k.a. the delta-hash optimization):
     *     <unset>    Normal mode, hash data and compare with recordHash
     *
     *     reset      Like normal, but first purge recordHash of possibly stale data.
     *                This will have the performance characteristics of "Day 1"
     *                for the current job, and "Day N" for the next job.
     *
     *     disable    Purge the recordHash and also do not hash the incoming data
     *                nor insert it into recordHash, leaving recordHash empty.
     *                This will have the performance charactersitics of "Day 1"
     *                for the current job and the next job.
     *
     *     debugdrop  Treat all entities subject to recordHash based duplicate discarding as if
     *                they are in fact duplicates.  This will mimic a Day-N fully redundant ingestion,
     *                with the side effect that all recordHash processing is skipped.  This is in
     *                turn useful for measuring the performance impact of recordHash processing.
     */
    DUPLICATE_DETECTION("duplicate-detection");

    private String name;

    AttributeType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
