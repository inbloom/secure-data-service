package org.slc.sli.modeling.psm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PsmConfig<TYPE> {
    
    private final List<PsmClassType<TYPE>> classTypes;
    
    public PsmConfig(final List<PsmClassType<TYPE>> classTypes) {
        if (classTypes == null) {
            throw new NullPointerException("classTypes");
        }
        this.classTypes = Collections.unmodifiableList(new ArrayList<PsmClassType<TYPE>>(classTypes));
    }
    
    public List<PsmClassType<TYPE>> getClassTypes() {
        return classTypes;
    }
}
