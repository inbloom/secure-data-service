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


package org.slc.sli.ingestion;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junitx.util.PrivateAccessor;
import org.apache.avro.generic.GenericRecord;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit tests to test the NeutralRecordFileWriter class.
 * @author ablum
 *
 */
public class NeutralRecordFileWriterTest {
    
    @Test
    public void writeRecordTest() throws IOException, NoSuchFieldException {
        NeutralRecord record = new NeutralRecord();
        record.setSourceId("mySourceId");
        record.setRecordType("myRecordType");
        record.setLocalId("myLocalId");
        record.setBatchJobId("myBatchJobId");
        record.setAttributesCrc("myAttributesCrc");
        
        Map<String, Object> localParentIds = new HashMap<String, Object>();
        localParentIds.put("Teacher", "TeacherID");
        record.setLocalParentIds(localParentIds);
        
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("name", "Name");
        record.setAttributes(attributes);
        
        File outputStream = new File("test.tmp");
        
        NeutralRecordFileWriter nrfw = new NeutralRecordFileWriter(outputStream);
        nrfw.writeRecord(record);
        
        GenericRecord avroRecord = (GenericRecord) PrivateAccessor.getField(nrfw, "avroRecord");
        String json = avroRecord.toString();
        Assert.assertTrue(json.contains("\"sourceId\": \"mySourceId\""));
        Assert.assertTrue(json.contains("\"recordType\": \"myRecordType\""));
        Assert.assertTrue(json.contains("\"localId\": \"myLocalId\""));
        Assert.assertTrue(json.contains("\"jobId\": \"myBatchJobId\""));
        Assert.assertTrue(json.contains("\"atrributesCrc\": \"myAttributesCrc\""));
        Assert.assertTrue(json.contains("\"localParentIds\": \"{\\\"Teacher\\\":\\\"TeacherID\\\"}\""));
        Assert.assertTrue(json.contains("\"attributes\": \"{\\\"name\\\":\\\"Name\\\"}\""));
    }
    
}
