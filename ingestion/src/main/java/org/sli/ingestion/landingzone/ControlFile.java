package org.sli.ingestion.landingzone;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sli.ingestion.FileFormat;
import org.sli.ingestion.FileType;

public class ControlFile {

    // simple structure to hold together items named in the control file
    public static class ControlFileItem {
        
        protected FileFormat fileFormat;  
        protected FileType fileType;  
        protected String fileName;
        protected String checksum;
        
        public ControlFileItem(FileFormat fileFormat, FileType fileType,
                String fileName, String checksum) {
            this.fileFormat = fileFormat;
            this.fileType = fileType;
            this.fileName = fileName;
            this.checksum = checksum;
        }
        
    }
    
    protected File file;
    protected List<ControlFileItem> fileItems = new ArrayList<ControlFileItem>();
    protected Properties configProperties = new Properties();
    protected String fileContent;
    
    public void parseFile(File file) throws IOException {
        this.file = file;
        Scanner scanner = new Scanner(file);
        Pattern fileItemPattern = Pattern.compile("^([^\\s]+)\\,([^\\s]+)\\,([^,]+)\\,(\\w+)\\s*$");
        Matcher fileItemMatcher;
        Pattern configItemPattern = Pattern.compile("^@(.*)$");
        Matcher configItemMatcher;
        String line;
        FileFormat fileFormat;
        FileType fileType;
        try {
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                fileItemMatcher = fileItemPattern.matcher(line);
                if (fileItemMatcher.matches()) {
                    fileFormat = FileFormat.findByCode(fileItemMatcher.group(1));
                    fileType = FileType.findByNameAndFormat(fileItemMatcher.group(2), fileFormat);
                    fileItems.add(new ControlFileItem(fileFormat, fileType, fileItemMatcher.group(3), fileItemMatcher.group(4)));
                    continue;
                } 

                configItemMatcher = configItemPattern.matcher(line);
                if (configItemMatcher.matches()) {
                	configProperties.load(new ByteArrayInputStream(configItemMatcher.group(1).getBytes()));
                	//System.err.println("found configItem: ["+configItemMatcher.group(1)+"]");
                	continue;
                } 

                // line was not parseable
                throw new RuntimeException("invalid control file entry: " + line);
            }
        } finally {
            scanner.close();
        }
    }
    
    public List<ControlFileItem> getFileItems() {
        return this.fileItems;
    }
    
}
