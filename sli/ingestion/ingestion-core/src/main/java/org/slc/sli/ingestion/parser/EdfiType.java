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
package org.slc.sli.ingestion.parser;

/**
 * Describes an Edfi element type.
 *
 * @author dduran
 *
 */
public interface EdfiType {

    /**
     * The type of the element as defined in the XSD.
     *
     * @return String value of type.
     */
    String getType();

    /**
     * The name of the element as described in the XSD parent type.
     *
     * @return String value of name.
     */
    String getName();

    /**
     * Whether this element is unbounded.
     *
     * @return <code>true</code> if the element should be represented as a list.
     */
    boolean isList();

}
