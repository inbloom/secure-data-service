package org.slc.sli.ingestion;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.util.ResourceUtils;

import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.processors.ContextManager;
import org.slc.sli.ingestion.processors.EdFiProcessor;
import org.slc.sli.ingestion.processors.PersistenceProcessor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@Ignore
public class IngestionTest {

    public static final String INGESTION_FILE_PREFIX = "conv";
    public static final String INGESTION_TEMP_FILE_SUFFIX = ".tmp";
    public static final String INGESTION_CSV_FILE_SUFFIX = ".csv";
    public static final String INGESTION_XML_FILE_SUFFIX = ".xml";

    @Autowired
    private ContextManager contextManager;

    @Autowired
    private EdFiProcessor edFiProcessor;

    @Autowired
    private PersistenceProcessor persistenceProcessor;

    @Before
    public void setup() {
    }

    protected ContextManager getRepositoryFactory() {
        return this.contextManager;
    }

    protected EdFiProcessor getEdFiProcessor() {
        return this.edFiProcessor;
    }

    protected PersistenceProcessor getPersistenceProcessor() {
        return this.persistenceProcessor;
    }

    public static InputStream createInputStream(String inputString) {
        return new ByteArrayInputStream(inputString.getBytes());
    }

    public static File getFile(String fileResourcePath)
            throws FileNotFoundException {
        if (!fileResourcePath.startsWith("classpath:")) {
            fileResourcePath = "classpath:" + fileResourcePath;
        }
        File file = ResourceUtils.getFile(fileResourcePath);
        return file;
    }

    public static InputStream getFileInputStream(String fileResourcePath)
            throws FileNotFoundException {
        if (!fileResourcePath.startsWith("classpath:")) {
            fileResourcePath = "classpath:" + fileResourcePath;
        }
        File file = ResourceUtils.getFile(fileResourcePath);
        return new BufferedInputStream(new FileInputStream(file));
    }

    public static OutputStream createFileOutputStream(String filePath)
            throws IOException {
        File file = new File(filePath);
        return new BufferedOutputStream(new FileOutputStream(file));
    }

    public static OutputStream createTempFileOutputStream() throws IOException {
        File file = createTempFile();
        return new BufferedOutputStream(new FileOutputStream(file));
    }

    public static File createTempFile() throws IOException {
        return createTempFile(INGESTION_FILE_PREFIX, INGESTION_TEMP_FILE_SUFFIX);
    }

    public static File createTempFile(String prefix, String suffix)
            throws IOException {
        File file = File.createTempFile(prefix, suffix);
        file.deleteOnExit();
        return file;
    }

    public static File createTestFile(String fileContents) throws IOException {
        return createTestFile(INGESTION_FILE_PREFIX,
                INGESTION_TEMP_FILE_SUFFIX, fileContents);
    }

    public static File createTestFile(String prefix, String suffix,
            String fileContents) throws IOException {
        File file = createTempFile(prefix, suffix);
        BufferedOutputStream outputStream = null;

        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
            outputStream.write(fileContents.getBytes());
        } finally {
            outputStream.close();
        }

        return file;
    }

    public static File createNeutralRecordsFile(
            List<NeutralRecord> neutralRecords) throws IOException {
        File file = createTempFile();

        // Create Ingestion Neutral record writer
        NeutralRecordFileWriter fileWriter = new NeutralRecordFileWriter(file);

        try {
            for (NeutralRecord item : neutralRecords)
                fileWriter.writeRecord(item);
        } finally {
            fileWriter.close();
        }

        return file;
    }

    public static List<NeutralRecord> getNeutralRecords(File inputFile)
            throws IOException {
        List<NeutralRecord> list = new ArrayList<NeutralRecord>();

        // Create Ingestion Neutral record reader
        NeutralRecordFileReader fileReader = new NeutralRecordFileReader(
                inputFile);

        // Ingestion Neutral record
        NeutralRecord ingestionRecord;

        try {

            // Iterate Ingestion neutral records/lines
            while (fileReader.hasNext()) {

                // Read next Ingestion neutral record/line
                ingestionRecord = fileReader.next();

                list.add(ingestionRecord);
            }
        } finally {
            fileReader.close();
        }

        return list;
    }

    public static long getTotalCountOfEntityInRepository(EntityRepository repository, String entityType) {
        int count = 0;
        Iterator<Entity> entities = repository.findAll(entityType).iterator();

        while (entities.hasNext()) {
            count++;
            entities.next();
        }

        return count;
    }

}
