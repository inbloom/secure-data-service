package org.slc.sli.modeling.xmicomp;

import javax.xml.namespace.QName;

public final class XmiMappingConstants {
    /**
     * The name space for mapping elements.
     */
    public static final String NAMESPACE_URI = "";
    /**
     * A commentary on the mapping.
     */
    public static final QName COMMENT = new QName(NAMESPACE_URI, "comment");
    /**
     * The top-level a.k.a document element name.
     */
    public static final QName DOCUMENT_ELEMENT = new QName(NAMESPACE_URI, "mappings");
    /**
     * Whether the feature is defined (in the UML model).
     */
    public static final QName EXISTS = new QName(NAMESPACE_URI, "exists");
    /**
     * The version.
     */
    public static final QName FILE = new QName(NAMESPACE_URI, "xmi");
    /**
     * The left-hand feature.
     */
    public static final QName LHS_FEATURE = new QName(NAMESPACE_URI, "lhs");
    /**
     * The left-hand missing.
     */
    public static final QName LHS_MISSING = new QName(NAMESPACE_URI, "lhs-missing");
    /**
     * The left-hand model.
     */
    public static final QName LHS_MODEL = new QName(NAMESPACE_URI, "lhs-model");
    /**
     * A mapping from a feature in one data dictionary to another data dictionary.
     */
    public static final QName MAPPING = new QName(NAMESPACE_URI, "mapping");
    /**
     * The name of the feature.
     */
    public static final QName NAME = new QName(NAMESPACE_URI, "name");
    /**
     * Whether the feature owner is defined (in the UML model).
     */
    public static final QName OWNER_EXISTS = new QName(NAMESPACE_URI, "owner-exists");
    /**
     * The type of the feature.
     */
    public static final QName OWNER_NAME = new QName(NAMESPACE_URI, "owner-name");
    /**
     * The right-hand feature.
     */
    public static final QName RHS_FEATURE = new QName(NAMESPACE_URI, "rhs");
    /**
     * The right-hand missing.
     */
    public static final QName RHS_MISSING = new QName(NAMESPACE_URI, "rhs-missing");
    /**
     * The right-hand model.
     */
    public static final QName RHS_MODEL = new QName(NAMESPACE_URI, "rhs-model");
    /**
     * The status of the mapping.
     */
    public static final QName STATUS = new QName(NAMESPACE_URI, "status");
    /**
     * The version.
     */
    public static final QName VERSION = new QName(NAMESPACE_URI, "version");
}
