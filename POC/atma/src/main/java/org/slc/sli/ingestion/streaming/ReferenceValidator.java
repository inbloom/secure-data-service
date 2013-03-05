package org.slc.sli.ingestion.streaming;

import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

public interface ReferenceValidator {
    public void addForValidation(String elementName, String value);
    public Set<Pair<String, String>> getRemainingReferences();
}
