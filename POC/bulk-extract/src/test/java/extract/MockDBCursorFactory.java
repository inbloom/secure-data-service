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
package extract;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.LinkedList;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import org.apache.commons.io.IOUtils;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class for Mocking Mongo Template
 * 
 * @author tosako
 * 
 */
public final class MockDBCursorFactory {
    private static final Logger LOG = LoggerFactory.getLogger(MockDBCursorFactory.class);

    private MockDBCursorFactory() {
    }

    public static DBCursor create(String collection) {
        return (new MockDBCursorFactory()).createDBCursor(collection);
    }

    /**
     * Create DBCollectionWrapper.
     * Since this is mocking data, Date source comes from file in resource directory.
     * If a file does not exist, it returns null object.
     * 
     * @param collection
     * @return
     */
    private DBCursor createDBCursor(String collection) {
        DBCursor cursor = mock(DBCursor.class);
        final LinkedList<DBObject> jsonArray = new LinkedList<DBObject>();

        BufferedReader br = null;
        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource(collection + ".json");
            if (url != null) {
                File jsonFile = new File(url.getFile());
                br = new BufferedReader(new FileReader(jsonFile));
                String line;

                while ((line = br.readLine()) != null) {
                    DBObject bson = (DBObject) JSON.parse(line);
                    jsonArray.add(bson);
                }
            }
        } catch (Exception e) {
            LOG.error("Error extracting " + collection, e);
        } finally {
            IOUtils.closeQuietly(br);
        }

        when(cursor.hasNext()).thenAnswer(new Answer<Boolean>() {
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return !jsonArray.isEmpty();
            }
        });

        when(cursor.next()).thenAnswer(new Answer<DBObject>() {
            public DBObject answer(InvocationOnMock invocation) throws Throwable {
                return jsonArray.remove();
            }
        });
        return cursor;
    }
}
