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

/**
 * EmittableKey for holding both a tenant identifier and an object id
 *
 */
public class TenantAndIdEmittableKey extends EmittableKey {

    protected Text tenantFieldName = null;
    protected IdFieldEmittableKey idKey = null;

    public TenantAndIdEmittableKey() {
        this("_id", "metaData.tenantId");
    }

    public TenantAndIdEmittableKey(final String tenantIdFieldName, final String idFieldName) {
        String[] fields = { idFieldName, tenantIdFieldName };
        setFieldNames(fields);

        tenantFieldName = new Text(tenantIdFieldName);
        this.idKey = new IdFieldEmittableKey(idFieldName);
    }

    public Text getTenantId() {
        return (Text) get(tenantFieldName);
    }

    public void setTenantId(final Text id) {
        put(tenantFieldName, id);
    }

    public String getTenantIdField() {
        return tenantFieldName.toString();
    }

    public Text getId() {
        return idKey.getId();
    }

    public void setId(final Text id) {
        idKey.setId(id);
    }

    public Text getIdField() {
        return idKey.getIdField();
    }

    @Override
    public void readFields(DataInput data) throws IOException {
        idKey.readFields(data);
        setTenantId(new Text(data.readLine()));
    }

    @Override
    public void write(DataOutput data) throws IOException {
        data.writeBytes(idKey.getId().toString());
        data.writeBytes(getTenantId().toString());
    }

    @Override
    public String toString() {
        return "TenantAndIdEmittableKey [" + getIdField() + "=" + getId().toString() + ", " + getTenantIdField() + "="
            + getTenantId().toString() + "]";
    }

    @Override
    public BSONObject toBSON() {
        BSONObject rval = idKey.toBSON();
        rval.put(getTenantIdField(), getTenantId().toString());
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
    public int compareTo(EmittableKey other) {
        return getId().toString().compareTo(other.get(getIdField()).toString());
    }
}
