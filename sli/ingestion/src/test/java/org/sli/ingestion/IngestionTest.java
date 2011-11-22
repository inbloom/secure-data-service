package org.sli.ingestion;

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
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.sli.ingestion.processors.ContextManager;
import org.sli.ingestion.processors.EdFiXmlProcessor;
import org.sli.ingestion.processors.PersistenceProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.util.ResourceUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ 
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class })

@Ignore
public class IngestionTest {

	public static final String INGESTION_FILE_PREFIX = "conv";
	public static final String INGESTION_FILE_SUFFIX = ".tmp";
	
	@Autowired
	private ContextManager contextManager;

	@Autowired
	private EdFiXmlProcessor edFiXmlProcessor;

	@Autowired
	private PersistenceProcessor persistenceProcessor;


	@Before
	public void setup() {
	}
	
	protected ContextManager getRepositoryFactory() {
		return this.contextManager;
	}
	
	protected EdFiXmlProcessor getEdFiXmlProcessor() {
		return this.edFiXmlProcessor;
	}
	
	protected PersistenceProcessor getPersistenceProcessor() {
		return this.persistenceProcessor;
	}
	
	protected InputStream createInputStream(String inputString) {
		return new ByteArrayInputStream(inputString.getBytes());
	}
	
	protected InputStream createFileResourceStream(String fileResourcePath) throws FileNotFoundException {
		if (!fileResourcePath.startsWith("classpath:")) {
			fileResourcePath = "classpath:" + fileResourcePath;
		}
		File file = ResourceUtils.getFile(fileResourcePath);
		return new BufferedInputStream(new FileInputStream(file));
	}
	
	protected OutputStream createFileOutputStream(String filePath) throws IOException {
		File file = new File(filePath);
		return new BufferedOutputStream(new FileOutputStream(file));
	}
	
	protected OutputStream createTempFileOutputStream() throws IOException {
		File file = this.createTempFile();
		return new BufferedOutputStream(new FileOutputStream(file));
	}
	
	protected File createTempFile() throws IOException {
		File file = File.createTempFile(INGESTION_FILE_PREFIX, INGESTION_FILE_SUFFIX);
		file.deleteOnExit();
		return file;
	}
	
	protected File createTestFile(String fileContents) throws IOException {	
		File file = this.createTempFile();
	    BufferedOutputStream outputStream = null;
	    
	    try {
			outputStream = new BufferedOutputStream( new FileOutputStream(file) );
		    outputStream.write(fileContents.getBytes());		    
	    }
	    finally {
		    outputStream.close();
	    }
	    
	    return file;
	}
	
	protected File createNeutralRecordsFile(List neutralRecords) throws IOException {	
		File file = this.createTempFile();
	    
		// Create Ingestion Neutral record writer
		NeutralRecordFileWriter fileWriter = new NeutralRecordFileWriter(file);
		
	    try {
	    	Iterator iterator = neutralRecords.iterator();
			while (iterator.hasNext()) {
				NeutralRecord neutralRecord = (NeutralRecord)iterator.next();
				
				fileWriter.writeRecord(neutralRecord);
			}
	    }
	    finally {
	    	fileWriter.close();
	    }
	    
	    return file;
	}
	
	protected String calculateTestDate(int studentId) {
		String testDate = "";
		
		int yearId = studentId % 10000;
		testDate = "" + yearId;
		if (yearId < 10) testDate = "000" + testDate;
		else if (yearId < 100) testDate = "00" + testDate;
		else if (yearId < 1000) testDate = "0" + testDate;
		
		testDate = testDate + "-01-01";
		return testDate;
	}
	
}
