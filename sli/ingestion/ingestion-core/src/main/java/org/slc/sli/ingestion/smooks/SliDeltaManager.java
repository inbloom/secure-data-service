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

package org.slc.sli.ingestion.smooks;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.model.RecordHash;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;
import org.slc.sli.validation.NaturalKeyValidationException;
import org.slc.sli.validation.NoNaturalKeysDefinedException;

/**
 * @author unavani
 *
 *         Static class implementation of SliDeltaManager
 *
 */
public final class SliDeltaManager {

    public static final String RECORDHASH_DATA = "rhData";
    public static final String RECORDHASH_HASH = "rhHash";
    public static final String RECORDHASH_ID = "rhId";
    public static final String RECORDHASH_CURRENT = "rhCurrentHash";

    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(SliDeltaManager.class);

    private SliDeltaManager() {
    }

    public static final String NRKEYVALUEFIELDNAMES = "neutralRecordKeyValueFieldNames";
    public static final String OPTIONALNRKEYVALUEFIELDNAMES = "optionalNeutralRecordKeyValueFieldNames";

    /**
     * Calculate record ID and values hash for NeutralRecord and save in metadata.
     * Return true iff data is unchanged (no updated needed)
     *
     * @param n
     * @param batchJobDAO
     * @param dIdStrategy
     * @return
     */
    public static boolean isPreviouslyIngested(NeutralRecord n, BatchJobDAO batchJobDAO,
            DeterministicUUIDGeneratorStrategy dIdStrategy, DeterministicIdResolver didResolver,
            AbstractMessageReport report, ReportStats reportStats) {
        boolean isPrevIngested = false;
        String tenantId = TenantContext.getTenantId();

        // Align Ed-Fi and SLI schema recordType
        String sliEntityType = MapUtils.getString(n.getMetaData(), "sliEntityType");
        if (sliEntityType == null) {
            // If an explicit sliEntityName isn't provided via smooks config, use the
            // neutralRecord recordType
            sliEntityType = n.getRecordType();
        }

        // HACK HACK HACK HACK ... and needs more work
        // Determine the neutralrecord key fields from smooks config
        // TODOs
        // 1. DE2260 Do deterministic id processing here directly on the neutralrecord,
        // however after attempting this is was determined there are non-trivial dependencies
        // in the existing ingestion pipeline in transformation and quite possibly other areas
        // that still depend on non-dId resolved reference structures.
        // 2. DE2261 The natural key fields should be mapped deterministically from the
        // "source of truth" rather than smooks-all-xml.
        // This is problematic right now since the current "source of truth" is the SLI schema which
        // does not match up
        // to neutral record fields here which are based on SLI-Ed-Fi.
        Map<String, String> naturalKeys = new HashMap<String, String>();

        NeutralRecord neutralRecordResolved = null;

        neutralRecordResolved = (NeutralRecord) n.clone();
        NeutralRecordEntity entity = new NeutralRecordEntity(neutralRecordResolved);
        didResolver.resolveInternalIds(entity, neutralRecordResolved.getSourceId(), report, reportStats);

        // Calculate DiD using natural key values (that are references) in their Did form
        try {
            populateNaturalKeys(neutralRecordResolved, naturalKeys);
            NaturalKeyDescriptor nkd = new NaturalKeyDescriptor(naturalKeys, tenantId, sliEntityType, null);

            String recordId = dIdStrategy.generateId(nkd);

            // Calculate record hash using natural keys' values
            String recordHashValues = DigestUtils.shaHex(neutralRecordResolved.getRecordType() + "-"
                    + neutralRecordResolved.getAttributes().toString() + "-" + tenantId);
            RecordHash record = batchJobDAO.findRecordHash(tenantId, recordId);

            // TODO consider making this a util
            List<Map<String, Object>> rhData = new ArrayList<Map<String, Object>>();
            Map<String, Object> rhDataElement = new HashMap<String, Object>();
            rhDataElement.put(RECORDHASH_ID, recordId);
            rhDataElement.put(RECORDHASH_HASH, recordHashValues);
            rhData.add(rhDataElement);

            n.addMetaData(RECORDHASH_DATA, rhData);

            isPrevIngested = (record != null && record.getHash().equals(recordHashValues));

            if(record != null) {
                //not ingested previously
                rhDataElement.put(RECORDHASH_CURRENT, record.exportToSerializableMap());
            }

        } catch (NoNaturalKeysDefinedException e) {
            // If we can't determine the natural keys, don't include it in recordHash processing
            // Errors will be logged by normal (non-recordHash) processing
            LOG.warn(e.getMessage());
            isPrevIngested = false;
        } catch (NaturalKeyValidationException e) {
            // If we can't determine the natural key values, don't include it in recordHash
            // processing
            // Errors will be logged by normal (non-recordHash) processing
            LOG.warn(e.getMessage());
            isPrevIngested = false;
        }

        return isPrevIngested;
    }

    private static void populateNaturalKeys(NeutralRecord n, Map<String, String> naturalKeys)
            throws NoNaturalKeysDefinedException {
        addFieldsToNaturalKeysImpl(n, naturalKeys, MapUtils.getString(n.getMetaData(), NRKEYVALUEFIELDNAMES), false);
        addFieldsToNaturalKeysImpl(n, naturalKeys, MapUtils.getString(n.getMetaData(), OPTIONALNRKEYVALUEFIELDNAMES),
                true);
    }

    private static void addFieldsToNaturalKeysImpl(NeutralRecord n, Map<String, String> naturalKeys, String fieldNames,
            boolean optional) throws NoNaturalKeysDefinedException {

        String recordType = n.getRecordType();

        // TODO: this needs cleanup
        if (fieldNames == null) {
            if (optional) {
                return;
            } else {
                throw new NoNaturalKeysDefinedException("A mapping for \"" + NRKEYVALUEFIELDNAMES
                        + "\" in smooks-all-xml needs to be added for \"" + recordType + "\"");
            }
        }
        StringTokenizer fieldNameTokenizer = new StringTokenizer(fieldNames, ",");
        while (fieldNameTokenizer.hasMoreElements()) {
            String fieldName = (String) fieldNameTokenizer.nextElement();
            // TODO: Use NaturalKeyExtractor or an impl of the interface once we annotate
            // SLC-Ed-Fi.xml
            Object value = null;
            try {
                value = PropertyUtils.getProperty(n.getAttributes(), fieldName);
            } catch (IllegalAccessException e) {
                handleFieldAccessException(fieldName, n, optional);
            } catch (InvocationTargetException e) {
                handleFieldAccessException(fieldName, n, optional);
            } catch (NoSuchMethodException e) {
                handleFieldAccessException(fieldName, n, optional);
            }
            String strValue = "";
            if (value != null) {
                strValue = value.toString();
            } else {
                handleFieldAccessException(fieldName, n, optional);
            }
            naturalKeys.put(fieldName, strValue);
        }
    }

    private static void handleFieldAccessException(String fieldName, NeutralRecord n, boolean optional) {
        if (!optional) {
            String message = "The \"" + n.getRecordType() + "\" entity at location " + n.getLocationInSourceFile()
                    + " in file \"" + n.getSourceFile() + "\" is missing a value for required natural key field \""
                    + fieldName + "\" as specified in \"" + NRKEYVALUEFIELDNAMES + "\" in smooks-all-xml.";

            throw new NaturalKeyValidationException(message);
        }
    }
}
