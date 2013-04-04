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
package org.slc.sli.bulk.extract.files.writer;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.domain.Entity;

/**
* Writes an Entity to a File.
* @author ablum
*
*/
public class ComplexEntityWriter implements EntityWriter{
    private static final Logger LOG = LoggerFactory.getLogger(ComplexEntityWriter.class);

    Map<String, JsonEntityWriter> writers;
    Map<String, String> multiFileEntities;

    @Override
    public Entity write(Entity entity, ExtractFile archiveFile) {
        writers.get(entity.getType()).write(entity, archiveFile.getDataFileEntry(entity.getType()));

        if (multiFileEntities.containsKey(entity.getType())) {
            String otherFileName = multiFileEntities.get(entity.getType());
            writers.get(otherFileName).write(entity, archiveFile.getDataFileEntry(otherFileName));
        }

        return entity;
    }

    /**
     * writers.
     * @param writers writers.
     */
    public void setWriters(Map<String, JsonEntityWriter> writers) {
        this.writers = writers;
    }

    /**
     * multiFileEntities.
     * @param multiFileEntities multiFileEntities
     */
    public void setmultiFileEntities(Map<String, String> multiFileEntities) {
        this.multiFileEntities = multiFileEntities;
    }

}
