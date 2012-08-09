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
import java.util.LinkedHashSet;

import org.apache.hadoop.io.Text;
import org.bson.BSONObject;

/**
 * EmittableKey for holding both a tenant identifier and an object id
 *
 */
public class TenantAndIdEmittableKey extends IdFieldEmittableKey {

    protected static final String TENANT_ID_FIELD = "metaData.tenantId";

    protected Text tenantFieldName = null;

    public TenantAndIdEmittableKey() {
        this(IdFieldEmittableKey.DEFAULT_ID_FIELD, TENANT_ID_FIELD);
    }

    public TenantAndIdEmittableKey(final String tenantIdFieldName, final String idFieldName) {
        super(idFieldName);
        tenantFieldName = new Text(tenantIdFieldName);
    }

    public String getTenantId() {
        return get(tenantFieldName).toString();
    }

    public void setTenantId(String id) {
        put(tenantFieldName, new Text(id));
    }

    public String getTenantIdField() {
        return tenantFieldName.toString();
    }

    @Override
    public void readFields(DataInput data) throws IOException {
        super.readFields(data);
        setTenantId(data.readLine());
    }

    @Override
    public void write(DataOutput data) throws IOException {
        data.writeBytes(getId());
        data.writeBytes(getTenantId());
    }

    @Override
    public String toString() {
        return "TenantAndIdEmittableKey [" + getIdField() + "=" + getId() + ", " + getTenantIdField() + "="
            + getTenantId() + "]";
    }

    @Override
    public BSONObject toBSON() {
        BSONObject rval = super.toBSON();
        rval.put(getTenantIdField(), getTenantId());
        return rval;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((getTenantId() == null) ? 0 : getTenantId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TenantAndIdEmittableKey other = (TenantAndIdEmittableKey) obj;
        if (!(getTenantId().equals(other.getTenantId()))) {
            return false;
        }
        if (getTenantId() == null) {
            if (other.getTenantId() != null) {
                return false;
            }
        } else if (!getTenantId().equals(other.getTenantId())) {
            return false;
        }
        return true;
    }

    @Override
    public LinkedHashSet<String> getFieldNames() {
        LinkedHashSet<String> rval = super.getFieldNames();
        rval.add(getTenantIdField());
        return rval;
    }

}
