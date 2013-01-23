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

package org.slc.sli.modeling.psm.helpers;

/**
 * Constants used in the SLI UML Model.
 */
public final class SliUmlConstants {
    /**
     * Prefix avoids collisions with other tag definitions.
     */
    private static final String TAGDEF_PREFIX = "dataStore.";
    
    public static final String TAGDEF_COLLECTION_NAME = TAGDEF_PREFIX.concat("collectionName");
    public static final String TAGDEF_NATURAL_KEY = TAGDEF_PREFIX.concat("naturalKey");
    public static final String TAGDEF_APPLY_NATURAL_KEYS = TAGDEF_PREFIX.concat("applyNaturalKeys");
    public static final String TAGDEF_SELF_REFERENCE = TAGDEF_PREFIX.concat("selfReference");
    public static final String TAGDEF_PII = TAGDEF_PREFIX.concat("pii");
    public static final String TAGDEF_ENFORCE_READ = TAGDEF_PREFIX.concat("enforceRead");
    public static final String TAGDEF_ENFORCE_WRITE = TAGDEF_PREFIX.concat("enforceWrite");
    public static final String TAGDEF_REFERENCE = TAGDEF_PREFIX.concat("reference");
    public static final String TAGDEF_RELAXED_BLACKLIST = TAGDEF_PREFIX.concat("relaxedBlacklist");
    public static final String TAGDEF_REST_RESOURCE = TAGDEF_PREFIX.concat("resource");
    public static final String TAGDEF_RESTRICTED_FOR_LOGGING = TAGDEF_PREFIX.concat("restrictedForLogging");
    public static final String TAGDEF_SECURITY_SPHERE = TAGDEF_PREFIX.concat("securitySphere");
    public static final String TAGDEF_ASSOCIATION_KEY = TAGDEF_PREFIX.concat("associationKey");
    public static final String TAGDEF_BEGIN_DATE = TAGDEF_PREFIX.concat("beginDate");
    public static final String TAGDEF_END_DATE = TAGDEF_PREFIX.concat("endDate");
    public static final String TAGDEF_ASSOCIATED_DATED_COLLECTION = TAGDEF_PREFIX.concat("associatedDatedCollection");
    public static final String TAGDEF_FILTER_BEGIN_DATE_ON = TAGDEF_PREFIX.concat("filterBeginDateOn");
    public static final String TAGDEF_FILTER_END_DATE_ON = TAGDEF_PREFIX.concat("filterEndDateOn");
}
