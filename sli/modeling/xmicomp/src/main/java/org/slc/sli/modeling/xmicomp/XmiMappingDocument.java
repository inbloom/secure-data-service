package org.slc.sli.modeling.xmicomp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class XmiMappingDocument {

    private final XmiMappingModel lhsModel;
    private final XmiMappingModel rhsModel;
    private final List<XmiMapping> mappings;

    public XmiMappingDocument(final XmiMappingModel lhsModel, final XmiMappingModel rhsModel,
            final List<XmiMapping> mappings) {
        if (lhsModel == null) {
            throw new NullPointerException("lhsModel");
        }
        if (rhsModel == null) {
            throw new NullPointerException("rhsModel");
        }
        this.lhsModel = lhsModel;
        this.rhsModel = rhsModel;
        this.mappings = Collections.unmodifiableList(new ArrayList<XmiMapping>(mappings));
    }

    public XmiMappingModel getLhsModel() {
        return lhsModel;
    }

    public XmiMappingModel getRhsModel() {
        return rhsModel;
    }

    public List<XmiMapping> getMappings() {
        return mappings;
    }
}
