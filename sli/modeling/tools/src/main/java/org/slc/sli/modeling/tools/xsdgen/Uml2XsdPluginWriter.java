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


package org.slc.sli.modeling.tools.xsdgen;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.uml.Occurs;

/**
 * The interface for writing the W3C XML Schema from the {@link Uml2XsdPluginWriter}.
 */
public interface Uml2XsdPluginWriter {

    void annotation();

    void appinfo();

    void begin(final String prefix, final String localName, final String namespace);

    void characters(String text);

    void choice();

    void comment(String data);

    void complexType();

    void documentation();

    void element();

    void elementName(QName name);

    void end();

    void maxOccurs(final Occurs value);

    void minOccurs(final Occurs value);

    void ref(QName name);

    void sequence();

    void type(QName name);
}
