package org.sli.ingestion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;


/**
 * 
 */
public class NeutralRecordFileWriter {

    Logger log = LoggerFactory.getLogger(NeutralRecordFileWriter.class);

    protected File outputFile;

    protected Schema avroSchema;
    protected GenericDatumWriter avroDatumWriter;
    protected DataFileWriter avroDataFileWriter;
    protected GenericRecord avroRecord;
    
    /**
     * @param outputStream
     * @throws IOException 
     */
    public NeutralRecordFileWriter(File outputFile) throws IOException {
        
        this.outputFile = outputFile;

        try {
            
            // Obtain AVRO schema file
            File schemaFile = ResourceUtils.getFile("classpath:neutral.avpr");
            
            // initialize some avro specifics
            this.avroSchema = Schema.parse(schemaFile);
            this.avroDatumWriter = new GenericDatumWriter(this.avroSchema);
            this.avroDataFileWriter = new DataFileWriter(this.avroDatumWriter);
            this.avroDataFileWriter.create(this.avroSchema, this.outputFile);

            // initialize an empty instance of the record
            this.avroRecord = new GenericData.Record(this.avroSchema);
            
        } catch (FileNotFoundException fileNotFoundException) {
            log.error(fileNotFoundException.toString());
        }
        
    }

    protected HashMap<Utf8, Utf8> encodeMap(Map<String, String> map) {
        HashMap<Utf8, Utf8> avroMap = new HashMap<Utf8, Utf8>();
        String key;
        String value;
        for (Entry<String, String> entry : map.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            avroMap.put(new Utf8(key), new Utf8(value));
        }
        return avroMap;        
    }
    
    public void writeRecord(NeutralRecord record) throws IOException {
        
        // populate the sourceId if present
        if (record.getSourceId() != null) {
            avroRecord.put("sourceId", new Utf8(record.getSourceId()));
        } else {
            avroRecord.put("sourceId", null);
        }
        
        // populate the jobId if present
        if (record.getJobId() != null) {
            avroRecord.put("jobId", new Utf8(record.getJobId()));
        } else {
            avroRecord.put("jobId", null);
        }
        
        // populate the localId
        avroRecord.put("localId", new Utf8(record.getLocalId()));
        
        // populate localParentIds if present
        if (record.getLocalParentIds() != null 
            && record.getLocalParentIds().size() > 0) {
            avroRecord.put(
                    "localParentIds", encodeMap(record.getLocalParentIds()));
        } else {
            avroRecord.put("localParentIds", null);
        }
        
        // populate the recordType
        avroRecord.put("recordType", new Utf8(record.getRecordType()));
        
        // populate attributes if present
        if (record.getAttributes() != null && record.getAttributes().size() > 0) {
            avroRecord.put("attributes", encodeMap(record.getAttributes()));
        } else {
            avroRecord.put("attributes", null);
        }
        
        // populate the attributesCrc if present
        if (record.getAttributesCrc() != null) {
            avroRecord.put("atrributesCrc", 
                    new Utf8(record.getAttributesCrc()));
        } else {
            avroRecord.put("atrributesCrc", null);
        }
        
        this.avroDataFileWriter.append(avroRecord);
    } 
    
    public void close() throws IOException {
        this.avroDataFileWriter.close();
    }
    
}
