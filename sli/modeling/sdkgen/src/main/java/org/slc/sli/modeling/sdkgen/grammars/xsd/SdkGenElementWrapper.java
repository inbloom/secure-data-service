/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.modeling.sdkgen.grammars.xsd;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaType;

import org.slc.sli.modeling.sdkgen.grammars.SdkGenElement;
import org.slc.sli.modeling.sdkgen.grammars.SdkGenType;

public final class SdkGenElementWrapper implements SdkGenElement {

    private final XmlSchemaElement element;

    public SdkGenElementWrapper(final XmlSchemaElement element) {
        if (element == null) {
            throw new IllegalArgumentException("element");
        }
        this.element = element;
    }

    @Override
    public QName getName() {
        return element.getQName();
    }

    public SdkGenType getType() {
        final XmlSchemaType schemaType = element.getSchemaType();
        return new SdkGenTypeWrapper(schemaType);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("name : ").append(element.getQName());
        sb.append("}");
        return sb.toString();
    }
}
