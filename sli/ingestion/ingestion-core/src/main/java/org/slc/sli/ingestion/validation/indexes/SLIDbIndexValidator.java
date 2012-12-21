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

package org.slc.sli.ingestion.validation.indexes;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.ingestion.util.IndexFileParser;
import org.slc.sli.ingestion.util.MongoIndex;

/**
 *
 * @author npandey
 *
 */
public class SLIDbIndexValidator extends DbIndexValidator {

    private static final String INDEX_FILE = "sli_indexes.js";

    @Autowired
    private IndexFileParser indexJSFileParser;

    @Override
    protected Set<MongoIndex> loadExpectedIndexes() {
        return indexJSFileParser.parse(INDEX_FILE);
    }
}
