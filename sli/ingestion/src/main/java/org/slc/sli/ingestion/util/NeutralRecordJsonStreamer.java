package org.slc.sli.ingestion.util;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.MappingJsonFactory;

import org.slc.sli.ingestion.NeutralRecord;

/**
 * Utility methods for dealing with JSON files and format.
 *
 * @author dduran
 *
 */
public class NeutralRecordJsonStreamer implements FileRecordWriter<NeutralRecord>, FileRecordParser<NeutralRecord> {

    private final JsonFactory jsonFactory;
    private final File streamFile;

    private JsonGenerator jsonGenerator;
    private JsonParser jsonParser;

    public NeutralRecordJsonStreamer(File streamFile) {
        this.jsonFactory = new MappingJsonFactory();
        this.streamFile = streamFile;
    }

    @Override
    public void writeRecord(NeutralRecord neutralRecord) throws IOException {

        if (jsonGenerator == null) {
            jsonGenerator = jsonFactory.createJsonGenerator(streamFile, JsonEncoding.UTF8);
        }

        jsonGenerator.writeObject(neutralRecord);
    }

    @Override
    public void close() {
        try {
            if (jsonGenerator != null) {
                jsonGenerator.close();
            }

            if (jsonParser != null) {
                jsonParser.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public NeutralRecord parseRecord() throws IOException {
        NeutralRecord neutralRecord = null;

        if (jsonParser == null) {
            jsonParser = jsonFactory.createJsonParser(streamFile);
        }

        if (jsonParser.nextValue() != null) {
            neutralRecord = jsonParser.readValueAs(NeutralRecord.class);
        }

        return neutralRecord;
    }

}
