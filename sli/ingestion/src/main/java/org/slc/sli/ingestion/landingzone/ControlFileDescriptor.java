package org.slc.sli.ingestion.landingzone;


/**
 * Describes control file and the Landing Zone it is in.
 *
 * @author okrook
 */
public class ControlFileDescriptor extends FileDescriptor<ControlFile> {

    public ControlFileDescriptor(ControlFile fileItem, LandingZone landingZone) {
        super(fileItem, landingZone);
    }
}
