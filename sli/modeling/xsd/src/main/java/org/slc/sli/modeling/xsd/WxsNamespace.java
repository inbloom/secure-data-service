/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.modeling.xsd;

import javax.xml.namespace.QName;

/**
 * Symbolic constants for the W3C XML Schema (WXS) name-space.
 */
public final class WxsNamespace {
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
