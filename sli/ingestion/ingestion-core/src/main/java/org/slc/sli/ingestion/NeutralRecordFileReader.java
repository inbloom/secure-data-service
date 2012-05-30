package org.slc.sli.ingestion;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.file.SeekableFileInput;
import org.apache.avro.file.SeekableInput;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.util.LogUtil;

/**
 *
 */
public class NeutralRecordFileReader implements Iterator<NeutralRecord> {

    private static final Logger LOG = LoggerFactory.getLogger(NeutralRecordFileReader.class);

    protected File file;

    protected GenericDatumReader<GenericRecord> datum;
    protected DataFileReader<GenericRecord> reader;
    protected GenericData.Record avroRecord;

    protected ObjectMapper jsonObjectMapper;

    public NeutralRecordFileReader(byte[] data) throws IOException {
        this(new SeekableByteArrayInput(data));
    }

    public NeutralRecordFileReader(File file) throws IOException {
        this(new SeekableFileInput(file));
    }

    public NeutralRecordFileReader(SeekableInput input) throws IOException {
        this.datum = new GenericDatumReader<GenericRecord>();
        this.reader = new DataFileReader<GenericRecord>(input, datum);
        this.avroRecord = new GenericData.Record(reader.getSchema());

        this.jsonObjectMapper = new ObjectMapper();
    }

    protected static String getStringNullable(GenericData.Record avroRecord, String field) {
        if (avroRecord.get(field) != null) {
            return avroRecord.get(field).toString();
        } else {
            return null;
        }
    }

    /**
     * Extract the content of an avro-serialized NeutralRecord into a
     * fully-inflated instance.
     *
     * @param avroRecord
     *            - the avroRecord, parsed against the defined schema.
     * @return
     * @throws IOException
     */
    protected NeutralRecord getNeutralRecord(GenericData.Record avroRecord) throws IOException {
        NeutralRecord nr = new NeutralRecord();
        nr.setBatchJobId(getStringNullable(avroRecord, "jobId"));
        nr.setSourceId(getStringNullable(avroRecord, "sourceId"));
        nr.setLocalId(getStringNullable(avroRecord, "localId"));
        nr.setAssociation((Boolean) avroRecord.get("association"));
        nr.setRecordType(getStringNullable(avroRecord, "recordType"));

        if (avroRecord.get("attributes") != null) {

            // decode JSON back into map
            nr.setAttributes(jsonToMap(avroRecord.get("attributes")));
        }

        if (avroRecord.get("localParentIds") != null) {
            nr.setLocalParentIds(jsonToMap(avroRecord.get("localParentIds")));
        }

        nr.setAttributesCrc(getStringNullable(avroRecord, "attributesCrc"));
        return nr;
    }

    private Map<String, Object> jsonToMap(Object object) throws IOException {
        @SuppressWarnings("unchecked")
        Map<String, Object> attributesMap = jsonObjectMapper.readValue(object.toString(), Map.class);
//        DE260 - commenting out possibly sensitive data
//        LOG.debug("decoded json to map: {}", attributesMap);
        return attributesMap;
    }

    protected HashMap<String, Object> unencodeMap(Map<Utf8, Utf8> map) {
        HashMap<String, Object> normalMap = new HashMap<String, Object>();
        String key;
        String value;
        for (Entry<Utf8, Utf8> entry : map.entrySet()) {
            key = entry.getKey().toString();
            value = entry.getValue().toString();
            normalMap.put(key, value);
        }
        return normalMap;
    }

    @Override
    public boolean hasNext() {
        return this.reader.hasNext();
    }

    @Override
    public NeutralRecord next() {
        NeutralRecord neutralRecord = null;
        try {
            neutralRecord = getNeutralRecord((Record) this.reader.next());
        } catch (IOException e) {
            LogUtil.error(LOG, "Could not decode NeutralRecord", e);
        }
        return neutralRecord;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() operation is unsupported");
    }

    public void close() throws IOException {
        this.reader.close();
    }

}
