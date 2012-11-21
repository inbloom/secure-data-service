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

package org.slc.sli.modeling.sdkgen;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.jgen.JavaType;

/**
 * Helper methods
 */
public final class GenericJavaHelper {

    public static JavaType getJavaType(final QName name) {
        if (name.getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema")) {
            final String localName = name.getLocalPart();
            if (localName.equals("string")) {
                return JavaType.JT_STRING;
            } else {
                throw new AssertionError("localName: " + name.getLocalPart());
            }
        } else {
            throw new AssertionError("namespaceURI: " + name.getNamespaceURI());
        }
    }
}
