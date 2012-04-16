package org.slc.sli.modeling.xsd;

import javax.xml.namespace.QName;

/**
 * Symbolic constants for the W3C XML Schema (WXS) name-space.
 */
public interface WxsNamespace {
    public static final String URI = "http://www.w3.org/2001/XMLSchema";
    public static final QName BOOLEAN = new QName(URI, "boolean");
    public static final QName DATE = new QName(URI, "date");
    public static final QName DOUBLE = new QName(URI, "double");
    public static final QName INT = new QName(URI, "int");
    public static final QName INTEGER = new QName(URI, "integer");
    public static final QName STRING = new QName(URI, "string");
    public static final QName TIME = new QName(URI, "time");
    public static final QName TOKEN = new QName(URI, "token");
}
