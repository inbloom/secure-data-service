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


package org.slc.sli.ingestion.dal;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.springframework.core.convert.converter.Converter;

import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.ingestion.model.Error;

/**
 * Spring converter registered in the Mongo configuration to convert Error into DBObject.
 *
 */

public class ErrorWriteConverter implements Converter<Error, DBObject> {

    private EntityEncryption encryptor;

    @Override
    public DBObject convert(Error error) {

        BasicDBObject dbObj = new BasicDBObject();
        dbObj.put("_class", error.getClass().getName());
        dbObj.put("batchJobId", error.getBatchJobId());
        dbObj.put("stageName", error.getStageName());
        dbObj.put("resourceId", error.getResourceId());
        dbObj.put("sourceIp", error.getSourceIp());
        dbObj.put("hostname", error.getHostname());
        dbObj.put("timestamp", error.getTimestamp());
        dbObj.put("severity", error.getSeverity());
        dbObj.put("errorDetail", encryptor.encryptSingleValue(error.getErrorDetail()));
        return dbObj;

    }

    public EntityEncryption getEncryptor() {
        return encryptor;
    }

    public void setEncryptor(EntityEncryption encryptor) {
        this.encryptor = encryptor;
    }

}
