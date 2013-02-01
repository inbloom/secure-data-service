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


package org.slc.sli.scaffold.semantics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adds elements to the existing html to include links to the generated 
 * database schema documents.
 * 
 * @author jstokes
 *
 */
public class ResourceDocumenter {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceDocumenter.class);
            
    private static final String LINK_HTML = "<a href=\"$LINK\">$TYPE</a>";
    private static final String PROP_FILE = "/resource_mapping.properties";
    private String baseUrl;
    private Properties props;
    
    /**
     * Replaces schema tags with links to schema in generated html documentation
     * @param generatedHtml
     */
    public void addResourceMerge(File generatedHtml) {
        String content = "";
        content = readFile(generatedHtml);
        readPropertiesFile();
  
        for (Entry<Object, Object> entry : props.entrySet()) {
            content = addResource(content, entry);
        }
        
        writeFile(content, generatedHtml);
    }
    
    protected void readPropertiesFile() {
        InputStream in = null;
        try {
            props = new Properties();
            in = ResourceDocumenter.class.getResourceAsStream(PROP_FILE);
            props.load(in);
            baseUrl = (String) props.get("base_url");
            props.remove("base_url");
            
        } catch (IOException e) {
            LOG.warn(e.getMessage());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
    
    /**
     * Helper function to write content to a file
     * @param content content to write
     * @param output output file
     */
    private void writeFile(String content, File output) {
        try {
            IOUtils.write(content, new FileOutputStream(output));
        } catch (FileNotFoundException e) {
            LOG.warn(e.getMessage());
        } catch (IOException e) {
            LOG.warn(e.getMessage());
        }
    }

    /**
     * Helper function to read content from a file
     * @param generatedHtml file to read from
     * @return the file as a string
     */
    protected String readFile(File generatedHtml) {
        String content = "";
        try {
            content = IOUtils.toString(new FileInputStream(generatedHtml));
        } catch (FileNotFoundException e) {
            LOG.warn(e.getMessage());
        } catch (IOException e) {
            LOG.warn(e.getMessage());
        }
        return content;
    }

    protected String addResource(String content, Entry<Object, Object> entry) {
        String value = (String) entry.getValue();
        String key = (String) entry.getKey();
        
        String link = (String) value.split(",")[0];
        String href = (String) value.split(",")[1];
        
        String newContent = content.replace("$$" + key + "$$", createLink(link, href));
        
        return newContent;
    }
    
    protected String createLink(String key, String value) {
        String link = "";
        
        link = LINK_HTML.replace("$LINK", baseUrl + value);
        link = link.replace("$TYPE", key);
        
        return link;
    }
    
    protected String getBaseUrl() {
        return baseUrl;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            return;
        }
        
        ResourceDocumenter resourceDoc = new ResourceDocumenter();
        resourceDoc.addResourceMerge(new File(args[0]));
    }
}
