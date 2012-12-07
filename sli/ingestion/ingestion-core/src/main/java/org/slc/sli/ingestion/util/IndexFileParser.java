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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author npandey
 *
 */

public final class IndexFileParser {

    private static final Logger LOG = LoggerFactory.getLogger(IndexFileParser.class);

    public static List<MongoIndex> parseJSFile(String fileName) {

        Map<String, Object> indexMap = null;

        File resourceFile = null;
        FileInputStream fstream = null;
        BufferedReader br = null;

        List<MongoIndex> indexes = new ArrayList<MongoIndex>();

        try {
            URL resourceURL = Thread.currentThread().getContextClassLoader().getResource(fileName);
            if (resourceURL != null) {
                resourceFile = new File(resourceURL.toURI());
                if (resourceFile != null) {

                    fstream = new FileInputStream(resourceFile);
                    br = new BufferedReader(new InputStreamReader(fstream));
                    String collectionName = null;
                    String keyJsonString;
                    boolean unique = false;
                    String currentFileLine;

                    while ((currentFileLine = br.readLine()) != null) {

                        Matcher indexMatcher = ensureIndexStatement(currentFileLine);

                        if (indexMatcher != null) {
                            collectionName = indexMatcher.group(1);
                            keyJsonString = indexMatcher.group(2);
                            if (indexMatcher.group(3) != null) {
                                unique = Boolean.parseBoolean(indexMatcher.group(4));
                            }
                            indexMap = parseJson(keyJsonString);
                            indexes.add(new MongoIndex(collectionName, unique, indexMap));
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage());
        } catch (IOException e) {
            LOG.error(e.getMessage());
        } catch (URISyntaxException e) {
            LOG.error(e.getMessage());
        } finally {
            IOUtils.closeQuietly(br);
            IOUtils.closeQuietly(fstream);
        }

        return indexes;
    }

    /**
     * Parse index in the formate of tenantDB_indexes.txt
     * @param indexFile : the name of the index file
     * @return a list MongoIndexes for all the indexes
     */
    public static List<MongoIndex> parseTxtFile(String indexFile) {
        List<MongoIndex> indexes = new ArrayList<MongoIndex>();

        Set<String> indexSet = readIndexes(indexFile);
        for (String index : indexSet) {
            indexes.add(parseIndex(index));
        }

        return indexes;
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

    public static Set<String> readIndexes(String indexFile) {
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
                if (validIndex(currentLine)) {
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