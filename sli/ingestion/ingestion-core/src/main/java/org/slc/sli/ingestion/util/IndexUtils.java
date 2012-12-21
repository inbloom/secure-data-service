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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tke
 *
 */
public class IndexUtils {
    protected static final Logger LOG = LoggerFactory.getLogger(IndexUtils.class);

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


    public static MongoIndex parseIndex(String indexEntry) {
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

    public static Map<String, Object> parseJson(String jsonString) {

        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);

        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
        };
        try {
            return mapper.readValue(jsonString, typeRef);
        } catch (JsonParseException e) {
            LOG.error("Error validating indexes " + e.getLocalizedMessage());
        } catch (JsonMappingException e) {
            LOG.error("Error validating indexes " + e.getLocalizedMessage());
        } catch (IOException e) {
            LOG.error("Error validating indexes " + e.getLocalizedMessage());
        }
        return null;
    }
}
