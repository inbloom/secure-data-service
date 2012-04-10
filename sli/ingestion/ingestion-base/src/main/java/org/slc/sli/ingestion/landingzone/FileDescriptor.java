package org.slc.sli.ingestion.landingzone;

import java.io.Serializable;

/**
 * Describes a file and the Landing Zone it is in.
 *
 * @author okrook
 *
 * @param <T> Type that holds file information
 */
public class FileDescriptor<T> implements Serializable {

    private static final long serialVersionUID = -2800997090364423334L;

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
