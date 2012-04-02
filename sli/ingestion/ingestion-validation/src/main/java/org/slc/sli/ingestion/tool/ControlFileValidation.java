package org.slc.sli.ingestion.tool;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.landingzone.BatchJobAssembler;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;

/**
 * ControlFileValidation is used to validate the control file
 *
 * @author tke
 *
 */
public class ControlFileValidation {

    BatchJobAssembler assembler;
    /**
     * Validate the control file specified by control file descriptor
     *
     * @param control
     *            file descriptor
     * @param the
     *            batch job
     */
    void validate(ControlFileDescriptor fileDesc, BatchJob job){
        assembler.populateJob(fileDesc, job);
    }

    public void setAssembler(BatchJobAssembler assembler){
        this.assembler = assembler;
    }

    public BatchJobAssembler getAssembler(){
        return assembler;
    }

}
