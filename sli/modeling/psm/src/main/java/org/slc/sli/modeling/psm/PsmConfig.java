package org.slc.sli.modeling.psm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PsmConfig<TYPE> {

    private final List<PsmDocument<TYPE>> documents;

    public PsmConfig(final List<PsmDocument<TYPE>> documents) {
        if (documents == null) {
            throw new NullPointerException("documents");
        }
        this.documents = Collections.unmodifiableList(new ArrayList<PsmDocument<TYPE>>(documents));
    }

    public List<PsmDocument<TYPE>> getDocuments() {
        return documents;
    }
}
