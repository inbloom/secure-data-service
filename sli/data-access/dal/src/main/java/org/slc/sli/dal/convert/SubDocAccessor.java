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

package org.slc.sli.dal.convert;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.slc.sli.common.domain.EmbeddedDocumentRelations;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.validation.schema.INaturalKeyExtractor;

/**
 * Utility for accessing subdocuments that have been collapsed into a super-doc
 *
 * @author nbrown
 *
 */
public class SubDocAccessor {

    private final Map<String, SubDocLocation> locations = new HashMap<String, SubDocLocation>();

    private final MongoTemplate template;

    private final UUIDGeneratorStrategy didGenerator;

    private final INaturalKeyExtractor naturalKeyExtractor;

    public SubDocAccessor(MongoTemplate template, UUIDGeneratorStrategy didGenerator,
            INaturalKeyExtractor naturalKeyExtractor) {
        this.template = template;
        this.didGenerator = didGenerator;
        this.naturalKeyExtractor = naturalKeyExtractor;

        for (String entityType : EmbeddedDocumentRelations.getSubDocuments()) {
            String parent = EmbeddedDocumentRelations.getParentEntityType(entityType);
            String parentKey = EmbeddedDocumentRelations.getParentFieldReference(entityType);
            if (parent != null && parentKey != null) {
                store(entityType).within(parent).as(entityType).mapping(parentKey, "_id").asEntity().register();
            }
        }
    }

    /**
     * Start a location for a given sub doc type
     *
     * @param type
     * @return
     */
    public LocationBuilder store(String type) {
        return new LocationBuilder(type);
    }

    private class LocationBuilder {
        private Map<String, String> lookup = new HashMap<String, String>();
        private String collection;
        private String subField;
        private final String type;
        private boolean asEntity = false;
        private String key = "_id";

        public LocationBuilder(String type) {
            super();
            this.type = type;
        }

        /**
         * Store the subdoc within the given super doc collection
         *
         * @param collection
         *            the collection the subdoc gets stored in
         * @return
         */
        public LocationBuilder within(String collection) {
            this.collection = collection;
            return this;
        }

        /**
         * The field the subdocs show up in
         *
         * @param subField
         *            The field the subdocs show up in
         * @return
         */
        public LocationBuilder as(String subField) {
            this.subField = subField;
            return this;
        }

        /**
         * Map a field in the sub doc to the super doc. This will be used when resolving parenthood
         *
         * @param subDocField
         * @param superDocField
         * @return
         */
        public LocationBuilder mapping(String subDocField, String superDocField) {
            lookup.put(subDocField, superDocField);
            return this;
        }

        public LocationBuilder asEntity() {
            this.asEntity = true;
            return this;
        }

        public LocationBuilder withKey(String key) {
            this.key = key;
            return this;
        }

        /**
         * Register it as a sub resource location
         */
        public void register() {
            SubDocLocation location = asEntity ? new SubDocLocation(SubDocAccessor.this, collection, lookup, subField, key)
                    : new PartialLocation(SubDocAccessor.this, collection, lookup, subField, key);
            locations.put(type, location);
        }

    }

    public boolean isSubDoc(String docType) {
        return locations.containsKey(docType);
    }

    public SubDocLocation subDoc(String docType) {
        return locations.get(docType);
    }

    public MongoTemplate getTemplate() {
        return template;
    }

    public UUIDGeneratorStrategy getDidGenerator() {
        return didGenerator;
    }

    public INaturalKeyExtractor getNaturalKeyExtractor() {
        return naturalKeyExtractor;
    }

    public static void main(String[] args) {
        PrintStream output = System.out;
        SubDocAccessor accessor = new SubDocAccessor(null, null, null);
        ConcurrentMap<String, List<String>> collections = new ConcurrentHashMap<String, List<String>>();
        for (Entry<String, SubDocLocation> entry : accessor.locations.entrySet()) {
            String type = entry.getKey();
            String collectionName = entry.getValue().getCollection();
            collections.putIfAbsent(collectionName, new ArrayList<String>());
            collections.get(collectionName).add(type);
        }
        for (Entry<String, List<String>> entry : collections.entrySet()) {
            output.println(entry.getKey() + ": " + StringUtils.join(entry.getValue().toArray(), ", "));
        }
    }
}
