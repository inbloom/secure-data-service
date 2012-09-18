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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.model.da.BatchJobDAO;

/**
 * @author unavani
 *
 * Singleton implementation of SliDeltaManager
 *
 */
public class SliDeltaManager {

    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(SmooksEdFiVisitor.class);

    private static SliDeltaManager instance = null;

    private SliDeltaManager() {
          // Exists only to defeat instantiation.
    }

    public static SliDeltaManager getInstance() {
        if (instance == null) {
            instance = new SliDeltaManager();
        }
        return instance;
    }


    public boolean isPreviouslyIngested(NeutralRecord n, BatchJobDAO batchJobDAO) {

        try {
            String recordId = createRecordHash((n.getRecordType() + "-" + n.getAttributes().toString()).getBytes("utf8"), "SHA-1")
                    + "-" + createRecordHash((n.getRecordType() + "-" + n.getAttributes().toString()).getBytes("utf8"), "MD5");

            LOG.info("RECORD HASH = " + recordId);

            return batchJobDAO.findAndUpsertRecordHash(recordId);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return false;
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
}
