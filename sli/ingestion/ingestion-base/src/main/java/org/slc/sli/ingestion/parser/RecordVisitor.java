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

import java.util.Map;

/**
 * Visitor for a parser to notify when a record has been fully parsed.
 *
 * @author dduran
 *
 */
public interface RecordVisitor {

    /**
     * Invoked upon completion of parsing a record.
     *
     * @param edfiType
     *            meta information about the parsed record.
     * @param record
     *            the fully constructed map of objects representation of the record
     */
    void visit(RecordMeta edfiType, Map<String, Object> record);

    void ignored();

}
