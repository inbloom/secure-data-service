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
import org.bson.BasicBSONObject;

/**
 * IdFieldEmittableKey - A generic emittable key instance that treats one or more Mongo fields
 * as emit ID keys.
 */
public class IdFieldEmittableKey extends EmittableKey<IdFieldEmittableKey> {

    protected static final String DEFAULT_ID_FIELD = "_id";

    protected Text fieldName = null;

    public IdFieldEmittableKey() {
        fieldName = new Text(DEFAULT_ID_FIELD);
    }

    public IdFieldEmittableKey(final String mongoFieldName) {
        fieldName = new Text(mongoFieldName);
    }

    public String getIdField() {
        return fieldName.toString();
    }

    public String getId() {
        return get(fieldName).toString();
    }

    public void setId(final String value) {
        put(fieldName, new Text(value));
    }

    @Override
    public void readFields(DataInput data) throws IOException {
        setId(data.readLine());
    }

    @Override
    public void write(DataOutput data) throws IOException {
        data.writeBytes(getId());
    }

    @Override
    public String toString() {
        return "IdFieldEmittableKey [" + getIdField() + "=" + getId() + "]";
    }

    @Override
    public BSONObject toBSON() {
        BSONObject rval = new BasicBSONObject();
        rval.put(getIdField(), getId());
        return rval;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
        IdFieldEmittableKey other = (IdFieldEmittableKey) obj;
        if (!(fieldName.equals(other.fieldName))) {
            return false;
        }
        if (getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!getId().equals(other.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(IdFieldEmittableKey other) {
        return this.getId().compareTo(other.getId());
    }

    @Override
    public LinkedHashSet<String> getFieldNames() {
        LinkedHashSet<String> rval = new LinkedHashSet<String>();
        rval.add(fieldName.toString());
        return rval;
    }
}
