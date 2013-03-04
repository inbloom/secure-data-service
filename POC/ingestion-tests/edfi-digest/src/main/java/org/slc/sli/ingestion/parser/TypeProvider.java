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

package org.slc.sli.ingestion.parser;

/**
 * Provides type information.
 *
 * @author dkornishev
 * @author dduran
 */
public interface TypeProvider {

    /**
     * Given an interchange, provide the type of the element with given name.
     *
     * @param interchange
     *            String value of EdFi Interchange name.
     * @param elementName
     *            String value of the name of an EdFi element defined in the interchange.
     * @return String value of the type of the element with given name as defined by the
     *         interchange.
     */
    public String getTypeFromInterchange(String interchange, String elementName);

    /**
     * Given an element's parent's type, provide the type of the element with given name.
     *
     * @param parentType
     *            String value of element's parent's EdFi type.
     * @param elementName
     *            String value of the name of an EdFi element.
     * @return EdfiType for this element.
     */
    public RecordMeta getTypeFromParentType(String parentType, String elementName);

    /**
     * Process / convert the value for a given type.
     *
     * @param type
     *            String value of an EdFi type.
     * @param value
     *            Parsed value to be converted.
     * @return Converted object.
     */
    public Object convertType(String type, String value);
}
