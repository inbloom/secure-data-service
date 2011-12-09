package org.slc.sli.ingestion.landingzone;


/**
 * Describes a file and the Landing Zone it is in.
 *
 * @author okrook
 *
 * @param <T> Type that holds file information
 */
public class FileDescriptor<T> {

    private T fileItem;
    private LandingZone landingZone;

    public FileDescriptor(T fileItem, LandingZone landingZone) {
        this.fileItem = fileItem;
        this.landingZone = landingZone;
    }

    public T getFileItem() {
        return fileItem;
    }

    public LandingZone getLandingZone() {
        return landingZone;
    }
}
