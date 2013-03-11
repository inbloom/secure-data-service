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
package org.slc.sli.ingestion.parser.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.jdom2.JDOMException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import org.slc.sli.ingestion.parser.RecordMeta;
import org.slc.sli.ingestion.parser.RecordVisitor;
import org.slc.sli.ingestion.reporting.impl.DummyMessageReport;
import org.slc.sli.ingestion.reporting.impl.JobSource;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;

/**
 *
 * @author slee
 *
 */
public class EntityTestHelper {

    public static final Logger LOG = LoggerFactory.getLogger(EntityTestHelper.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static XsdTypeProvider tp = new XsdTypeProvider();
    static {
        try {
            tp.setSchemaFiles(new PathMatchingResourcePatternResolver().getResources("classpath:edfiXsd-SLI/*.xsd"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }
    }

    public static void parseAndVerify(Resource schema, Resource inputXmlResource, Resource expectedJsonResource)
            throws Throwable {

        RecordVisitor mockVisitor = Mockito.mock(RecordVisitor.class);
        EdfiRecordUnmarshaller.parse(inputXmlResource.getInputStream(), schema, tp, mockVisitor,
                new DummyMessageReport(), new SimpleReportStats(), new JobSource(inputXmlResource.getFilename()));

        captureAndCompare(mockVisitor, expectedJsonResource);

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void captureAndCompare(RecordVisitor mockVisitor, Resource expectedJsonResource) throws IOException {

        ArgumentCaptor<Map> recordCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mockVisitor, atLeastOnce()).visit(any(RecordMeta.class), recordCaptor.capture());
        LOG.debug("Visitor invoked {} times.", recordCaptor.getAllValues().size());

        LOG.info("Visitor invoked with value: {}", objectMapper.writeValueAsString(recordCaptor.getValue()));

        Map<String, Object> expected = objectMapper.readValue(expectedJsonResource.getFile(), Map.class);
        compare(expected, recordCaptor.getValue());
    }

    @SuppressWarnings("unchecked")
    private static void compare(Map<String, Object> expected, Map<String, Object> record) {
        assertEquals("keys:\n" + expected.keySet() + "\n" + record.keySet() + "\n", expected.size(), record.size());

        for (String key : expected.keySet()) {
            assertTrue("Missing expected key: " + key, record.containsKey(key));
        }

        for (Map.Entry<String, Object> entry : expected.entrySet()) {
            String key = entry.getKey();
            Object exp = entry.getValue();
            Object rec = record.get(key);

            if (exp instanceof String) {
                assertTrue("Missing expected String value type for key: " + key, rec instanceof String);
                assertEquals(exp, rec);

            } else if (exp instanceof Integer) {
                assertTrue("Missing expected Integer value type for key: " + key, rec instanceof Integer);
                assertEquals(exp, rec);

            } else if (exp instanceof Double) {
                assertTrue("Missing expected Double value type for key: " + key, rec instanceof Double);
                assertEquals(exp, rec);

            } else if (exp instanceof Boolean) {
                assertTrue("Missing expected Boolean value type for key: " + key, rec instanceof Boolean);
                assertEquals(exp, rec);

            } else if (exp instanceof Map) {
                assertTrue("Missing expected Map value type for key: " + key, rec instanceof Map);
                compare((Map<String, Object>) exp, (Map<String, Object>) rec);

            } else if (exp instanceof List) {
                assertTrue("Missing expected List value type for key: " + key, rec instanceof List);
                @SuppressWarnings("rawtypes")
                List ex = (List) exp;
                @SuppressWarnings("rawtypes")
                List rc = (List) rec;
                assertEquals("Wrong List value size for key: " + key, ex.size(), rc.size());

                if (ex.get(0) instanceof String) {
                    for (Object o : rc) {
                        assertTrue("Missing expected String type for key: " + key, o instanceof String);
                    }
                    compareStringList(ex, rc);
                }

                if (ex.get(0) instanceof Map) {
                    for (Object o : rc) {
                        assertTrue("Missing expected Map type for key: " + key, o instanceof Map);
                    }
                    compareMapList(ex, rc);
                }
            } else {
                assertTrue("Expected a known and checked type: " + exp, false);
            }
        }
    }

    private static void compareStringList(List<String> expected, List<String> record) {
        String[] exp = (String[]) expected.toArray();
        String[] rec = (String[]) record.toArray();
        Arrays.sort(exp);
        Arrays.sort(rec);
        assertArrayEquals(exp, rec);
    }

    @SuppressWarnings("unchecked")
    private static void compareMapList(List<Map<String, Object>> expected, List<Map<String, Object>> record) {
        compare1stMapInList(expected.get(0), record.get(0));

        Map<String, List<Object>> exp = aggregateMapListByKey(expected);
        Map<String, List<Object>> rec = aggregateMapListByKey(record);

        assertEquals("Error list size\n" + exp + "\n" + rec + "\n", exp.size(), rec.size());
        for (String key : exp.keySet()) {
            assertTrue("Missing expected key: " + key, rec.containsKey(key));
            Object[] exv = exp.get(key).toArray();
            Object[] rcv = rec.get(key).toArray();
            assertEquals("Error array size for key: " + key, exv.length, rcv.length);

            if (exv[0] instanceof String) {
                assertTrue("Missing expected String type for key: " + key, rcv[0] instanceof String);
                Arrays.sort(exv);
                Arrays.sort(rcv);
                assertArrayEquals(exv, rcv);
            }

            if (exv[0] instanceof Map) {
                assertTrue("Missing expected Map type for key: " + key, rcv[0] instanceof Map);
                List<Map<String, Object>> exl = new ArrayList<Map<String, Object>>(rcv.length);
                for (Object o : exv) {
                    exl.add((Map<String, Object>) o);
                }
                List<Map<String, Object>> rcl = new ArrayList<Map<String, Object>>(rcv.length);
                for (Object o : rcv) {
                    rcl.add((Map<String, Object>) o);
                }
                compareMapList(exl, rcl);
            }
        }
    }

    private static Map<String, List<Object>> aggregateMapListByKey(List<Map<String, Object>> list) {
        Map<String, List<Object>> tmp = new HashMap<String, List<Object>>();
        for (Map<String, Object> map : list) {
            for (String key : map.keySet()) {
                if (!tmp.containsKey(key)) {
                    tmp.put(key, new ArrayList<Object>());
                }
                tmp.get(key).add(map.get(key));
            }
        }
        return tmp;
    }

    @SuppressWarnings("unchecked")
    private static void compare1stMapInList(Map<String, Object> exp, Map<String, Object> rec) {
        for (String key : exp.keySet()) {
            assertTrue("Missing expected key: " + key, rec.containsKey(key));

            if (exp.get(key) instanceof String) {
                assertTrue("Missing expected String type for key: " + key, rec.get(key) instanceof String);
                assertEquals(exp.get(key), rec.get(key));
            }

            if (exp.get(key) instanceof Map) {
                assertTrue("Missing expected Map type for key: " + key, rec.get(key) instanceof Map);
                compare((Map<String, Object>) exp.get(key), (Map<String, Object>) rec.get(key));
            }

            if (exp.get(key) instanceof List) {
                assertTrue("Missing expected List type for key: " + key, rec.get(key) instanceof List);
                @SuppressWarnings("rawtypes")
                List ex = (List) exp.get(key);
                @SuppressWarnings("rawtypes")
                List rc = (List) rec.get(key);
                assertEquals("Wrong List value size for key: " + key, ex.size(), rc.size());

                if (ex.get(0) instanceof String) {
                    for (Object o : rc) {
                        assertTrue("Missing expected String type for key: " + key, o instanceof String);
                    }
                    compareStringList(ex, rc);
                }

                if (ex.get(0) instanceof Map) {
                    for (Object o : rc) {
                        assertTrue("Missing expected Map type for key: " + key, o instanceof Map);
                    }
                    compareMapList(ex, rc);
                }
            }
        }
    }
}
