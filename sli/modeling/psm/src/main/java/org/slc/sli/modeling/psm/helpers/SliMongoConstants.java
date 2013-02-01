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

package org.slc.sli.modeling.psm.helpers;

import javax.xml.namespace.QName;

/**
 * Constants used in the SLI MongoDB W3C XML Schema.
 */
public final class SliMongoConstants {

    private SliMongoConstants() {

    }

    public static final String NAMESPACE_SLI = "http://slc-sli/ed-org/0.1";

    public static final QName SLI_COLLECTION_NAME = new QName(NAMESPACE_SLI, "CollectionType");
    public static final QName SLI_NATURAL_KEY = new QName(NAMESPACE_SLI, "naturalKey");
    public static final QName SLI_APPLY_NATURAL_KEYS = new QName(NAMESPACE_SLI, "applyNaturalKeys");
    public static final QName SLI_SELF_REFERENCE = new QName(NAMESPACE_SLI, "SelfReference");
    public static final QName SLI_PII = new QName(NAMESPACE_SLI, "PersonallyIdentifiableInfo");
    public static final QName SLI_READ_ENFORCEMENT = new QName(NAMESPACE_SLI, "ReadEnforcement");
    public static final QName SLI_REFERENCE_TYPE = new QName(NAMESPACE_SLI, "ReferenceType");
    public static final QName SLI_SECURITY_SPHERE = new QName(NAMESPACE_SLI, "SecuritySphere");
    public static final QName SLI_WRITE_ENFORCEMENT = new QName(NAMESPACE_SLI, "WriteEnforcement");
    public static final QName SLI_RELAXEDBLACKLIST = new QName(NAMESPACE_SLI, "RelaxedBlacklist");
    public static final QName SLI_RESTRICTED_FOR_LOGGING = new QName(NAMESPACE_SLI, "RestrictedForLogging");
    public static final QName SLI_SCHEMA_VERSION = new QName(NAMESPACE_SLI, "schemaVersion");
    public static final QName SLI_ASSOCIATION_KEY = new QName(NAMESPACE_SLI, "associationKey");
    public static final QName SLI_BEGIN_DATE = new QName(NAMESPACE_SLI, "beginDate");
    public static final QName SLI_END_DATE = new QName(NAMESPACE_SLI, "endDate");
    public static final QName SLI_ASSOCIATED_DATED_COLLECTION= new QName(NAMESPACE_SLI, "associatedDatedCollection");
    public static final QName SLI_FILTER_BEGIN_DATE_ON = new QName(NAMESPACE_SLI, "filterBeginDateOn");
    public static final QName SLI_FILTER_END_DATE_ON = new QName(NAMESPACE_SLI, "filterEndDateOn");
}
