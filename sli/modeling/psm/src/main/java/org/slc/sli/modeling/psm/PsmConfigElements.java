package org.slc.sli.modeling.psm;

import javax.xml.namespace.QName;

public interface PsmConfigElements {
    public static final QName ATTRIBUTE = new QName("attribute");
    public static final QName CLASS_TYPE = new QName("class");
    /**
     * The singular resource name will be used for elements of a collection.
     */
    public static final QName SINGULAR_RESOURCE_NAME = new QName("singular");
    public static final QName DATA_TYPE = new QName("datatype");
    public static final QName DESCRIPTION = new QName("description");
    public static final QName DIAGRAM = new QName("diagram");
    public static final QName DOCUMENT = new QName("document");
    public static final QName DOCUMENTS = new QName("documents");
    public static final QName DOMAIN = new QName("domain");
    public static final QName ENUM_TYPE = new QName("enumeration");
    public static final QName LITERAL = new QName("literal");
    public static final QName LOWER = new QName("lower");
    public static final QName NAME = new QName("name");
    /**
     * The plural resource name will typically be used for collections.
     */
    public static final QName PLURAL_RESOURCE_NAME = new QName("plural");
    public static final QName SOURCE = new QName("source");
    public static final QName TITLE = new QName("title");
    public static final QName TYPE = new QName("type");
    public static final QName UPPER = new QName("upper");
}
