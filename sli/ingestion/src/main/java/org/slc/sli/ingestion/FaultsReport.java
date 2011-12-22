package org.slc.sli.ingestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * Velidation Report callback based on Faults collection.
 *
 * @author okrook
 *
 */
public class FaultsReport implements ErrorReport {
    private List<Fault> faults = new ArrayList<Fault>();

    public FaultsReport() {
    }

    @Override
    public void fatal(String message, Object sender) {
        error(message, sender);
    }
    @Override
    public void error(String message, Object sender) {
        this.faults.add(Fault.createError(message));
    }

    @Override
    public void warning(String message, Object sender) {
        this.faults.add(Fault.createWarning(message));
    }

    public List<Fault> getFaults() {
        return Collections.unmodifiableList(this.faults);
    }

    public boolean hasErrors() {
        for (Fault f : faults) {
            if (f.isError())
                return true;
        }
        return false;
    }
}
