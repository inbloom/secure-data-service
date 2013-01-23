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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slc.sli.ingestion.reporting.ElementSource;

/**
 * Container format to store any type of Ingestion data generically.
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NeutralRecord implements Cloneable, Resource, ElementSource {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * stores an Id value uniquely identifying the record within the data store.
     */
    private String recordId;

    /**
     * stores an Id value correlating the record to a certain external source,
     * such as a particular LEA (or possibly a specific source system within
     * that LEA).
     */
    private String sourceId;

    /**
     * stores an Id value correlating the record to a particular batch or
     * processing request.
     */
    private String batchJobId;

    /**
     * stores a flag whether the current record is association.
     */
    private boolean association;

    /**
     * stores the Id value uniquely identifying this record in the scope of the
     * source system. The Id is assumed to be permanent and unique the object,
     * statewide.
     */
    private Object localId;

    /**
     * stores a name that identifies the type of object represented by this
     * record (Student, School, etc).
     */
    private String recordType;

    /**
     * stores a mapping that captures references to other records, in the form
     * {recordType:localId}.
     */
    private Map<String, Object> localParentIds;

    /**
     * stores a mapping that captures all the attributes associated with the
     * record (aside from localId and any id references).
     */
    private Map<String, Object> attributes;

    /**
     * stores the value of a hashing algorithm that can be used to quickly
     * compare two records' attributes for equality, and/or act as a surrogate
     * to the complete contents of a record.
     */
    private String attributesCrc;

    /**
     * The name of the file where this neutral record originated
     */
    private String sourceFile;

    /**
     * The location in source file of the record
     */
    private int locationInSourceFile;

    /**
     * The line number in source file of the record when the element is processed in visitBefore()
     */
    private int visitBeforeLineNumber;
    /**
     * The column number in source file of the record when the element is processed in visitBefore()
     */
    private int visitBeforeColumnNumber;
    /**
     * The line number in source file of the record when the element is processed in visitAfter()
     */
    private int visitAfterLineNumber;
    /**
     * The column number in source file of the record when the element is processed in visitAfter()
     */
    private int visitAfterColumnNumber;

    /**
     * Time when neutral record was created (in ms).
     */
    private long creationTime;

    /**
     * stores a mapping that captures contextual information regarding the record.
     */
    private Map<String, Object> metaData;

    /**
     * Default constructor
     */
    public NeutralRecord() {
        this.localParentIds = new HashMap<String, Object>();
        this.attributes = new HashMap<String, Object>();
        this.metaData = new HashMap<String, Object>();
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
    public String getElementType() {
        return recordType;
    }

    @Override
    public int getVisitBeforeLineNumber() {
        return visitBeforeLineNumber;
    }

    public void setVisitBeforeLineNumber(int visitBeforeLineNumber) {
        this.visitBeforeLineNumber = visitBeforeLineNumber;
    }

    @Override
    public int getVisitBeforeColumnNumber() {
        return visitBeforeColumnNumber;
    }

    public void setVisitBeforeColumnNumber(int visitBeforeColumnNumber) {
        this.visitBeforeColumnNumber = visitBeforeColumnNumber;
    }

    public int getVisitAfterLineNumber() {
        return visitAfterLineNumber;
    }

    public void setVisitAfterLineNumber(int visitAfterLineNumber) {
        this.visitAfterLineNumber = visitAfterLineNumber;
    }

    public int getVisitAfterColumnNumber() {
        return visitAfterColumnNumber;
    }

    public void setVisitAfterColumnNumber(int visitAfterColumnNumber) {
        this.visitAfterColumnNumber = visitAfterColumnNumber;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, Object> metaData) {
        this.metaData = metaData;
    }

    public void addMetaData(String name, Object value) {
        this.metaData.put(name, value);
    }

    public Object getMetaDataByName(String name) {
        if (this.metaData.containsKey(name)) {
            return this.metaData.get(name);
        }
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (association ? 1231 : 1237);
        result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
        result = prime * result + ((attributesCrc == null) ? 0 : attributesCrc.hashCode());
        result = prime * result + ((batchJobId == null) ? 0 : batchJobId.hashCode());
        result = prime * result + (int) (creationTime ^ (creationTime >>> 32));
        result = prime * result + ((localId == null) ? 0 : localId.hashCode());
        result = prime * result + ((localParentIds == null) ? 0 : localParentIds.hashCode());
        result = prime * result + locationInSourceFile;
        result = prime * result + ((metaData == null) ? 0 : metaData.hashCode());
        result = prime * result + ((recordId == null) ? 0 : recordId.hashCode());
        result = prime * result + ((recordType == null) ? 0 : recordType.hashCode());
        result = prime * result + ((sourceFile == null) ? 0 : sourceFile.hashCode());
        result = prime * result + ((sourceId == null) ? 0 : sourceId.hashCode());
        return result;
    }

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
        NeutralRecord other = (NeutralRecord) obj;
        if (association != other.association) {
            return false;
        }
        if (attributes == null) {
            if (other.attributes != null) {
                return false;
            }
        } else if (!attributes.equals(other.attributes)) {
            return false;
        }
        if (attributesCrc == null) {
            if (other.attributesCrc != null) {
                return false;
            }
        } else if (!attributesCrc.equals(other.attributesCrc)) {
            return false;
        }
        if (batchJobId == null) {
            if (other.batchJobId != null) {
                return false;
            }
        } else if (!batchJobId.equals(other.batchJobId)) {
            return false;
        }
        if (creationTime != other.creationTime) {
            return false;
        }
        if (localId == null) {
            if (other.localId != null) {
                return false;
            }
        } else if (!localId.equals(other.localId)) {
            return false;
        }
        if (localParentIds == null) {
            if (other.localParentIds != null) {
                return false;
            }
        } else if (!localParentIds.equals(other.localParentIds)) {
            return false;
        }
        if (locationInSourceFile != other.locationInSourceFile) {
            return false;
        }
        if (metaData == null) {
            if (other.metaData != null) {
                return false;
            }
        } else if (!metaData.equals(other.metaData)) {
            return false;
        }
        if (recordId == null) {
            if (other.recordId != null) {
                return false;
            }
        } else if (!recordId.equals(other.recordId)) {
            return false;
        }
        if (recordType == null) {
            if (other.recordType != null) {
                return false;
            }
        } else if (!recordType.equals(other.recordType)) {
            return false;
        }
        if (sourceFile == null) {
            if (other.sourceFile != null) {
                return false;
            }
        } else if (!sourceFile.equals(other.sourceFile)) {
            return false;
        }
        if (sourceId == null) {
            if (other.sourceId != null) {
                return false;
            }
        } else if (!sourceId.equals(other.sourceId)) {
            return false;
        }
        return true;
    }

    /**
     * as a convenience, return pure-json representation for strings by default
     */
    @Override
    public String toString() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (java.io.IOException e) {
            return super.toString();
        }
    }

    /*
     * Clone, e.g. for the DiD calculation needs
     *
     * @return The cloned object, taking care to handle composite members such as maps.
     */
    @Override
    public Object clone() {
        NeutralRecord result = null;
        try {
            result = (NeutralRecord) super.clone();
            result.localParentIds = (HashMap<String, Object>) ((HashMap<String, Object>) this.localParentIds).clone();
            result.attributes = (HashMap<String, Object>) ((HashMap<String, Object>) this.attributes).clone();
            result.metaData = (HashMap<String, Object>) ((HashMap<String, Object>) this.metaData).clone();
        } catch (CloneNotSupportedException e) {
            result = null;
        }
        return result;
    }

    @Override
    public String getResourceId() {
        return getSourceFile();
    }
}
