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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.apache.commons.io.IOUtils;
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
public class IndexJSFileParser implements IndexParser<String> {

    private static final Logger LOG = LoggerFactory.getLogger(IndexJSFileParser.class);

    /* (non-Javadoc)
     * @see org.slc.sli.ingestion.util.IndexFileParser#parse(java.lang.String)
     */
    @Override
    public Set<MongoIndex> parse(String fileName) {

        Map<String, Object> indexMap = null;

        File resourceFile = null;
        FileInputStream fstream = null;
        BufferedReader br = null;

        Set<MongoIndex> indexes = new HashSet<MongoIndex>();

        try {
            URL resourceURL = Thread.currentThread().getContextClassLoader().getResource(fileName);
            if (resourceURL != null) {
                resourceFile = new File(resourceURL.toURI());
                if (resourceFile != null) {

                    fstream = new FileInputStream(resourceFile);
                    br = new BufferedReader(new InputStreamReader(fstream));
                    String collectionName = null;
                    String keyJsonString;

                    String currentFileLine;

                    while ((currentFileLine = br.readLine()) != null) {
                        boolean unique = false;
                        Matcher indexMatcher = ensureIndexStatement(currentFileLine);

                        if (indexMatcher != null) {
                            collectionName = indexMatcher.group(1);
                            keyJsonString = indexMatcher.group(2);
                            if (indexMatcher.group(3) != null) {
                                unique = Boolean.parseBoolean(indexMatcher.group(4));
                            }
                            indexMap = parseJson(keyJsonString);
                            DBObject keyObj = new BasicDBObject(indexMap);
                            indexes.add(new MongoIndex(collectionName, unique, keyObj));
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOG.error("Error reading index file:" + e.getMessage());
        } catch (URISyntaxException e) {
            LOG.error("Index file not found: " + e.getMessage());
        } finally {
            IOUtils.closeQuietly(br);
            IOUtils.closeQuietly(fstream);
        }

        return indexes;
    }

    private static Matcher ensureIndexStatement(String statement) {
        Pattern ensureIndexPattern = Pattern.compile("^db\\[\"(\\S+)\"]\\.ensureIndex\\((\\{[^}]*\\})(,\\s*\\{\\s*unique\\s*:\\s*(\\S+)\\s*\\})?\\);.*", Pattern.MULTILINE);
        Matcher ensureIndexMatcher = ensureIndexPattern.matcher(statement);
        if (ensureIndexMatcher.matches()) {
            return ensureIndexMatcher;
        }
        return null;
    }

    public static Map<String, Object> parseJson(String jsonString) {

        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);

        TypeReference<LinkedHashMap<String, Object>> typeRef = new TypeReference<LinkedHashMap<String, Object>>() {
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
