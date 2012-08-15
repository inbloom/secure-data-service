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
package org.slc.sli.aggregation.mapreduce.map.key;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

/**
 * EmittableKey for holding both a tenant identifier and an object id
 * 
 */
public class TenantAndIdEmittableKey extends EmittableKey {
    
    private static final int TENANT_FIELD = 0;
    private static final int ID_FIELD = 1;
    
    public TenantAndIdEmittableKey() {
        this("metaData.tenantId", "_id");
    }
    
    public TenantAndIdEmittableKey(final String tenantIdFieldName, final String idFieldName) {
        String[] fieldNames = { tenantIdFieldName, idFieldName };
        setFieldNames(fieldNames);
    }
    
    public Text getTenantId() {
        return (Text) get(fieldNames[TENANT_FIELD]);
    }
    
    public void setTenantId(final Text id) {
        put(fieldNames[TENANT_FIELD], id);
    }
    
    public Text getTenantIdField() {
        return fieldNames[TENANT_FIELD];
    }
    
    public Text getId() {
        return (Text) get(fieldNames[ID_FIELD]);
    }
    
    public void setId(final Text id) {
        put(fieldNames[ID_FIELD], id);
    }
    
    public Text getIdField() {
        return fieldNames[ID_FIELD];
    }
    
    @Override
    public void readFields(DataInput data) throws IOException {
        setTenantId(new Text(data.readLine()));
        setId(new Text(data.readLine()));
    }
    
    @Override
    public void write(DataOutput data) throws IOException {
        data.writeBytes(getTenantId().toString());
        data.writeBytes(getId().toString());
    }
    
    @Override
    public String toString() {
        return "TenantAndIdEmittableKey [" + getIdField() + "=" + getId() + ", " + getTenantIdField() + "="
                + getTenantId() + "]";
    }
    
    @Override
    public BSONObject toBSON() {
        BSONObject bson = new BasicBSONObject();
        bson.put(getTenantIdField().toString(), getTenantId().toString());
        bson.put(getIdField().toString(), getId().toString());
        return bson;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = getId() == null ? "".hashCode() : getId().hashCode();
        result = prime * result + (getTenantId() == null? "".hashCode() : getTenantId().hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TenantAndIdEmittableKey other = (TenantAndIdEmittableKey) obj;
        if (other.getId() == null) {
            return getId() == null;
        }
        if (!other.getId().equals(getId())) {
            return false;
        }
        if (other.getTenantId() == null) {
            return getTenantId() == null;
        }
        if (!other.getTenantId().equals(getTenantId())) {
            return false;
        }
        return true;
    }
    
    @Override
    public int compareTo(EmittableKey other) {
        if (other instanceof TenantAndIdEmittableKey) {
            TenantAndIdEmittableKey obj = (TenantAndIdEmittableKey) other;
            return this.getId().compareTo(obj.getId());
        } else {
            return -1;
        }
    }
}
