package org.slc.sli.ingestion.landingzone;

import java.io.Serializable;

/**
 * Describes control file and the Landing Zone it is in.
 *
 * @author okrook
 */
public class ControlFileDescriptor extends FileDescriptor<ControlFile> implements Serializable {
    
    private static final long serialVersionUID = 8497511830606948940L;

    public ControlFileDescriptor(ControlFile fileItem, LandingZone landingZone) {
        super(fileItem, landingZone);
    }
}
