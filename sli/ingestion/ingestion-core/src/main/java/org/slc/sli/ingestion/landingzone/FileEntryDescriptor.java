package org.slc.sli.ingestion.landingzone;


/**
 * Describes a file and the Landing Zone it is in.
 *
 * @author okrook
 */
public class FileEntryDescriptor extends FileDescriptor<IngestionFileEntry> {

    private static final long serialVersionUID = 4327439876894214016L;

    public FileEntryDescriptor(IngestionFileEntry fileItem, LandingZone landingZone) {
        super(fileItem, landingZone);
    }
}
