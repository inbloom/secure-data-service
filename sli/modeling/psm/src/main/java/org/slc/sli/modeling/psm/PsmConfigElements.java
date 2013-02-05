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


package org.slc.sli.modeling.psm;

import javax.xml.namespace.QName;

/**
 * Element names for PSM document.
 */
public class PsmConfigElements {
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
     * The graph resource name will typically be used for association end names and REST URIs.
     */
    public static final QName GRAPH_RESOURCE_NAME = new QName("graph");
    public static final QName SOURCE = new QName("source");
    public static final QName TITLE = new QName("title");
    public static final QName TYPE = new QName("type");
    public static final QName UPPER = new QName("upper");
}
