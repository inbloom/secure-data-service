package org.slc.sli.ingestion.landingzone;

import java.util.List;

import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.validation.ValidationReport;

/**
 * Velidation Report callback based on Faults collection.
 *
 * @author okrook
 *
 */
public class ValidationFaultReport implements ValidationReport {
    List<Fault> faults;

    public ValidationFaultReport(List<Fault> faults) {
        this.faults = faults;
    }

    @Override
    public void fail(String message, Object sender) {
        this.faults.add(Fault.createError(message));
    }
}
