package org.slc.sli.ingestion.landingzone;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;

public class ControlFile {

    // simple structure to hold together items named in the control file
    public static class FileEntry {
        
        protected FileFormat fileFormat;  
        protected FileType fileType;  
        protected String fileName;
        protected String checksum;
        
        public FileEntry(FileFormat fileFormat, FileType fileType,
                String fileName, String checksum) {
            this.fileFormat = fileFormat;
            this.fileType = fileType;
            this.fileName = fileName;
            this.checksum = checksum;
        }
        
    }
    
    protected File file;
    protected List<FileEntry> fileEntries = new ArrayList<FileEntry>();
    protected Properties configProperties = new Properties();
    
    public static ControlFile parse(File file) throws IOException {
        
    	Scanner scanner = new Scanner(file);
        Pattern fileItemPattern = Pattern.compile("^([^\\s]+)\\,([^\\s]+)\\,([^,]+)\\,(\\w+)\\s*$");
        Matcher fileItemMatcher;
        Pattern configItemPattern = Pattern.compile("^@(.*)$");
        Matcher configItemMatcher;
        String line;
        FileFormat fileFormat;
        FileType fileType;

        Properties configProperties = new Properties();
        ArrayList<FileEntry> fileEntries = new ArrayList<FileEntry>();

        try {
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                fileItemMatcher = fileItemPattern.matcher(line);
                if (fileItemMatcher.matches()) {
                    fileFormat = FileFormat.findByCode(fileItemMatcher.group(1));
                    fileType = FileType.findByNameAndFormat(fileItemMatcher.group(2), fileFormat);
                    fileEntries.add(new FileEntry(fileFormat, fileType, fileItemMatcher.group(3), fileItemMatcher.group(4)));
                    continue;
                } 

                configItemMatcher = configItemPattern.matcher(line);
                if (configItemMatcher.matches()) {
                	configProperties.load(new ByteArrayInputStream(configItemMatcher.group(1).getBytes()));
                	//System.err.println("found configItem: ["+configItemMatcher.group(1)+"]");
                	continue;
                } 

                // blank lines are ignored silently, but stray marks are not
                if (line.trim().length()>0) {
                	// line was not parseable
                	// TODO fault or custom exception?
                	throw new RuntimeException("invalid control file entry: " + line);
                }
            }
            
            return new ControlFile(file, fileEntries, configProperties);
            
        } finally {
            scanner.close();
        }
    }
    
	public List<FileEntry> getFileEntries() {
        return this.fileEntries;
    }
    
	/**
	 * @param file
	 * @param fileEntries
	 * @param configProperties
	 */
	protected ControlFile(File file, List<FileEntry> fileEntries,
			Properties configProperties) {
		this.file = file;
		this.fileEntries = fileEntries;
		this.configProperties = configProperties;
	}
	
}
