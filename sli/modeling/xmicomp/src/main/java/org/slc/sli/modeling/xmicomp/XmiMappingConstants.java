package org.slc.sli.modeling.xmicomp;

import javax.xml.namespace.QName;

public final class XmiMappingConstants {
    /**
     * The name space for mapping elements.
     */
    public static final String NAMESPACE_MAPPING = "";
    /**
     * The top-level a.k.a document element name.
     */
    public static final QName DOCUMENT_ELEMENT = new QName(NAMESPACE_MAPPING, "mappings");
    /**
     * A mapping from a feature in one data dictionary to another data dictionary.
     */
    public static final QName MAPPING = new QName(NAMESPACE_MAPPING, "mapping");
    /**
     * The left-hand feature.
     */
    public static final QName LHS_FEATURE = new QName(NAMESPACE_MAPPING, "lhs");
    /**
     * The right-hand feature.
     */
    public static final QName RHS_FEATURE = new QName(NAMESPACE_MAPPING, "rhs");
    /**
     * The status of the mapping.
     */
    public static final QName STATUS = new QName(NAMESPACE_MAPPING, "status");
    /**
     * A commentary on the mapping.
     */
    public static final QName COMMENT = new QName(NAMESPACE_MAPPING, "comment");
    /**
     * The name of the feature.
     */
    public static final QName NAME = new QName(NAMESPACE_MAPPING, "name");
    /**
     * The type of the feature.
     */
    public static final QName TYPE = new QName(NAMESPACE_MAPPING, "type");
    /**
     * The version.
     */
    public static final QName VERSION = new QName(NAMESPACE_MAPPING, "version");
    /**
     * The version.
     */
    public static final QName FILE = new QName(NAMESPACE_MAPPING, "xmi");
    /**
     * The left-hand model.
     */
    public static final QName LHS_MODEL = new QName(NAMESPACE_MAPPING, "lhsModel");
    /**
     * The right-hand model.
     */
    public static final QName RHS_MODEL = new QName(NAMESPACE_MAPPING, "rhsModel");
    /**
     * The left-hand missing.
     */
    public static final QName LHS_MISSING = new QName(NAMESPACE_MAPPING, "lhsMissing");
    /**
     * The right-hand missing.
     */
    public static final QName RHS_MISSING = new QName(NAMESPACE_MAPPING, "rhsMissing");
}
