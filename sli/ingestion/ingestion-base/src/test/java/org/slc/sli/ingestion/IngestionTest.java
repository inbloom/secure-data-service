/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.util.ResourceUtils;

/**
 * a set of static functions that can be used by Ingestion Tests.
 * 
 * @author yuan
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@Ignore
public class IngestionTest {
    
    public static final String INGESTION_FILE_PREFIX = "conv";
    public static final String INGESTION_TEMP_FILE_SUFFIX = ".tmp";
    public static final String INGESTION_CSV_FILE_SUFFIX = ".csv";
    public static final String INGESTION_XML_FILE_SUFFIX = ".xml";
    
    public static File getFile(String fileResourcePath) throws FileNotFoundException {
        if (!fileResourcePath.startsWith("classpath:")) {
            fileResourcePath = "classpath:" + fileResourcePath;
        }
        File file = ResourceUtils.getFile(fileResourcePath);
        return file;
    }
    
    public static InputStream getFileInputStream(String fileResourcePath) throws FileNotFoundException {
        if (!fileResourcePath.startsWith("classpath:")) {
            fileResourcePath = "classpath:" + fileResourcePath;
        }
        File file = ResourceUtils.getFile(fileResourcePath);
        return new BufferedInputStream(new FileInputStream(file));
    }
    
    public static OutputStream createFileOutputStream(String filePath) throws IOException {
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
    
    public static File createTempFile(String prefix, String suffix) throws IOException {
        File file = File.createTempFile(prefix, suffix);
        file.deleteOnExit();
        return file;
    }
    
    public static File createTestFile(String fileContents) throws IOException {
        return createTestFile(INGESTION_FILE_PREFIX, INGESTION_TEMP_FILE_SUFFIX, fileContents);
    }
    
    public static File createTestFile(String prefix, String suffix, String fileContents) throws IOException {
        File file = createTempFile(prefix, suffix);
        BufferedOutputStream outputStream = null;
        
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
            outputStream.write(fileContents.getBytes());
        } finally {
            
            if (outputStream != null) {
                outputStream.close();
            }
        }
        
        return file;
    }
    
}
