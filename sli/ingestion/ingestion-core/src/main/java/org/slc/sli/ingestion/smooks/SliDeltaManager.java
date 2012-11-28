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

package org.slc.sli.ingestion.smooks;

import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.model.RecordHash;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * @author unavani
 *
 * Static class implementation of SliDeltaManager
 *
 */
public final class SliDeltaManager {

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
            DeterministicUUIDGeneratorStrategy dIdStrategy, DeterministicIdResolver didResolver, ErrorReport errorReport) {

        String tenantId = TenantContext.getTenantId();

        // US4439 TODO: This is POC code and needs to be cleaned up and put where it makes most
        // sense

        // Align Ed-Fi and SLI schema recordType
        String sliEntityType = MapUtils.getString(n.getMetaData(), "sliEntityType");
        if (sliEntityType == null) {
            // If an explicit sliEntityName isn't provided via smooks config, use the
            // neutralRecord recordType
            sliEntityType = n.getRecordType();
        }

        // Determine the neutralrecord key fields from smooks config - temporary solution until
        // annotations are added identifying key fields in SLI-Ed-Fi.xsd
        // that can be mapped deterministically to the neutralrecord fields
        Map<String, String> naturalKeys = new HashMap<String, String>();

        populateNaturalKeys(n, naturalKeys);

        NaturalKeyDescriptor nkd = new NaturalKeyDescriptor(naturalKeys, tenantId, sliEntityType, null);

        // HACK HACK HACK HACK ... and needs more work
        // didResolver has problems resolving StudentReference in "attendance" currently,
        // but it is not needed for DID calculation so we skip it

        // Calculate DiD using natural key values (that are references) in their Did form
        String recordId = null;
        Object recordIdObj = n.getMetaDataByName("rhId");
        if ( null == recordIdObj )
        	recordId = dIdStrategy.generateId(nkd);
        else
        	recordId = recordIdObj.toString();
        
        NeutralRecord neutralRecordResolved = null;
        if (! "attendance".equals(n.getRecordType())) {
            neutralRecordResolved = (NeutralRecord) n.clone();
            Entity entity = new NeutralRecordEntity(neutralRecordResolved);
            didResolver.resolveInternalIds(entity, neutralRecordResolved.getSourceId(), errorReport);
        }

        // Calculate record hash using natural keys' values
        String recordHashValues = DigestUtils.shaHex(n.getRecordType() + "-" + n.getAttributes().toString() + "-" + tenantId);
        RecordHash record = batchJobDAO.findRecordHash(tenantId, recordId);

        n.addMetaData("rhId", recordId);
        n.addMetaData("rhHash", recordHashValues);
        n.addMetaData("rhTenantId", tenantId);

        System.out.println("Generated DID " + recordId);

        // US4439 TODO end

        return (record != null && record.hash.equals(recordHashValues));
    }


    private static void populateNaturalKeys(NeutralRecord n, Map<String, String> naturalKeys) {
        addFieldsToNaturalKeysImpl(n, naturalKeys, MapUtils.getString(n.getMetaData(), "neutralRecordKeyValueFieldNames"), false);
        addFieldsToNaturalKeysImpl(n, naturalKeys, MapUtils.getString(n.getMetaData(), "optionalNeutralRecordKeyValueFieldNames"), true);
    }

    private static void addFieldsToNaturalKeysImpl(NeutralRecord n, Map<String, String> naturalKeys, String fieldNames, boolean optional) {

        String recordType = n.getRecordType();

        //TODO: this needs cleanup
        if (fieldNames == null) {
            if (optional) {
                return;
            } else {
                System.out.println("A mapping for \"neutralRecordKeyValueFieldNames\" in smooks-all-xml needs to be added for \"" + recordType + "\"");
                throw new RuntimeException("A mapping for \"neutralRecordKeyValueFieldNames\" in smooks-all-xml needs to be added for \"" + recordType + "\"");
            }
        }
        StringTokenizer fieldNameTokenizer = new StringTokenizer(fieldNames, ",");
        while (fieldNameTokenizer.hasMoreElements()) {
            String fieldName = (String) fieldNameTokenizer.nextElement();
            // TODO: Use NaturalKeyExtractor or an impl of the interface once we annotate SLC-Ed-Fi.xml
//                String strValue = MapUtils.getString(attributes, fieldName);
            Object value = null;
            try {
                value = PropertyUtils.getProperty(n.getAttributes(), fieldName);
            } catch (IllegalAccessException e) {
                handleFieldAccessException(fieldName, recordType, optional);
            } catch (InvocationTargetException e) {
                handleFieldAccessException(fieldName, recordType, optional);
            } catch (NoSuchMethodException e) {
                handleFieldAccessException(fieldName, recordType, optional);
            }
            String strValue = "";
            if (value != null) {
                strValue = value.toString();
            }
            naturalKeys.put(fieldName, strValue);
        }
    }

    private static void handleFieldAccessException(String fieldName, String recordType, boolean optional) {
        if (!optional) {
            System.out.println("Field name \"" + fieldName + "\" specified in \"neutralRecordKeyValueFieldNames\" in smooks-all-xml for \"" + recordType + "\" is wrong or not mapped properly.");
            throw new RuntimeException("Field name \"" + fieldName + "\" specified in \"neutralRecordKeyValueFieldNames\" in smooks-all-xml for \"" + recordType + "\" is wrong or not mapped properly.");
        }
    }

    public static String createRecordHash(byte[] input, String algorithmName) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithmName);
        return byteArray2Hex(md.digest(input));
    }

    private static String byteArray2Hex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    public static RecordHash createRecordHash(String tenantId, String recordId, String hashValues) {
        RecordHash rh = new RecordHash();
        rh._id = recordId;
        rh.hash = hashValues;
        rh.tenantId = tenantId;
        rh.created = "" + System.currentTimeMillis();
        rh.updated = rh.created;
        return rh;
    }

}
