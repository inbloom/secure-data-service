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
package org.slc.sli.ingestion.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tke
 *
 */
public class IndexTxtFileParser implements IndexFileParser {
    protected static final Logger LOG = LoggerFactory.getLogger(IndexTxtFileParser.class);
    /* (non-Javadoc)
     * @see org.slc.sli.ingestion.util.IndexFileParser#parse(java.lang.String)
     */
    @Override
    public Set<MongoIndex> parse(String fileName) {
        Set<MongoIndex> indexes = new HashSet<MongoIndex>();

        Set<String> indexSet = readIndexes(fileName);
        for (String index : indexSet) {
            indexes.add(IndexUtils.parseIndex(index));
        }

        return indexes;
    }

    public Set<String> readIndexes(String indexFile) {
        Set<String> indexes = new TreeSet<String>();
        InputStream indexesStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(indexFile);

        if (indexesStream == null) {
            LOG.error("Failed to open index file {}", indexFile);
            return indexes;
        }

        DataInputStream in = null;
        BufferedReader br = null;

        String currentLine;
        //Reading in all the indexes
        try {
            in = new DataInputStream(indexesStream);
            br = new BufferedReader(new InputStreamReader(in));
            while ((currentLine = br.readLine()) != null) {
                //skipping lines starting with #
                if (IndexUtils.validIndex(currentLine)) {
                    indexes.add(currentLine);
                }
            }
        } catch (IOException e) {
            LOG.error("Failed to create index from {}", indexFile);
        } finally {
            IOUtils.closeQuietly(br);
            IOUtils.closeQuietly(in);
        }
        return indexes;
    }

}
