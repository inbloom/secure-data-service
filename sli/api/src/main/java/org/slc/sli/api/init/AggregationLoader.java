package org.slc.sli.api.init;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * On startup loads all javascript function definitions into Mongo's memory.
 * These functions are used during map/reduce (aggregation) calculations.
 * 
 * @author Kevin Myers kmyers@wgen.net
 * 
 */
public class AggregationLoader {
    
    //ability to write to DEBUG output
    private static final Logger LOG = LoggerFactory.getLogger(AggregationLoader.class);
    
    //where javascript aggregation function definitions are stored 
    private static final String DEFINITIONS_PATH = 
            "src" + File.separator
            + "main" + File.separator
            + "resources" + File.separator
            + "aggregationDefinitions" + File.separator + "";
    
    //folders containing various functions inside the main definition folder
    private static final String[] DEFINITIONS_FOLDERS = new String[] {
        "cleanupFunctions" + File.separator, 
        "finalizeFunctions" + File.separator, 
        "mapFunctions" + File.separator, 
        "reduceFunctions" + File.separator, 
        "other" + File.separator
    };
    
    //ability to view only .js files in a directory as opposed to all files and directories
    private static final FileFilter JAVASCRIPT_FILE_FILTER = new FileFilter() {
        public boolean accept(File pathname) {
            return pathname.getName().endsWith(".js");
        }
    };
    
    //interface to mongo
    private MongoTemplate template;
   
    /**
     * Constructor. Loads all definitions from file system into Mongo.
     */
    public AggregationLoader(MongoTemplate template) {
    	this.template = template;
        this.loadJavascriptFolders();
    }
    
    /**
     * Loads all javascipt (*.js) files from all definition paths into Mongo's (global) memory. 
     * Returns true if successful, false otherwise.
     *  
     * @return true if all definition folders process without producing any errors
     */
    public boolean loadJavascriptFolders() {
        
        boolean allFilesLoaded = true;
        
        //for each known folder of aggregation definitions
        for (String definitionFolder : AggregationLoader.DEFINITIONS_FOLDERS) {
            //construct path to definition folder
            String path = AggregationLoader.DEFINITIONS_PATH + definitionFolder;
            //load all definitions found at the specified path
            allFilesLoaded = (this.loadJavascriptFolder(path) && allFilesLoaded);
        }
        
        return allFilesLoaded;
    }
    
    /**
     * Loads all javascipt (*.js) files from a specific path/directory into Mongo's (global) memory. 
     * Returns true if successful, false otherwise.
     * 
     * 
     * @param path directory where .js files should be loaded
     * @return true if all files found were loaded successfully
     */
    public boolean loadJavascriptFolder(String path) {
        
        boolean allFilesLoaded = true;
        
        //find folder (potentially) containing javascript to be loaded into mongo
        File[]javascriptFiles = new File(path).listFiles(AggregationLoader.JAVASCRIPT_FILE_FILTER);
        
        //can be null if directory does not exist
        if (javascriptFiles != null) {
            //load each javascript file
            for (File file : javascriptFiles) {
                //record "false" if file was not loaded
                allFilesLoaded = (this.loadJavascriptFile(file) && allFilesLoaded);
            }
        }
        
        return allFilesLoaded;
    }
    
    
    /**
     * Loads a specific javascript file into Mongo's javascript shell.
     * 
     * @param file reference to file to be opened and "piped" into javascript shell
     * @return true if successful, false otherwise
     */
    public boolean loadJavascriptFile(File file) {
        
        LOG.debug("Loading definition file: " + file.getAbsolutePath());
        
        //file IO can throw IOException(s)
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
            
            //tell mongo to execute what was just read from the file
            //return OK status from command execution
            return template.executeCommand("{\"$eval\":\"" + StringEscapeUtils.escapeJava(fileData.toString()) + "\"}").ok();
        } catch (IOException ioe) {
            LOG.debug("Failed to load definition file: " + file.getAbsolutePath());
            return false;
        }
    }
}
