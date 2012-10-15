package org.slc.sli.search;

import java.io.File;
import java.io.IOException;

import org.slc.sli.search.util.AppLock;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Launcher {

    private static final String DEFAULT_LOCK_DIR = "data";
    private static final String LOCK_FILE = "indexer.lock";
    
    public static final void main(String[] args) throws IOException {
        
        new AppLock(getLockLocation());
        new ClassPathXmlApplicationContext("application-context.xml");
    }
    
    private static String getLockLocation() {
        return System.getProperties().getProperty("lock.dir", DEFAULT_LOCK_DIR) + File.separator + LOCK_FILE;
    }
}
