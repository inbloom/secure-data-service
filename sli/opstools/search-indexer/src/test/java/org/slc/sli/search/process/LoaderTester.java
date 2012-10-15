package org.slc.sli.search.process;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slc.sli.search.config.IndexConfigStore;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.process.impl.LoaderImpl;
import org.slc.sli.search.transform.IndexEntityConverter;
import org.slc.sli.search.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoaderTester {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final LoaderImpl loader = new LoaderImpl() {
        @Override
        public void archive(File inFile) {
            mon.release();
        }
    };
    private final MockIndexer indexer = new MockIndexer();
    
    private Semaphore mon;
    
    private final IndexEntityConverter indexConverter = new IndexEntityConverter();

    private class MockIndexer implements Indexer {
        private final List<IndexEntity> entities = new ArrayList<IndexEntity>();
        public void reset() {
            entities.clear();
        }
        public void index(IndexEntity entity) {   
            logger.info("indexing " + entity);
            entities.add(entity);
        }
        public List<IndexEntity> getEntities() {
            return entities;
        }
    };
    
    private final File inbox = new File(Constants.DEFAULT_INBOX_DIR);
    
    @Before
    public void setup() throws Exception {
        loader.setIndexer(indexer);
        loader.setPollIntervalMillis(10);
        indexConverter.setDecrypt(false);
        indexConverter.setIndexConfigStore(new IndexConfigStore("index-config-test.json"));
        loader.setIndexEntityConverter(indexConverter);
        indexer.reset();
        
        FileUtils.deleteDirectory(inbox);
        inbox.mkdir();
        loader.init();
    }
    
    @After
    public void destroy() throws Exception {
        loader.destroy();
    }
    
    private void setNumOfFilesToWait(int n) throws Exception {
        mon = new Semaphore(n);
        waitForFiles(n);
    }
    
    private void waitForFiles(int n) throws Exception {
        mon.tryAcquire(n, 1, TimeUnit.SECONDS);
    }
    
    @Test
    public void testLoadFile() throws Exception {
        File file = new File(getClass().getResource("/testLoad.json").getFile());
        setNumOfFilesToWait(1);
        loader.processFile(file);
        waitForFiles(1);
        Assert.assertEquals(3, indexer.getEntities().size());
        Assert.assertEquals("another", indexer.getEntities().get(0).getId());
        Assert.assertEquals("student", indexer.getEntities().get(1).getType());
        Assert.assertEquals("tenant", indexer.getEntities().get(2).getIndex());
    }
    
    @Test
    public void testLoadTwoFiles() throws Exception {
        File file = new File(getClass().getResource("/testLoad.json").getFile());
        File file1 = new File(getClass().getResource("/testLoad2.json").getFile());
        
        setNumOfFilesToWait(2);
        FileUtils.copyFile(file, new File(inbox, file.getName()));
        FileUtils.copyFile(file1, new File(inbox, file1.getName()));
        if (!new File(inbox, file.getName()).exists() || !new File(inbox, file1.getName()).exists()) {
            Assert.fail("Unable to get test files");
        }
        waitForFiles(2);
        Assert.assertEquals(6, indexer.getEntities().size());
    }
}
