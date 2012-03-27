package org.slc.sli.ingestion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

/**
 *
 */
public class NeutralRecordFileWriter {
    
    private static final Logger LOG = LoggerFactory.getLogger(NeutralRecordFileWriter.class);
    
    private Schema avroSchema;
    private GenericDatumWriter<GenericRecord> avroDatumWriter;
    private DataFileWriter<GenericRecord> avroDataFileWriter;
    private GenericRecord avroRecord;
    
    protected ObjectMapper jsonObjectMapper;
    
    /**
     * @param outputFile
     * @throws IOException
     */
    public NeutralRecordFileWriter(File outputFile) throws IOException {
        this(new FileOutputStream(outputFile));
    }
    
    /**
     * @param outputStream
     * @throws IOException
     */
    public NeutralRecordFileWriter(OutputStream outputStream) throws IOException {
        try {
            
            // Obtain AVRO schema file
            File schemaFile = ResourceUtils.getFile("classpath:neutral.avpr");
            
            // initialize some avro specifics
            Schema.Parser parser = new Schema.Parser();
            
            this.avroSchema = parser.parse(schemaFile);
            this.avroDatumWriter = new GenericDatumWriter<GenericRecord>(this.avroSchema);
            this.avroDataFileWriter = new DataFileWriter<GenericRecord>(this.avroDatumWriter);
            this.avroDataFileWriter.create(this.avroSchema, outputStream);
            
            // initialize an empty instance of the record
            this.avroRecord = new GenericData.Record(this.avroSchema);
            
            this.jsonObjectMapper = new ObjectMapper();
            
        } catch (FileNotFoundException fileNotFoundException) {
            LOG.error(fileNotFoundException.toString());
        }
        
    }
    
    public void writeRecord(NeutralRecord record) throws IOException {
        // populate the sourceId if present
        if (record.getSourceId() != null) {
            avroRecord.put("sourceId", new Utf8(record.getSourceId()));
        } else {
            avroRecord.put("sourceId", null);
        }
        
        // populate the jobId if present
        if (record.getBatchJobId() != null) {
            avroRecord.put("jobId", new Utf8(record.getBatchJobId()));
        } else {
            avroRecord.put("jobId", null);
        }
        
        // populate the localId
        if (record.getLocalId() != null) {
            avroRecord.put("localId", new Utf8(record.getLocalId().toString()));
        } else {
            avroRecord.put("localId", null);
        }
        
        // populate the localId
        avroRecord.put("association", record.isAssociation());
        
        // populate localParentIds if present
        if (record.getLocalParentIds() != null && record.getLocalParentIds().size() > 0) {
            avroRecord.put("localParentIds", new Utf8(maptoJson(record.getLocalParentIds())));
        } else {
            avroRecord.put("localParentIds", null);
        }
        
        // populate the recordType
        if (record.getRecordType() != null) {
            avroRecord.put("recordType", new Utf8(record.getRecordType()));
        } else {
            avroRecord.put("recordType", null);
        }
        
        // populate attributes if present
        if (record.getAttributes() != null && record.getAttributes().size() > 0) {
            
            // we will store the map in JSON format because of AVRO limitations with mixed-type maps
            // TODO: look into more permanent solution
            avroRecord.put("attributes", new Utf8(maptoJson(record.getAttributes())));
        } else {
            avroRecord.put("attributes", null);
        }
        
        // populate the attributesCrc if present
        if (record.getAttributesCrc() != null) {
            avroRecord.put("atrributesCrc", new Utf8(record.getAttributesCrc()));
        } else {
            avroRecord.put("atrributesCrc", null);
        }
        
        this.avroDataFileWriter.append(avroRecord);
    }
    
    private String maptoJson(Map<String, Object> attributes) throws IOException {
        String jsonVal = jsonObjectMapper.writeValueAsString(attributes);
        LOG.debug("encoded attributes map to json: {}", jsonVal);
        return jsonVal;
    }
    
    public void close() throws IOException {
        this.avroDataFileWriter.close();
    }
    
}
