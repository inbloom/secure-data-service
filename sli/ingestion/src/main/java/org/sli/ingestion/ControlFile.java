package org.sli.ingestion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControlFile {

    // simple structure to hold together items named in the control file
    public static class ControlFileItem {
        
        protected FileFormat fileFormat;  // @todo should be enum
        protected FileType fileType;  // @todo should be enum
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
    protected List<ControlFileItem> items = new ArrayList<ControlFileItem>();
    protected String fileContent;
    
    public void parseFile(File file) throws IOException {
        this.file = file;
        Scanner scanner = new Scanner(file);
        Pattern pattern = Pattern.compile("^([^\\s]+)\\,([^\\s]+)\\,([^,]+)\\,(\\w+)\\s*$");
        Matcher matcher;
        String line;
        FileFormat fileFormat;
        FileType fileType;
        try {
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    fileFormat = FileFormat.findByCode(matcher.group(1));
                    fileType = FileType.findByNameAndFormat(matcher.group(2), fileFormat);
                    items.add(new ControlFileItem(fileFormat, fileType, matcher.group(3), matcher.group(4)));
                } else {
                    throw new RuntimeException("invalid control file entry: " + line);
                }
            }
        } finally {
            scanner.close();
        }
    }
    
    public List<ControlFileItem> getItems() {
        return this.items;
    }
    
}
