package org.slc.sli.modeling.docgen;

import javax.xml.namespace.QName;

/**
 * The elements used in the Platform Independent Model tool chain.
 */
public interface DocumentationElements {
    public static final QName FEATURE = new QName("feature");
    public static final QName CLASS = new QName("class");
    public static final QName DATA_TYPE = new QName("datatype");
    public static final QName DESCRIPTION = new QName("description");
    public static final QName GENERALIZATION = new QName("generalization");
    /**
     * Prolog is text that comes before a diagram.
     */
    public static final QName PROLOG = new QName("prolog");
    /**
     * Epilog is text that comes after a diagram.
     */
    public static final QName EPILOG = new QName("epilog");
    public static final QName DIAGRAM = new QName("diagram");
    public static final QName DOMAIN = new QName("domain");
    /**
     * domains. This is the document element for the generated Platform Independent Model.
     */
    public static final QName DOMAINS = new QName("domains");
    public static final QName ENTITY = new QName("entity");
    public static final QName ENUM_TYPE = new QName("enumeration");
    public static final QName LITERAL = new QName("literal");
    public static final QName LOWER = new QName("lower");
    public static final QName NAMESPACE = new QName("namespace");
    public static final QName NAME = new QName("name");
    /**
     * The the document element for the generated Platform Independent Model Configuration.
     */
    public static final QName PIM_CFG = new QName("pim-cfg");
    public static final QName SOURCE = new QName("source");
    public static final QName TITLE = new QName("title");
    public static final QName TYPE = new QName("type");
    public static final QName UPPER = new QName("upper");
}
