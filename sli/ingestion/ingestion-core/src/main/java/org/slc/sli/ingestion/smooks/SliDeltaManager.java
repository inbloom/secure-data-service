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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import org.apache.commons.codec.digest.DigestUtils;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.model.RecordHash;
import org.slc.sli.ingestion.model.da.BatchJobDAO;

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
    public static boolean isPreviouslyIngested(NeutralRecord n, NeutralRecord withDids, BatchJobDAO batchJobDAO, DeterministicUUIDGeneratorStrategy dIdStrategy) {

    	// Calculate DiD using natural key values (that are references) in their Did form
    	String recordId = null;
    	if ( withDids == null )
    		recordId = n.generateRecordId(dIdStrategy);
    	else
    		recordId = withDids.generateRecordId(dIdStrategy);
        String tenantId = TenantContext.getTenantId();
        // Calculate record hash using natural keys' values
        String recordHashValues = DigestUtils.shaHex(n.getRecordType() + "-" + n.getAttributes().toString() + "-" + tenantId);
        RecordHash record = batchJobDAO.findRecordHash(tenantId, recordId);

        n.addMetaData("rhId", recordId);
        n.addMetaData("rhHash", recordHashValues);
        n.addMetaData("rhTenantId", tenantId);
        
        return (record != null && record.hash.equals(recordHashValues));
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
