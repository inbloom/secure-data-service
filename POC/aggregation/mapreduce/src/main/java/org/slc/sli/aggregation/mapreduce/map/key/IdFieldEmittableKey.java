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

package org.slc.sli.aggregation.mapreduce.map.key;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

/**
 * IdFieldEmittableKey - A generic emittable key instance that treats one or more Mongo fields
 * as emit ID keys.
 */
public class IdFieldEmittableKey extends EmittableKey {

    public IdFieldEmittableKey() {
        this("_id");
    }

    public IdFieldEmittableKey(final String mongoFieldName) {
        super.setFieldName(mongoFieldName);
    }

    public Text getIdField() {
        return super.getFieldName();
    }

    public Text getId() {
        return (Text) get(getIdField());
    }

    public void setId(final Text value) {
        put(new Text(getIdField()), value);
    }

    @Override
    public void readFields(DataInput data) throws IOException {
        this.setFieldName(data.readLine());
        this.setId(new Text(data.readLine()));
    }

    @Override
    public void write(DataOutput data) throws IOException {
        data.writeBytes(getIdField().toString() + "\n");
        data.writeBytes(getId().toString() + "\n");
    }

    @Override
    public String toString() {
        return "IdFieldEmittableKey [" + getIdField() + "=" + getId().toString() + "]";
    }

    @Override
    public BSONObject toBSON() {
        BSONObject rval = new BasicBSONObject();
        rval.put(getIdField().toString(), getId().toString());
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
        if (!(getIdField().equals(other.getIdField()))) {
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
    public int compareTo(EmittableKey other) {
        if (other instanceof IdFieldEmittableKey) {
            IdFieldEmittableKey obj = (IdFieldEmittableKey) other;
            return this.getId().compareTo(obj.getId());
        } else {
            return -1;
        }
    }

}
