package org.slc.sli.ingestion;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.util.Utf8;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Deprecated
public class NeutralRecordFileReader implements Iterator {

    private static final Logger LOG = LoggerFactory.getLogger(NeutralRecordFileReader.class);

    protected File file;

    protected GenericDatumReader datum;
    protected DataFileReader reader;
    protected GenericData.Record avroRecord;

    protected ObjectMapper jsonObjectMapper;

    public NeutralRecordFileReader(File file) throws IOException {
        this.datum = new GenericDatumReader();
        this.reader = new DataFileReader(file, datum);
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
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    protected NeutralRecord getNeutralRecord(GenericData.Record avroRecord) throws IOException {
        NeutralRecord nr = new NeutralRecord();
        nr.setJobId(getStringNullable(avroRecord, "jobId"));
        nr.setSourceId(getStringNullable(avroRecord, "sourceId"));
        nr.setLocalId(getStringNullable(avroRecord, "localId"));
        nr.setRecordType(getStringNullable(avroRecord, "recordType"));

        if (avroRecord.get("attributes") != null) {

            // decode JSON back into map
            nr.setAttributes(jsonToMap(avroRecord.get("attributes")));
        }

        if (avroRecord.get("localParentIds") != null) {
            nr.setLocalParentIds(unencodeMap((Map<Utf8, Utf8>) avroRecord.get("localParentIds")));
        }

        nr.setAttributesCrc(getStringNullable(avroRecord, "attributesCrc"));
        return nr;
    }

    private Map<String, Object> jsonToMap(Object object) throws IOException {
        Map<String, Object> attributesMap = jsonObjectMapper.readValue(object.toString(), Map.class);
        LOG.debug("decoded json to map: " + attributesMap);
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

    public boolean hasNext() {
        return this.reader.hasNext();
    }

    public NeutralRecord next() {
        NeutralRecord neutralRecord = null;
        try {
            neutralRecord = getNeutralRecord((Record) this.reader.next());
        } catch (IOException e) {
            LOG.error("Could not decode NeutralRecord {}", Arrays.asList(e.getStackTrace()));
        }
        return neutralRecord;
    }

    public void remove() {
        throw new UnsupportedOperationException("remove() operation is unsupported");
    }

    public void close() throws IOException {
        this.reader.close();
    }

}
