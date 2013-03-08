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
package org.slc.sli.search.process;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import junit.framework.Assert;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slc.sli.search.config.IndexConfigStore;
import org.slc.sli.search.connector.SourceDatastoreConnector;
import org.slc.sli.search.connector.SourceDatastoreConnector.Tenant;
import org.slc.sli.search.entity.IndexEntity.Action;
import org.slc.sli.search.process.impl.ExtractorImpl;
import org.slc.sli.search.transform.IndexEntityConverter;
import org.slc.sli.search.util.MockDBCursorFactory;

/**
 * current student.json for mock data has 192 records
 *
 * @author tosako
 *
 */
public class ExtractorImplTest {

    private IndexConfigStore indexConfigStore;
    private IndexEntityConverter indexEntityConverter;

    private class MockExtractor extends ExtractorImpl {
        final HashSet<Action> actions = new HashSet<Action>();
        int numOfLines = 0;

        @Override
        protected void finishProcessing(String index, File outFile, Action action, List<File> producedFiles) {
            if (outFile != null) {
                producedFiles.add(outFile);
                try {
                    numOfLines += FileUtils.readLines(outFile).size();
                } catch (IOException e) {
                    throw new RuntimeException("Something with the extract file", e);
                }
                outFile.delete();
            }
            actions.add(action);
        }

        public HashSet<Action> getActions() {
            return actions;
        }

        public void reset() {
            actions.clear();
            numOfLines = 0;
        }
    };

    private final SourceDatastoreConnector connector = new SourceDatastoreConnector() {

        @Override
        public DBCursor getDBCursor(String collectionName, List<String> fields) {
            // get cursor from static file
            return MockDBCursorFactory.create(collectionName);
        }

        @Override
        public List<Tenant> getTenants() {
            return Arrays.asList(new Tenant[]{new Tenant("test", "test")});
        }

        @Override
        public void save(String collectionName, Object o) {
            // TODO Auto-generated method stub

        }

        @Override
        public <T> List<T> findAll(String collectionName, Class<T> entityClass) {
            // TODO Auto-generated method stub
            return null;
        }

		@Override
		public DBCursor getDBCursor(String collectionName, List<String> fields,
				DBObject query) {
			// TODO Auto-generated method stub
            return MockDBCursorFactory.create(collectionName);
		}

    };

    private final MockExtractor extractor = new MockExtractor();

    @Before
    public void init() throws IOException {
        extractor.init();
        extractor.setJobWaitTimeoutInMins(1);
        indexConfigStore = new IndexConfigStore("index-config-test.json");
        indexEntityConverter = new IndexEntityConverter();

        extractor.setIndexConfigStore(indexConfigStore);
        extractor.reset();
        extractor.setSourceDatastoreConnector(connector);
    }

    @After
    public void destroy() {
        extractor.destroy();
    }
/**
 * Test to count number of files and lines in each file
 * @throws Exception
 */
    @Test
    public void testFileCounts() throws Exception {
        // set max lines per file is 10
        extractor.setMaxLinePerFile(10);
        List<File> files = extractor.extractCollection(indexConfigStore.getConfig("student"), Action.INDEX, new Tenant("test", "test"));

        Assert.assertEquals(20, files.size());
        Assert.assertEquals(191, extractor.numOfLines);
    }

    @Test
    public void testAction() throws Exception {
        extractor.execute(Action.UPDATE);
        HashSet<Action> actions = extractor.getActions();
        Assert.assertEquals(1, actions.size());
        Assert.assertEquals(Action.UPDATE, actions.iterator().next());
    }

}
