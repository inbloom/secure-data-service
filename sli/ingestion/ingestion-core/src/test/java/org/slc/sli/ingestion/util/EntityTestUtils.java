/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.ingestion.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import javax.xml.transform.stream.StreamSource;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.payload.JavaResult;
import org.milyn.payload.StringSource;
import org.mockito.Mockito;
import org.xml.sax.SAXException;

import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.ResourceWriter;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;
import org.slc.sli.ingestion.smooks.SliSmooks;
import org.slc.sli.ingestion.smooks.SmooksEdFiVisitor;
import org.slc.sli.ingestion.transformation.SimpleEntity;
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.EntityValidator;
import org.slc.sli.validation.ValidationError;

/**
 *
 * @author ablum
 *
 */
public class EntityTestUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static final Charset CHARSET_UTF8 = Charset.forName("utf-8");

    private static SliSmooks smooks;
    private static String testTargetSelector;

    public static void init(String smooksXmlConfigFilePath, String targetSelector,
            Set<String> recordLevelDeltaEnabledEntityNames, DeterministicUUIDGeneratorStrategy didGeneratorStrategy,
            DeterministicIdResolver didResolver) throws IOException, SAXException {

        smooks = new SliSmooks(smooksXmlConfigFilePath);
        testTargetSelector = targetSelector;

        BatchJobDAO batchJobDAO = Mockito.mock(BatchJobDAO.class);
        when(batchJobDAO.findRecordHash((String) any(), (String) any())).thenReturn(null);

        AbstractMessageReport errorReport = Mockito.mock(AbstractMessageReport.class);
        ReportStats reportStats = new SimpleReportStats();

        SmooksEdFiVisitor smooksEdFiVisitor = SmooksEdFiVisitor.createInstance("record", null, errorReport,
                reportStats, null);
        smooksEdFiVisitor.setRecordLevelDeltaEnabledEntities(recordLevelDeltaEnabledEntityNames);
        smooksEdFiVisitor.setBatchJobDAO(batchJobDAO);
        smooksEdFiVisitor.setDIdGeneratorStrategy(didGeneratorStrategy);
        smooksEdFiVisitor.setDIdResolver(didResolver);

        smooks.addVisitor(smooksEdFiVisitor, targetSelector);
    }

    private static List<NeutralRecord> getNeutralRecords(InputStream dataSource) throws IOException, SAXException,
            SmooksException {

        DummyResourceWriter dummyResourceWriter = new DummyResourceWriter();
        smooks.getSmooksEdFiVisitor().setNrMongoStagingWriter(dummyResourceWriter);

        JavaResult result = new JavaResult();
        smooks.filterSource(new StreamSource(dataSource), result);

        smooks.getSmooksEdFiVisitor().getRecordsPerisisted();
        List<NeutralRecord> entityList = dummyResourceWriter.getNeutralRecordsList();

        return entityList;
    }

    public static void mapValidation(Map<String, Object> obj, String schemaName, EntityValidator validator) {

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(obj);
        when(e.getType()).thenReturn(schemaName);

        try {
            Assert.assertTrue(validator.validate(e));
        } catch (EntityValidationException ex) {
            for (ValidationError err : ex.getValidationErrors()) {
                System.err.println(err);
            }
            Assert.fail();
        }
    }

    /**
     * Utility to make checking values in a map less verbose.
     *
     * @param map
     *            The map containing the entry we want to check.
     * @param key
     *            The string key for the entry
     * @param expectedValue
     *            The Object value we will assertEquals against
     */
    @SuppressWarnings("rawtypes")
    public static void assertObjectInMapEquals(final Map map, final String key, final Object expectedValue) {
        assertEquals("Object value in map does not match expected.", expectedValue, map.get(key));
    }

    /**
     * Utility to run smooks with provided configurations and return the first NeutralRecord (if
     * there is one)
     *
     * @param smooksXmlConfigFilePath
     *            path to smooks config xml
     * @param targetSelector
     *            selector for this run
     * @param testData
     *            string we want to use as the input for smooks run
     * @return first NeutralRecord
     * @throws IOException
     * @throws SAXException
     */
    public static NeutralRecord smooksGetSingleNeutralRecord(String smooksConfig, String targetSelector,
            String testData, Set<String> recordLevelDeltaEnabledEntityNames,
            DeterministicUUIDGeneratorStrategy didGeneratorStrategy, DeterministicIdResolver didResolver)
            throws IOException, SAXException {

        if (testTargetSelector != targetSelector) {
            init(smooksConfig, targetSelector, recordLevelDeltaEnabledEntityNames, didGeneratorStrategy, didResolver);
        }

        ByteArrayInputStream testDataStream = new ByteArrayInputStream(testData.getBytes());

        NeutralRecord neutralRecord = null;

        List<NeutralRecord> records = getNeutralRecords(testDataStream);
        if (records != null && records.size() > 0) {
            neutralRecord = records.get(0);
        }
        return neutralRecord;
    }

    /**
     *
     * @param smooksXmlConfigFilePath
     * @param targetSelector
     * @param testData
     * @param recordLevelDeltaEnabledEntityNames
     * @param didGeneratorStrategy
     * @param didResolver
     * @return
     * @throws IOException
     * @throws SAXException
     */
    public static List<NeutralRecord> smooksGetNeutralRecords(String smooksConfig, String targetSelector,
            String testData, Set<String> recordLevelDeltaEnabledEntityNames,
            DeterministicUUIDGeneratorStrategy didGeneratorStrategy, DeterministicIdResolver didResolver)
            throws IOException, SAXException {

        if (testTargetSelector != targetSelector) {
            init(smooksConfig, targetSelector, recordLevelDeltaEnabledEntityNames, didGeneratorStrategy,
                    didResolver);
        }

        ByteArrayInputStream testDataStream = new ByteArrayInputStream(testData.getBytes());

        return getNeutralRecords(testDataStream);
    }

    @SuppressWarnings("unchecked")
    public static SimpleEntity smooksGetSingleSimpleEntity(String smooksConfigPath, NeutralRecord item)
            throws IOException, SAXException {
        JavaResult result = new JavaResult();
        Smooks smooks = new Smooks(smooksConfigPath);
        List<SimpleEntity> entityList = new ArrayList<SimpleEntity>();
        try {

            StringSource source = new StringSource(MAPPER.writeValueAsString(item));

            smooks.filterSource(source, result);

            for (Entry<String, Object> resEntry : result.getResultMap().entrySet()) {
                if (resEntry.getValue() instanceof List) {
                    List<?> list = (List<?>) resEntry.getValue();
                    if (list.size() != 0 && list.get(0) instanceof SimpleEntity) {
                        entityList = (List<SimpleEntity>) list;
                        break;
                    }
                }
            }
            return entityList.get(0);
        } catch (IOException e) {
            entityList = Collections.emptyList();
        }
        return null;
    }

    /**
     * Reads the entire contents of a resource file found on the classpath and returns it as a
     * string.
     *
     * @param resourceName
     *            the name of the resource to locate on the classpath
     * @return the contents of the resource file
     * @throws FileNotFoundException
     *             if the resource cannot be located on the classpath
     */
    public static String readResourceAsString(String resourceName) throws FileNotFoundException {
        InputStream stream = getResourceAsStream(resourceName);
        if (stream == null) {
            throw new FileNotFoundException("Could not find resource " + resourceName + " in the classpath");
        }

        try {
            return convertStreamToString(stream);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                System.err.println(e);
                Assert.fail();
            }
        }
    }

    /**
     * Reads the contents of a stream in UTF-8 format and returns it as a string.
     *
     * @param stream
     *            the stream to read
     * @return the contents of the stream or an empty string if the stream has not content
     */
    public static String convertStreamToString(InputStream stream) {
        // Reference: http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
        // "The reason this works is because Scanner iterates over tokens in the stream,
        // and in this case we separate tokens using "beginning of the input boundary" (\A)
        // thus giving us only one token for the entire contents of the stream."
        // See also:
        // http://stackoverflow.com/questions/309424/in-java-how-do-a-read-convert-an-inputstream-in-to-a-string
        try {
            return new Scanner(stream, "UTF-8").useDelimiter("\\A").next();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    public static String delimit(String[] strings, String delimiter) {
        StringBuilder builder = new StringBuilder();
        for (String string : strings) {
            if (builder.length() > 0) {
                builder.append(delimiter);
            }
            builder.append(string);
        }
        return builder.toString();
    }

    public static URL getResource(String resourceName) {
        return EntityTestUtils.class.getClassLoader().getResource(resourceName);
    }

    public static InputStream getResourceAsStream(String resourceName) {
        return EntityTestUtils.class.getClassLoader().getResourceAsStream(resourceName);
    }

    private static final class DummyResourceWriter implements ResourceWriter<NeutralRecord> {

        private List<NeutralRecord> neutralRecordList;

        private DummyResourceWriter() {
            this.neutralRecordList = new ArrayList<NeutralRecord>();
        }

        @Override
        public void writeResource(NeutralRecord neutralRecord) {
            neutralRecordList.add(neutralRecord);
        }

        public List<NeutralRecord> getNeutralRecordsList() {
            return neutralRecordList;
        }

        @Override
        public void insertResource(NeutralRecord neutralRecord) {
            neutralRecordList.add(neutralRecord);
        }

        @Override
        public void insertResources(List<NeutralRecord> neutralRecords, String collectionName) {
            neutralRecordList.addAll(neutralRecords);
        }
    }
}
