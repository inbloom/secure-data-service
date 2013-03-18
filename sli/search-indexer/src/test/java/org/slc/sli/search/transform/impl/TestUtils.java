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
package org.slc.sli.search.transform.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * Utils class for tests
 * 
 * @author nbrown
 *
 */
public class TestUtils {

    public static DBCursor buildMockCursor(final DBObject obj) {
        DBCursor cursor = mock(DBCursor.class);
        when(cursor.hasNext()).thenReturn(true, false);
        when(cursor.next()).thenReturn(obj);
        return cursor;
    }

    public static DBCursor buildEmptyMockCursor() {
        DBCursor cursor = mock(DBCursor.class);
        when(cursor.hasNext()).thenReturn(false);
        when(cursor.next()).thenReturn(null);
        return cursor;
    }

    static BaseMatcher<DBObject> buildIdMatcher(final String id) {
        return new BaseMatcher<DBObject>() {
            
            @Override
            public boolean matches(Object arg0) {
                return id.equals(((DBObject) arg0).get("_id"));
            }
            
            @Override
            public void describeTo(Description arg0) {
            }
        };
    }
    
}
