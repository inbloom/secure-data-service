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
package org.slc.sli.bulk.extract.files;

import java.util.Map;

import org.slc.sli.bulk.extract.files.writer.EntityWriter;
import org.slc.sli.bulk.extract.util.DefaultHashMap;
import org.slc.sli.domain.Entity;

/**
* Writes an Entity to a File.
* @author ablum
*
*/
public class EntityWriterManager {

    private final String DELETE = "deleted";

    DefaultHashMap<String, EntityWriter> writers;
    Map<String, String> multiFileEntities;
    Map<String, String> entities;


    /**
     * Write an Entity to a File.
     * @param entity entity
     * @param archiveFile archiveFile
     * @return written entity
     */
    public Entity write(Entity entity, ExtractFile archiveFile) {
        writeEntityFile(entity, archiveFile);

        writerCollectionFile(entity, archiveFile);

        return entity;
    }
    
    /**
     * Write all delete/purge event to a single delete file
     * 
     * @param entity
     * @param archiveFile
     */
    public void writeDeleteFile(Entity entity, ExtractFile archiveFile) {
        EntityWriter defaultWriter = writers.getDefault();
        defaultWriter.write(entity, archiveFile.getDataFileEntry(DELETE));
    }

    private void writeEntityFile(Entity entity, ExtractFile archiveFile) {
        if(entities.containsKey(entity.getType())) {
            writers.getValue(entity.getType()).write(entity, archiveFile.getDataFileEntry(entity.getType()));
        }
    }

    private void writerCollectionFile(Entity entity, ExtractFile archiveFile) {
        if (multiFileEntities.containsKey(entity.getType())) {
            String otherFileName = multiFileEntities.get(entity.getType());
            writers.getValue(otherFileName).write(entity, archiveFile.getDataFileEntry(otherFileName));
        }
    }

    /**
     * writers.
     * @param writers writers.
     */
    public void setWriters(DefaultHashMap<String, EntityWriter> writers) {
        this.writers = writers;
    }

    /**
     * multiFileEntities.
     * @param multiFileEntities multiFileEntities
     */
    public void setMultiFileEntities(Map<String, String> multiFileEntities) {
        this.multiFileEntities = multiFileEntities;
    }

    /**
     * entities.
     * @param entities entities
     */
    public void setEntities(Map<String, String> entities) {
        this.entities = entities;
    }
}
