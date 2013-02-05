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

package org.slc.sli.ingestion.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * A RecordHash is calculated per entity, based on its fields in "neutral record" form,
 * early in the ingestion process -- prior to transformation -- to allow skipping of
 * processing records seen with the exact same content on the most recent upload.
 *
 * @author unavani
 *
 */
public class RecordHash {

    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(RecordHash.class);

    // These are the fields persisted in the MongoDB recordHash collection

    private String id;       // Deterministic ID = function(natural key) = a (mostly) stable ID
    private String hash;     // Record hash = SHA-1(neutral record attributes + tenant ID)
    private long created;    // Unix time stamp of creation, never updated.
    private long updated;    // Unix time stamp of update, absent for first version
    private int version;     // Number of times updated after create (== zero-origin version number),
                             // absent for first version

    @Indexed
    private String tenantId; // Tenant ID, for purge purposes, will be un-needed when record hash
                             // store is moved to tenant Db

    // Mode values for config property "duplicate-detection".  See AttributeType.java
    public static final String RECORD_HASH_MODE_RESET = "reset";
    public static final String RECORD_HASH_MODE_DISABLE = "disable";
    public static final String RECORD_HASH_MODE_DEBUG_DROP = "debugdrop";

    public RecordHash() {
        this.id = "";
        this.hash = "";
        this.created = 0;
        this.updated = 0;
        this.version = 0;
        this.tenantId = "";
    }

    /*
     * Conversions to/from compact key/value map, suitable for document-oriented databases,
     * including
     * those DBMSes that benefit from very short field names.
     *
     * Member Map key Always in Map? Default if absent
     * --------- ------- -------------- -----------------
     * id       id      Yes
     * hash     h       Yes
     * created  c       Yes
     * updated  u       No          <created>
     * version  v       No          0
     * tenantID t       No          ""
     */

    /**
     * Construct from compact, document-oriented database key/value map
     *
     * @param map
     *            A key/value map whose fields are suitable for a document-oriented database that
     *            has a need to keep fields short.
     *
     * @return The constructed object
     */
    public RecordHash(Map<String, Object> map) {
        this();

        if (null == map) {
            return;
        }

        id = binary2Hex((byte[]) map.get("_id")) + "_id";
        hash = binary2Hex((byte[]) map.get("h"));
        created = ((Long) map.get("c")).longValue();

        Long mapUpdated = (Long) map.get("u");
        if (null == mapUpdated) {
            updated = created;
        } else {
            updated = mapUpdated.longValue();
        }

        Integer mapVersion = (Integer) map.get("v");
        if (null == mapVersion) {
            version = 0;
        } else {
            version = mapVersion.shortValue();
        }

        String mapTenantId = (String) map.get("t");
        if (null == mapTenantId) {
            tenantId = "";
        } else {
            tenantId = mapTenantId;
        }
    }

    /**
     * @return A key/value map, suitable for use in a document-oriented database that
     *         prefers to have very small field names, omitting defaults for some fields
     */
    public Map<String, Object> toKVMap() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("_id", hex2Binary(id));
        m.put("h", hex2Binary(hash));
        m.put("c", Long.valueOf(created));
        if (updated != created) {
            m.put("u", Long.valueOf(updated));
        }
        if (version != 0) {
            m.put("v", Integer.valueOf(version));
        }
        if (null != tenantId && tenantId.length() > 0) {
            m.put("t", tenantId);
        }
        return m;
    }

    /**
     * Convert binary bytes to Hex string. E.g. 20-bytes SHA hash to 40-hex-char string
     *
     * @param bytes
     *            Binary bytes to be converted to hex
     *
     * @return A hex String object
     */
    public static String binary2Hex(byte[] bytes) {
        return new String(new Hex().encode(bytes));
    }

    /**
     * Convert DiD or hash to 20-byte binary form
     *
     * @param id
     *            String of 40 chars of hex, either a SHA hash or an ID suffixed with "_id"
     *
     * @return Binary bytes, or null if cannot be decoded
     */
    public static byte[] hex2Binary(String hexId) {
        // Take first 40 hex digits of DiD, lopping off the trailing "_id"
        try {
            return new Hex().decode(hexId.substring(0, 40).getBytes());
        } catch (DecoderException e) {
            LOG.warn("Cannot convert hex hash or ID to binary: '" + hexId + "'");
            LOG.warn(e.getMessage());
        }
        byte[] null_result = null;
        return null_result;
    }

    /**
     * Converts RecordHash object to a Map which can then be serialized to MongoDB.
     * @return A map containing the data members of the RecordHash that can then be serialized to MongoDB
     */
    public Map<String, Object> exportToSerializableMap() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("id",         id);
        m.put("hash",       hash);
        m.put("created",    created);
        m.put("updated",    updated);
        m.put("version",    version);
        m.put("tenantId",   tenantId);
        return m;
    }

    /**
     * Populates the data members of the RecordHash object using values from a Map.
     * Facilitates serialization/deserialization from MongoDB
     * @param m A map that will be used to populate the data members of the RecordHash
     */
    public void importFromSerializableMap(Map<String, Object> m) {
        this.id                  = (String)m.get("id");
        this.hash                = (String)m.get("hash");
        this.created             = (Long)m.get("created");
        this.updated             = (Long)m.get("updated");
        this.version             = (Integer)m.get("version");
        this.tenantId            = (String)m.get("tenantId");
    }

    /* Getters and setters
     *
     */
    public String getId() {
        return id;
    }

    public void setId(String newId) {
        id = newId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String newHash) {
        hash = newHash;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long newCreated) {
        created = newCreated;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long newUpdated) {
        updated = newUpdated;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int newVersion) {
        version = newVersion;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String newTenantId) {
        tenantId = newTenantId;
    }

}
