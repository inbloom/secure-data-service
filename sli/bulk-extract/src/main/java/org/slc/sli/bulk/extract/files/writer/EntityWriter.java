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

import java.io.IOException;

import org.codehaus.jackson.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.bulk.extract.files.metadata.ErrorFile;
import org.slc.sli.bulk.extract.treatment.Treatment;
import org.slc.sli.domain.Entity;

/**
 * Writes an Entity to a File.
 * @author ablum
 *
 */
public class EntityWriter {
    private static final Logger LOG = LoggerFactory.getLogger(EntityWriter.class);

    private Treatment applicator;

    /**
     * Constructor.
     * @param treatment treatment
     */
    public EntityWriter(Treatment treatment) {
        this.applicator = treatment;
    }

    /**
     * Writes a treated entity to a file.
     * @param entity entity
     * @param file file
     * @return entity
     */
    public Entity write(Entity entity, JsonFileWriter file, ErrorFile errors) {
        Entity treated = applicator.apply(entity);
        try {
            file.write(treated);
        } catch (JsonProcessingException e) {
            LOG.error("Error while extracting from " + entity.getType(), e);
            errors.logEntityError(entity);
        } catch (IOException e) {
            LOG.error("Error while extracting from " + entity.getType(), e);
            errors.logEntityError(entity);
        }

        return treated;
    }
}
