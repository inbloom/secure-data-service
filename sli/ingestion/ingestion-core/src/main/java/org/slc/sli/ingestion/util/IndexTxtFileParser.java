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

package org.slc.sli.ingestion.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parser to parse the file with "sli" index format
 * @author tke
 *
 */
public class IndexTxtFileParser implements IndexParser<String> {
    protected static final Logger LOG = LoggerFactory.getLogger(IndexTxtFileParser.class);
    /* (non-Javadoc)
     * @see org.slc.sli.ingestion.util.IndexFileParser#parse(java.lang.String)
     */

    @Override
    public Set<MongoIndex> parse(String fileName) {
        Set<String> indexSet = loadIndexes(fileName);

        return new IndexSliFormatParser().parse(indexSet);
    }

    public static Set<String> loadIndexes(String indexFile) {
        Set<String> indexes = new HashSet<String>();
        InputStream indexesStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(indexFile);

        if (indexesStream == null) {
            LOG.error("Failed to open index file {}", indexFile);
            return indexes;
        }

        BufferedReader br = null;

        String currentLine;
        //Reading in all the indexes
        try {
            br = new BufferedReader(new InputStreamReader(indexesStream));
            while ((currentLine = br.readLine()) != null) {
                indexes.add(currentLine);
            }
        } catch (IOException e) {
            LOG.error("Failed to create index from {}", indexFile);
        } finally {
            IOUtils.closeQuietly(br);
        }
        return indexes;
    }

}
