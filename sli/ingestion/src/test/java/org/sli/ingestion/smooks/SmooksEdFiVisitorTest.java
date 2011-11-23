package org.sli.ingestion.smooks;

import org.sli.ingestion.NeutralRecord;
import org.sli.ingestion.NeutralRecordFileReader;
import org.sli.ingestion.NeutralRecordFileWriter;
import org.sli.ingestion.smooks.SmooksEdFiVisitor;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.milyn.Smooks;
import org.milyn.payload.JavaResult;
import org.milyn.payload.StringResult;
import org.springframework.util.ResourceUtils;
import org.xml.sax.SAXException;


//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:/spring/applicationContext.xml" })

public class SmooksEdFiVisitorTest {
	
	
	@Test
	public void testJavaResult() throws IOException, SAXException {

		// Instantiate Smooks with the config...
		InputStream messageIn = null;

		try {

			File inFile = ResourceUtils
					.getFile("classpath:smooks/InterchangeStudent.xml");

			messageIn = new BufferedInputStream(new FileInputStream(inFile));

			Smooks smooks = new Smooks("smooks-config.xml");
			//smooks.addVisitor(new SmooksEdFiVisitor("student"), "InterchangeStudent/Student");
			
			JavaResult result = new JavaResult();
			
			smooks.filterSource(new StreamSource(messageIn), result);			
			
			ArrayList<NeutralRecord> records = (ArrayList<NeutralRecord>) result.getBean("records");			
			assertEquals(records.size(), 100);
			
			// TODO assert for a few entries that all values got successfully mapped
			
		} finally {

			IOUtils.closeQuietly(messageIn);
		}

	}


	@Test
	public void testFileWriterResult() throws IOException, SAXException {

		// Instantiate Smooks with the config...
		InputStream messageIn = null;

		try {

			File inFile = ResourceUtils
					.getFile("classpath:smooks/InterchangeStudent.xml");

			messageIn = new BufferedInputStream(new FileInputStream(inFile));
			File outputFile = File.createTempFile("test", ".dat");
			outputFile.deleteOnExit();
			NeutralRecordFileWriter nrfWriter = 
				new NeutralRecordFileWriter(outputFile);
			
			Smooks smooks = new Smooks("smooks-config.xml");
			smooks.addVisitor(new SmooksEdFiVisitor("record", nrfWriter), 
					"InterchangeStudent/Student");
			
			try {
				smooks.filterSource(new StreamSource(messageIn));	
			} finally {
				nrfWriter.close();
			}
			
			int c = 0;
			NeutralRecordFileReader nrfr = new NeutralRecordFileReader(
					new File(outputFile.getAbsolutePath()));
			try {
				while (nrfr.hasNext()) {
					NeutralRecord value = nrfr.next();
					// System.out.println(value.getAttributes());
					c ++;
				}
				assertEquals(c, 100);
			} finally {
				nrfr.close();
			}
			
		    
		} finally {

			IOUtils.closeQuietly(messageIn);
		}

	}


	@Test
	public void testSchool() throws IOException, SAXException {	

		// Instantiate Smooks with the config...
		InputStream messageIn = null;

		try {

			File inFile = ResourceUtils
					.getFile("classpath:smooks/InterchangeSchool.xml");

			messageIn = new BufferedInputStream(new FileInputStream(inFile));

			Smooks smooks = new Smooks("smooks-config.xml");
			//smooks.addVisitor(new SmooksEdFiVisitor("student"), "InterchangeStudent/Student");
			
			JavaResult result = new JavaResult();
			
			smooks.filterSource(new StreamSource(messageIn), result);			
			
			ArrayList<NeutralRecord> records = (ArrayList<NeutralRecord>) result.getBean("records");			
			assertEquals(records.size(), 2);
			
			// TODO assert for a few entries that all values got successfully mapped
			
		} finally {

			IOUtils.closeQuietly(messageIn);
		}

	}



	@Test
	public void testStudentSchoolAssociation() throws IOException, SAXException {	

		// Instantiate Smooks with the config...
		InputStream messageIn = null;

		try {

			File inFile = ResourceUtils
					.getFile("classpath:smooks/InterchangeEnrollment.xml");

			messageIn = new BufferedInputStream(new FileInputStream(inFile));

			Smooks smooks = new Smooks("smooks-config.xml");
			//smooks.addVisitor(new SmooksEdFiVisitor("student"), "InterchangeStudent/Student");
			
			JavaResult result = new JavaResult();
			
			smooks.filterSource(new StreamSource(messageIn), result);			
			
			ArrayList<NeutralRecord> records = (ArrayList<NeutralRecord>) result.getBean("records");			
			assertEquals(records.size(), 100);
			
			// TODO assert for a few entries that all values got successfully mapped
			
		} finally {

			IOUtils.closeQuietly(messageIn);
		}

	}

}
