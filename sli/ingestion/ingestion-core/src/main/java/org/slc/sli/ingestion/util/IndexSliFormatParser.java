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

import java.util.HashSet;
import java.util.Set;


/**
 * Parser to parse the indexes we define in the index file.
 * It is a simple format as:
 * collection,uniqueness,key1:order,key2:order...
 *
 * @author tke
 *
 */
public class IndexSliFormatParser implements IndexParser<Set<String>> {

    /**
     * This
     */
    @Override
    public Set<MongoIndex> parse(Set<String> indexes) {
        Set<MongoIndex> res = new HashSet<MongoIndex>();

        for(String index : indexes) {
            if(validIndex(index)) {
                MongoIndex mongoIndex = parse(index);
                res.add(mongoIndex);
            }
        }

        return res;
    }

    public static MongoIndex parse(String indexEntry) {
        MongoIndex mongoIndex = new MongoIndex();

        String[] indexTokens = indexEntry.split(",");

        if (indexTokens.length < 3) {
            throw new IllegalStateException("Expected at least 3 tokens for index config definition: "
                    + indexEntry);
        }

        String collection = indexTokens[0];
        boolean unique = Boolean.parseBoolean(indexTokens[1]);

        mongoIndex.setCollection(collection);
        mongoIndex.setUnique(unique);

        for (int i = 2; i < indexTokens.length; i++) {
            String [] index = indexTokens[i].split(":");

            //default order of the index
            int order = 1;

            //If the key specifies order
            if (index.length == 2) {
                //remove all the non visible characters from order string
                order = Integer.parseInt(index[1].replaceAll("\\s", ""));
            } else if (index.length != 1) {
                throw new IllegalStateException("Unexpected index order: "
                        + indexTokens[i]);
            }

            mongoIndex.getKeys().put(index[0], order);
        }

        return mongoIndex;
    }

    public static boolean validIndex(String line) {
        if (line.startsWith("#")) {
            return false;
        }
        String[] indexTokens = line.split(",");
        if (indexTokens.length < 3) {
            return false;
        }
        return true;
    }

}
