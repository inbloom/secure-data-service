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
package org.slc.sli.search.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import org.apache.commons.io.IOUtils;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Factory class for Mocking Mongo Template
 * 
 * @author tosako
 * 
 */
public class MockDBCursorFactory {

    private LinkedList<DBObject> jsonArray = new LinkedList<DBObject>();

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
        BufferedReader br = null;
        File jsonFile = new File(DBCollection.class.getClassLoader().getResource(collection + ".json").getFile());

        if (jsonFile.exists()) {
            try {
                br = new BufferedReader(new FileReader(jsonFile));
                String line;

                while ((line = br.readLine()) != null) {
                    DBObject bson = (DBObject) JSON.parse(line);
                    jsonArray.add(bson);
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
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(br);
            }
        }
        return cursor;
    }
}
