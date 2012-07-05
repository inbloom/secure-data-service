package org.slc.sli.modeling.xmicomp;

public final class XmiMapping {
    private final XmiFeature lhs;
    private final XmiFeature rhs;
    private final XmiMappingStatus status;
    private final String comment;

    public XmiMapping(final XmiFeature lhs, final XmiFeature rhs, final XmiMappingStatus status, final String comment) {
        if (null == status) {
            throw new NullPointerException("status");
        }
        if (null == comment) {
            throw new NullPointerException("comment");
        }
        this.lhs = lhs;
        this.rhs = rhs;
        this.status = status;
        this.comment = comment;
    }

    public XmiFeature getLhsFeature() {
        return lhs;
    }

    public XmiFeature getRhsFeature() {
        return rhs;
    }

    public XmiMappingStatus getStatus() {
        return status;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return String.format("{lhs : %s, rhs : %s, status : %s, comment : %s}", lhs, rhs, status, comment);
    }
}