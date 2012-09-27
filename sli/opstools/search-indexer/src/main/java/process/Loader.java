package process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.lucene.util.IOUtils;
import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class Loader implements FileAlterationListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private static final String DEFAULT_DROP_OFF_DIR = "inbox";
    private static final int DEFAULT_INTERVAL_SEC = 3;
    
    @Autowired
    @Qualifier("entityEncryption")
    private EntityEncryption encrypt;
    
    @Autowired
    private Indexer indexer;
    
    @Autowired(required=false)
    private String inboxDir = DEFAULT_DROP_OFF_DIR;
    
    @Autowired(required=false)
    private long intervalSec = DEFAULT_INTERVAL_SEC;
    
    private FileAlterationMonitor monitor;
    
    public Loader() {
        monitor = new FileAlterationMonitor(TimeUnit.SECONDS.toMillis(intervalSec));
        FileAlterationObserver observer = new FileAlterationObserver(new File(inboxDir));
        monitor.addObserver(observer);
        observer.addListener(this);
    }
    
    public void init() throws Exception {
        // watch directory for files
        monitor.start();
    }
    
    public void destroy()  throws Exception{
        monitor.stop();
    }
    
    private void loadFromFileAndIndex(File inFile) {
        
        // read records from file
        BufferedReader br = null;
        String id, type, index, entity;
        
        try {
            br = new BufferedReader(new FileReader(inFile));
            while ((id = br.readLine()) != null) {
                //TODO: parse out type, id, tenant from filename/entity body
                type = br.readLine();
                index = br.readLine();
                entity = br.readLine();
                indexer.index(type, id, index, entity);
            }
            indexer.flush();
        } catch (Exception e) {
            logger.error("Error loading from file", e);
        }
        finally {
            
            try {
                IOUtils.close(br);
            } catch (IOException e) {
                logger.error("Error closing file", e);
            }
        }
    }

    public void onDirectoryChange(File arg0) {
    }

    public void onDirectoryCreate(File arg0) {
    }

    public void onDirectoryDelete(File arg0) {
    }

    public void onFileChange(File arg0) {
    }

    public void onFileCreate(File inFile) {
        // TODO: make sure file is not being written to still
        loadFromFileAndIndex(inFile);
        inFile.delete();
    }

    public void onFileDelete(File arg0) {
    }

    public void onStart(FileAlterationObserver arg0) {
    }

    public void onStop(FileAlterationObserver arg0) {
    }
    
}