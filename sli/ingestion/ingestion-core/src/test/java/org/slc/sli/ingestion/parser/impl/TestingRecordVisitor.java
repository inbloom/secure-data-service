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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.ingestion.parser.RecordMeta;
import org.slc.sli.ingestion.parser.RecordVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**
 * 
 * @author slee
 *
 */
public final class TestingRecordVisitor implements RecordVisitor {
    public static final Logger LOG = LoggerFactory.getLogger(TestingRecordVisitor.class);

    private Resource expectedResource;
    private ObjectMapper objectMapper;

    public TestingRecordVisitor(Resource expectedResource, ObjectMapper objectMapper) {
        this.expectedResource = expectedResource;
        this.objectMapper = objectMapper;
    }

    @Override
    public void visit(RecordMeta edfiType, Map<String, Object> record) {
        try {
            LOG.debug(objectMapper.writeValueAsString(record));

            @SuppressWarnings("unchecked")
            Map<String, Object> expected = objectMapper.readValue(expectedResource.getFile(), Map.class);

            compare(expected, record);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    private void compare(Map<String, Object> expected, Map<String, Object> record) {

        assertEquals(expected.size(), record.size());

        for (String key : expected.keySet()) {
            assertTrue("Missing expected key: " + key, record.containsKey(key));
        }

        for (Map.Entry<String, Object> entry : expected.entrySet()) {
            String key = entry.getKey();
            Object exp = entry.getValue();
            Object rec = record.get(key);

            if (exp instanceof String) {
                assertTrue("Missing expected String value type for key: " + key, rec instanceof String);
                assertTrue("Record expected: "+exp+" for key: " + key, rec.equals(exp));
            }

            if (exp instanceof Map) {
                assertTrue("Missing expected Map value type for key: " + key, rec instanceof Map);
                compare((Map<String, Object>) exp, (Map<String, Object>) rec);                    
            }

            if (exp instanceof List) {
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
                    compareStringList((List<String>) ex, (List<String>) rc);
                }

                if (ex.get(0) instanceof Map) {
                    for (Object o : rc) {
                        assertTrue("Missing expected Map type for key: " + key, o instanceof Map);
                    }
                    compareMapList((List<Map<String, Object>>) ex, (List<Map<String, Object>>) rc);
                }
            }
        }
    }

    private void compareStringList(List<String> expected, List<String> record) {
        String[] exp = (String[]) expected.toArray();
        String[] rec = (String[]) record.toArray();
        Arrays.sort(exp);
        Arrays.sort(rec);
        assertArrayEquals(exp, rec);
    }

    private void compareMapList(List<Map<String, Object>> expected, List<Map<String, Object>> record) {
        Map<String, List<Object>> exp = aggregateMapListByKey(expected);
        Map<String, List<Object>> rec = aggregateMapListByKey(record);

        assertEquals("Error list size ", exp.size(), rec.size());
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

    private Map<String, List<Object>> aggregateMapListByKey(List<Map<String, Object>>  list) {
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

}
