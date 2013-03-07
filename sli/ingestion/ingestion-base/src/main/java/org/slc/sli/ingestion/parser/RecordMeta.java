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

import javax.xml.stream.Location;

/**
 * Describes an XML record.
 *
 * @author dduran
 *
 */
public interface RecordMeta {

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

    /**
     * Provide start location for record.
     *
     * @return The <code>javax.xml.stream.Location</code> associated with the XML start tag for this
     *         record in the source.
     */
    Location getSourceStartLocation();

    /**
     * Provide end location for record;
     *
     * @return The <code>javax.xml.stream.Location</code> associated with the XML start tag for this
     *         record in the source.
     */
    Location getSourceEndLocation();

}
