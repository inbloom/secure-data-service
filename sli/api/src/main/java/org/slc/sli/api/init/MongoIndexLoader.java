package org.slc.sli.api.init;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Loads scripts located in api/src/main/resources/indexDefinitions that hold index constraints for
 * Mongo to enforce. Modeled after AggregationLoader class.
 * 
 * When adding an index for a collection, add a folder corresponding to the name of the collection
 * in the indexDefinitions folder, and create a javascript file that contains the index definition
 * to be loaded into Mongo.
 * 
 * @author shalka
 * 
 */
public class MongoIndexLoader {
    
    private static final Logger LOG = LoggerFactory.getLogger(MongoIndexLoader.class);
    private static final String[] MONGO_INDEX_FOLDERS = new String[] { "attendanceEvent" + File.separator };
    private static final FileFilter JAVASCRIPT_FILE_FILTER = new FileFilter() {
        public boolean accept(File pathname) {
            return pathname.getName().endsWith(".js");
        }
    };
    
    private File basePath;
    private MongoTemplate template;
    
    public MongoIndexLoader(MongoTemplate template, URI path) {
        this.basePath = new File(path);
        this.template = template;
        loadJavascriptFolders();
    }
    
    /**
     * Loads all javascipt (*.js) files from all definition paths into Mongo's (global) memory.
     * Returns true if successful, false otherwise.
     * 
     * @return true if all definition folders process without producing any errors
     */
    public boolean loadJavascriptFolders() {
        boolean allFilesLoaded = true;
        
        for (String definitionFolder : MongoIndexLoader.MONGO_INDEX_FOLDERS) {
            String path = basePath.getAbsolutePath() + File.separator + definitionFolder;
            allFilesLoaded = (this.loadJavascriptFolder(path) && allFilesLoaded);
        }
        
        return allFilesLoaded;
    }
    
    /**
     * Loads all javascipt (*.js) files from a specific path/directory into Mongo's (global) memory.
     * Returns true if successful, false otherwise.
     * 
     * @param path
     *            directory where .js files should be loaded
     * @return true if all files found were loaded successfully
     */
    public boolean loadJavascriptFolder(String path) {
        
        boolean allFilesLoaded = true;
        File[] javascriptFiles = new File(path).listFiles(MongoIndexLoader.JAVASCRIPT_FILE_FILTER);
        
        if (javascriptFiles != null) {
            for (File file : javascriptFiles) {
                allFilesLoaded = (this.loadJavascriptFile(file) && allFilesLoaded);
            }
        }
        
        return allFilesLoaded;
    }
    
    /**
     * Loads a specific javascript file into Mongo's javascript shell.
     * 
     * @param file
     *            reference to file to be opened and "piped" into javascript shell
     * @return true if successful, false otherwise
     */
    public boolean loadJavascriptFile(File file) {
        
        LOG.debug("Loading index file: " + file.getAbsolutePath());
        
        try {
            StringBuffer fileData = new StringBuffer("");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String temp = br.readLine();
            while (temp != null) {
                fileData.append(temp + "\n");
                temp = br.readLine();
            }
            br.close();
            fileData.append("\n");
            
            return template.executeCommand("{\"$eval\":\"" + StringEscapeUtils.escapeJava(fileData.toString()) + "\"}")
                    .ok();
        } catch (IOException ioe) {
            LOG.debug("Failed to load index file: " + file.getAbsolutePath());
            return false;
        }
    }
}
