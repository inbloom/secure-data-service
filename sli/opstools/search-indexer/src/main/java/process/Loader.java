package process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.lucene.util.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slc.sli.search.entity.IndexEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Loader implements FileAlterationListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private static final String DEFAULT_DROP_OFF_DIR = "inbox";
    private static final int DEFAULT_INTERVAL_SEC = 3;
    private static final int DEFAULT_EXECUTOR_THREADS = 5;
    
    @Autowired
    private Indexer indexer;
    
    @Autowired(required=false)
    private String inboxDir = DEFAULT_DROP_OFF_DIR;
    
    @Autowired(required=false)
    private long intervalSec = DEFAULT_INTERVAL_SEC;
    
    private FileAlterationMonitor monitor;
    
    private ExecutorService executor;
    private int executorThreads = DEFAULT_EXECUTOR_THREADS;
    
    public Loader() {
        monitor = new FileAlterationMonitor(TimeUnit.SECONDS.toMillis(intervalSec));
        FileAlterationObserver observer = new FileAlterationObserver(new File(inboxDir));
        monitor.addObserver(observer);
        observer.addListener(this);
    }
    
    public void init() throws Exception {
        
        // create thread pool to process files
        executor = Executors.newFixedThreadPool(executorThreads);
        
        // watch directory for files
        monitor.start();
    }
    
    public void destroy()  throws Exception{
        monitor.stop();
        executor.shutdown();
    }
    
    private class LoaderWorker implements Runnable {
        
        File inFile;
        
        LoaderWorker(File inFile) {
            this.inFile = inFile;
        }
        
        public void run() {
        
            // read records from file
            BufferedReader br = null;
            String id, type, index, entity;
        
            try {
                // get tenant and entity type from file name
                String fileName = inFile.getName();
                index = fileName.substring(0, fileName.indexOf('-'));
                type = fileName.substring(fileName.indexOf('-')+1, fileName.indexOf('.'));
            
                br = new BufferedReader(new FileReader(inFile));
                while ((entity = br.readLine()) != null) {
                
                    // create entity object
                    ObjectMapper mapper = new ObjectMapper();
                
                    Map<String, Object> entityMap = mapper.readValue(
                            entity, 
                            new TypeReference<Map<String, Object>>() {});
                
                    id = (String) entityMap.get("_id");
                    //String body = mapper.writeValueAsString((Map<String, Object>) entityMap.get("body"));
                    IndexEntity indexEntity = new IndexEntity(index, type, id, (Map<String, Object>) entityMap.get("body"));
                
                    indexer.index(type, indexEntity);
                
                }
                indexer.flush();
            } catch (Exception e) {
                logger.error("Error loading from file", e);
            }
            finally {
            
                try {
                    IOUtils.close(br);
                    inFile.delete();
                    logger.info("Done processing file: " + inFile.getName());
                } catch (IOException e) {
                    logger.error("Error closing file", e);
                }
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
        logger.info("Processing file: " + inFile.getName());
        executor.execute(new LoaderWorker(inFile));
    }

    public void onFileDelete(File arg0) {
    }

    public void onStart(FileAlterationObserver arg0) {
    }

    public void onStop(FileAlterationObserver arg0) {
    }
    
}