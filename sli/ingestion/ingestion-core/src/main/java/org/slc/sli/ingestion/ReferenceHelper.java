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

package org.slc.sli.ingestion;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.elasticsearch.common.jackson.core.JsonGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
*
*/

public class ReferenceHelper implements ResourceLoaderAware {

    private String mapFile;
    private ResourceLoader resourceLoader;
    private static final String KEY_ALL = "ALL";
    private static final String PATH_SEPARATOR = "\\.";

    private static final Logger LOG = LoggerFactory.getLogger(ReferenceHelper.class);
    private Map<String, Object> converterMap = null;

    public String getMapFile() {
        return mapFile;
    }

    public void setMapFile(String mapFile) {
        this.mapFile = mapFile;
    }

    private synchronized void init() {
        if (converterMap != null) {
            return;
        }

        try {
            InputStream configIs = null;
            Resource config = resourceLoader.getResource(mapFile);

            if (config.exists()) {
                configIs = config.getInputStream();
                ObjectMapper mapper = new ObjectMapper();
                converterMap = mapper.readValue(configIs, new TypeReference<Map<String, Object>>() {
                });
            } else {
                LOG.warn("Could not read Reference Converter file {}", mapFile);

            }
            configIs.close();
        } catch (JsonGenerationException e) {
            LOG.error(e.getStackTrace().toString());
        } catch (JsonMappingException e) {
            LOG.error(e.getStackTrace().toString());
        } catch (IOException e) {
            LOG.error(e.getStackTrace().toString());
        }
    }

    @SuppressWarnings("unchecked")
    public void mapAttributes(Map<String, Object> attributes, String referenceName) {

        if (converterMap == null) {
            init();
        }

        final Map<String, String> derivedFields = (Map<String, String>) converterMap.get(KEY_ALL);
        if (derivedFields != null) {
            fillMap(attributes, derivedFields);
        }

        final Map<String, String> refMap = (Map<String, String>) converterMap.get(referenceName);
        if (refMap == null) {
            return;
        }

        for (String fromField : refMap.keySet()) {
            // split source ref path and look for lists in embedded objects
            String[] fromPath = fromField.split(PATH_SEPARATOR);
            String[] toPath = refMap.get(fromField).split(PATH_SEPARATOR);

            Object value = getAttribute( fromPath, attributes, referenceName);
            buildMap(toPath, attributes, value );
        }

    }

    private Object getAttribute(String[] fromPath, Map<String, Object> source, String referenceName ) {
        Object result = null;

        Map<String, Object> from = source;
        for (int i = 0; i < fromPath.length; ++i) {
            result = from.get(fromPath[i]);
            /*
             * Something is wrong: we are not at the end of the path yet
             */
            if (i < (fromPath.length - 1) && !(result instanceof Map)) {
                LOG.error( "Error occured while building reference subsitutions for {} field {}", referenceName, fromPath[ i ]);
                return null;
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private void buildMap(String[] path, Map<String, Object> curNode, Object value ) {
        int lastIndex = path.length - 1;

        Map< String, Object> top = curNode;

        for ( int i = 0 ; i < lastIndex ; ++i ) {
            if( top.containsKey( path[ i ] ) ) {
               top = (Map<String, Object>) top.get( path[ i ] );
            } else {
                top.put( path[ i ], new HashMap<String, Object>());
                top = (Map<String, Object>) top.get( path[ i ]);
            }
        }
        top.put( path[ lastIndex ], value);
    }


    private void fillMap(Map<String, Object> attributes, Map<String, String> corrections) {

        for (String derivedField : corrections.keySet()) {
            String existingField = corrections.get(derivedField);
            if (attributes.containsKey(existingField)) {
                attributes.put(derivedField, attributes.get(existingField));
                // attributes.remove(refFieldName);
            }
        }
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
