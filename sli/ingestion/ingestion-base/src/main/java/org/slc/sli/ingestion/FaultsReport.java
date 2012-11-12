/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.ingestion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * Validation Report callback based on Faults collection.
 *
 * @author okrook
 *
 */
public class FaultsReport implements Serializable, ErrorReport {

    private static final long serialVersionUID = 2190485796418439710L;

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

    @Override
    public void fatal(String message, String resourceId, Object sender) {
        error(message, sender);
    }

    @Override
    public void error(String message, String resourceId, Object sender) {
        error(message, sender);
    }

    @Override
    public void warning(String message, String resourceId, Object sender) {
        warning(message, sender);
    }

    public List<Fault> getFaults() {
        return Collections.unmodifiableList(this.faults);
    }

    public synchronized void append(FaultsReport addFaultsReport) {
        this.faults.addAll(addFaultsReport.getFaults());
    }

    @Override
    public boolean hasErrors() {
        for (Fault f : faults) {
            if (f.isError()) {
                return true;
            }
        }
        return false;
    }
}
