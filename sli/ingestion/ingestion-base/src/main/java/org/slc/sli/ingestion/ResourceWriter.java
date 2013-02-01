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

import java.util.List;

/**
 * Generic interface for writing resources.
 *
 * @author dduran
 *
 * @param <T> Type to write
 */
public interface ResourceWriter<T> {

    /**
     * Write an entity to the data store (using 'upsert').
     *
     * @param t Entity to be written.
     * @param jobId Current batch job id.
     *
     */
    void writeResource(T t);

    /**
     * Inserts an entity to the data store (using 'insert').
     *
     * @param t Entity to be written.
     * @param jobId Current batch job id.
     */
    void insertResource(T t);

    /**
     * Inserts multiple entities to the data store (using 'insert').
     *
     * @param t Entities to be written.
     * @param collectionName Name of collection to write entities to.
     * @param jobId Current batch job id.
     */
    void insertResources(List<T> neutralRecords, String collectionName);
}
