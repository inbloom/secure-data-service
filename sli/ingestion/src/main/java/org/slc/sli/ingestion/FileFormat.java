package org.slc.sli.ingestion;

public enum FileFormat {
    
    EDFI_XML("edfi-xml", "xml"), 
    CSV("csv", "csv");
    
    private final String code;
    private final String extension;
    
    FileFormat(String code, String extension) {
        this.code = code;
        this.extension = extension;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getExtension() {
        return extension;
    }
    
    public static FileFormat findByCode(String code) {
        for (FileFormat ff : FileFormat.values()) {
            if (ff.getCode().equals(code)) {
                return ff;
            }
        }
        return null;
    }
    
}
