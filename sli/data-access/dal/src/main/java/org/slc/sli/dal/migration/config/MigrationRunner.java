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

package org.slc.sli.dal.migration.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.dal.versioning.SliSchemaVersionValidator;
import org.slc.sli.domain.Entity;

/**
 * Aspect for executing migrations on demand
 *
 * @author srupasinghe
 */

@Aspect
public class MigrationRunner {

    private SliSchemaVersionValidator sliSchemaVersionValidator;

    private MongoEntityRepository repository;

    public void setSliSchemaVersionValidator(SliSchemaVersionValidator sliSchemaVersionValidator) {
        this.sliSchemaVersionValidator = sliSchemaVersionValidator;
    }

    public void setRepository(MongoEntityRepository repository) {
        this.repository = repository;
    }

    /*
     * Designates a method that migrates a single entity.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface MigrateEntity {
    }

    /*
     * Designates a method that migrates a collection of entities.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface MigrateEntityCollection {
    }

    @Around(value = "@annotation(annotation)")
    public Object migrateEntiy(final ProceedingJoinPoint proceedingJoinPoint, final MigrateEntity annotation) throws Throwable {
        Object obj = proceedingJoinPoint.proceed();
        String collectionName = "";

        Object[] args = proceedingJoinPoint.getArgs();
        if (args.length > 0) {
            if (args[0] instanceof String) {
                collectionName = (String) args[0];
            }
        }

        if (sliSchemaVersionValidator != null) {
            return sliSchemaVersionValidator.migrate(collectionName, (Entity) obj, repository);
        } else {
            return obj;
        }
    }

    @Around(value = "@annotation(annotation)")
    public Object migrateEntiyCollection(final ProceedingJoinPoint proceedingJoinPoint, final MigrateEntityCollection annotation) throws Throwable {
        Object obj = proceedingJoinPoint.proceed();
        String collectionName = "";

        Object[] args = proceedingJoinPoint.getArgs();
        if (args.length > 0) {
            if (args[0] instanceof String) {
                collectionName = (String) args[0];
            }
        }

        if (sliSchemaVersionValidator != null) {
            return sliSchemaVersionValidator.migrate(collectionName, (Iterable<Entity>) obj, repository);
        } else {
            return obj;
        }
    }
}
