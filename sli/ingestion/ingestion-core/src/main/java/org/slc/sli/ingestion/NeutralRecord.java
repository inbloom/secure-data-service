/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.ingestion;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Container format to store any type of Ingestion data generically.
 *
 */
public class NeutralRecord {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * stores an Id value uniquely identifying the record within the data store.
     */
    protected String recordId;

    /**
     * stores an Id value correlating the record to a certain external source,
     * such as a particular LEA (or possibly a specific source system within
     * that LEA).
     */
    protected String sourceId;

    /**
     * stores an Id value correlating the record to a particular batch or
     * processing request.
     */
    protected String batchJobId;

    /**
     * stores a flag whether the current record is association.
     */
    protected boolean association;

    /**
     * stores the Id value uniquely identifying this record in the scope of the
     * source system. The Id is assumed to be permanent and unique the object,
     * statewide.
     */
    protected Object localId;

    /**
     * stores a name that identifies the type of object represented by this
     * record (Student, School, etc).
     */
    protected String recordType;

    /**
     * stores a mapping that captures references to other records, in the form
     * {recordType:localId}.
     */
    protected Map<String, Object> localParentIds;

    /**
     * stores a mapping that captures all the attributes associated with the
     * record (aside from localId and any id references).
     */
    protected Map<String, Object> attributes;

    /**
     * stores the value of a hashing algorithm that can be used to quickly
     * compare two records' attributes for equality, and/or act as a surrogate
     * to the complete contents of a record.
     */
    protected String attributesCrc;

    /**
     * The name of the file where this neutral record originated
     */
    private String sourceFile;

    /**
     * The location in source file of the record
     */
    private int locationInSourceFile;

    /**
     * Default constructor
     */
    public NeutralRecord() {
        // initialize the two Maps as a convenience
        this.localParentIds = new HashMap<String, Object>();
        this.attributes = new HashMap<String, Object>();

        this.recordId = null;
    }

    /**
     * @return the recordId
     */
    public String getRecordId() {
        return recordId;
    }

    /**
     * @return the localId
     */
    public Object getLocalId() {
        return localId;
    }

    /**
     * @param recordId
     *            the recordId to set
     */
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    /**
     * @param localId
     *            the localId to set
     */
    public void setLocalId(Object localId) {
        this.localId = localId;
    }

    /**
     * @return the sourceId
     */
    public String getSourceId() {
        return sourceId;
    }

    /**
     * @param sourceId
     *            the sourceId to set
     */
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * @return the jobId
     */
    public String getBatchJobId() {
        return batchJobId;
    }

    /**
     * @param batchJobId
     *            the jobId to set
     */
    public void setBatchJobId(String batchJobId) {
        this.batchJobId = batchJobId;
    }

    /**
     * @return the association
     */
    public boolean isAssociation() {
        return association;
    }

    /**
     * @param association
     *            the association to set
     */
    public void setAssociation(boolean association) {
        this.association = association;
    }

    /**
     * @return the localParentIds
     */
    public Map<String, Object> getLocalParentIds() {
        return localParentIds;
    }

    /**
     * @param localParentIds
     *            the localParentIds to set
     */
    public void setLocalParentIds(Map<String, Object> localParentIds) {
        this.localParentIds = localParentIds;
    }

    /**
     * @return the recordType
     */
    public String getRecordType() {
        return recordType;
    }

    /**
     * @param recordType
     *            the recordType to set
     */
    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    /**
     * @return the attributes
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes
     *            the attributes to set
     */
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     */
    public void setAttributeField(String fieldName, Object fieldValue) {
        this.attributes.put(fieldName, fieldValue);
    }

    /**
     * @return the attributesCrc
     */
    public String getAttributesCrc() {
        return attributesCrc;
    }

    /**
     * @param attributesCrc
     *            the attributesCrc to set
     */
    public void setAttributesCrc(String attributesCrc) {
        this.attributesCrc = attributesCrc;
    }

    /**
     * Get the name of the source file
     *
     * @return the name of the source file
     */
    public String getSourceFile() {
        return sourceFile;
    }

    /**
     * Set the name of the source file
     *
     * @param sourceFile
     *            the name of the source file
     */
    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public int getLocationInSourceFile() {
        return locationInSourceFile;
    }

    public void setLocationInSourceFile(int locationInSourceFile) {
        this.locationInSourceFile = locationInSourceFile;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    /**
     * compare neutral records for equality initial version compares JSON
     * representations
     *
     * TODO - replace with version incorporating CRC as basis for comparison
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        return (this.toString().equals(obj.toString()));
    }

    /**
     * as a convenience, return pure-json representation for strings by default
     */
    @Override
    public String toString() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (Exception e) {
            return super.toString();
        }
    }

}
