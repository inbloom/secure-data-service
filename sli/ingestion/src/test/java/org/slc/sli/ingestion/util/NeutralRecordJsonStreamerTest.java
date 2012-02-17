package org.slc.sli.ingestion.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import org.slc.sli.ingestion.NeutralRecord;

/**
 * Tests for NeutralRecordJsonStreamer
 *
 * @author dduran
 *
 */
public class NeutralRecordJsonStreamerTest {

    @Test
    public void testParse() throws IOException {
        NeutralRecord neutralRecordWrite = new NeutralRecord();
        neutralRecordWrite.setLocalId("localId");

        File file = File.createTempFile("test_", ".json");

        writeJson(neutralRecordWrite, file);

        NeutralRecord neutralRecordRead = readJson(file);

        assertEquals(neutralRecordWrite, neutralRecordRead);
    }

    private void writeJson(NeutralRecord nr, File file) throws IOException {
        NeutralRecordJsonStreamer nrJsonStreamer = new NeutralRecordJsonStreamer(file);
        nrJsonStreamer.writeRecord(nr);
    }

    private NeutralRecord readJson(File file) throws IOException {
        NeutralRecordJsonStreamer nrJsonStreamer = new NeutralRecordJsonStreamer(file);
        return nrJsonStreamer.parseRecord();
    }

}
