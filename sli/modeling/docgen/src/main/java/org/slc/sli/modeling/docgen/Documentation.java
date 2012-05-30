package org.slc.sli.modeling.docgen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Documentation<TYPE> {
    
    private final List<Domain<TYPE>> domains;
    
    public Documentation(final List<Domain<TYPE>> domains) {
        if (domains == null) {
            throw new NullPointerException("domains");
        }
        this.domains = Collections.unmodifiableList(new ArrayList<Domain<TYPE>>(domains));
    }
    
    public List<Domain<TYPE>> getDomains() {
        return domains;
    }
}
