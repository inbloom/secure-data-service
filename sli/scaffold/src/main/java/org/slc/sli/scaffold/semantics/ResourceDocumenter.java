package org.slc.sli.scaffold.semantics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

/**
 * Adds elements to the existing html to include links to the generated 
 * database schema documents.
 * 
 * @author jstokes
 *
 */
public class ResourceDocumenter {
            
    private static final String LINK_HTML = "<a href=\"$LINK\">$TYPE</a>";
    private static final String PROP_FILE = "/resource_mapping.properties";
    private static final String BASE_URL = 
            "http://ec2-107-20-87-141.compute-1.amazonaws.com:8080/view/API/job/domain/ws/sli/common/domain/Documentation/html/";
    private static Properties props;
    
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
    
    private void readPropertiesFile() {
        try {
            props = new Properties();
            props.load(ResourceDocumenter.class.getResourceAsStream(PROP_FILE));
            
        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper function to read content from a file
     * @param generatedHtml file to read from
     * @return the file as a string
     */
    private String readFile(File generatedHtml) {
        String content = "";
        try {
            content = IOUtils.toString(new FileInputStream(generatedHtml));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    private String addResource(String content, Entry<Object, Object> entry) {
        String key = (String) entry.getKey();
        content = content.replace("$$" + key + "$$", createLink(key, (String) entry.getValue()));
        
        return content;
    }
    
    private String createLink(String key, String value) {
        String link = "";
        
        link = LINK_HTML.replace("$LINK", BASE_URL + value);
        link = link.replace("$TYPE", key);
        
        return link;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            return;
        }
        
        ResourceDocumenter resourceDoc = new ResourceDocumenter();
        resourceDoc.addResourceMerge(new File(args[0]));
    }
}
