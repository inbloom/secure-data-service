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
package org.slc.sli.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.slc.sli.api.migration.ApiSchemaAdapter;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;


/**
 * Aspect for executing API migrations
 *
 * @author jtully
 */

@Aspect
public class ApiMigrationAspect {

    protected ApiSchemaAdapter apiSchemaAdapter;

    public void setApiSchemaAdapter(ApiSchemaAdapter apiSchemaAdapter) {
        this.apiSchemaAdapter = apiSchemaAdapter;
    }

    private final static String ENTITY_TYPE = "entityType";
    private final static String VERSION_1 = "v1";

    /*
     * Designates a method that migrates a response
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface MigrateResponse {
    }

    /*
     * Designates a method that migrates an entity
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface MigratePostedEntity {
    }

    /**
     * Migrate entities in a response (down transform) from the current DB schema version to the version of the API schema requested
     */
    @Around(value = "@annotation(annotation)")
    public Object migrateResponse(final ProceedingJoinPoint proceedingJoinPoint, final MigrateResponse annotation) throws Throwable {
        Object obj = proceedingJoinPoint.proceed();
        String namespace = "";

        Object[] args = proceedingJoinPoint.getArgs();
        if (args.length > 0) {
            if (args[0] instanceof Resource) {
                Resource resource = (Resource) args[0];
                namespace = resource.getNamespace();
            }
        }

        int version = getVersion(namespace);

        if (version > 0 && obj instanceof ServiceResponse) {
            ServiceResponse response = (ServiceResponse) obj;
            List<EntityBody> bodyList = response.getEntityBodyList();
            obj = transformResponse(response, version);
        }
        return obj;
    }

    /**
     * Migrate posted entity (up transform) from the specified API version to the latest DB schema version
     */
    @Around(value = "@annotation(annotation)")
    public Object migratePostedEntity(final ProceedingJoinPoint proceedingJoinPoint, final MigratePostedEntity annotation) throws Throwable {
        String namespace = "";

        EntityBody entityBody = null;
        int entityIndex = -1;

        Object[] args = proceedingJoinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof EntityBody) {
                entityBody = (EntityBody) args[i];
                entityIndex = i;
            } else if (args[i] instanceof Resource) {
                Resource resource = (Resource) args[i];
                namespace = resource.getNamespace();
            }
        }

        int version = getVersion(namespace);

        if (version > 0 && entityBody != null) {
            EntityBody transformedBody = transformEntityBody(entityBody, version, true);
            args[entityIndex] = transformedBody;
        }
        return proceedingJoinPoint.proceed(args);
    }

    private ServiceResponse transformResponse(ServiceResponse response, int version) {
        List<EntityBody> entityBodyList = response.getEntityBodyList();
        List<EntityBody> translatedEntities = new ArrayList<EntityBody>();

        for (EntityBody body : entityBodyList) {
            translatedEntities.add(transformEntityBody(body, 1, false));
        }

        return new ServiceResponse(translatedEntities, response.getEntityCount());
    }

    private EntityBody transformEntityBody(EntityBody entityBody, Integer version, boolean upTransform) {
        EntityBody translated = entityBody;

        Object entityTypeObj = entityBody.get(ENTITY_TYPE);
        if (entityTypeObj instanceof String) {
            String entityType = (String) entityTypeObj;

            //transform entity - have to transform into an entity and back again to fit framework
            Entity entity =  new MongoEntity(entityType, entityBody);
            Entity translatedEntity = apiSchemaAdapter.migrate(entity, version.toString(), upTransform);
            translated = new EntityBody();
            translated.putAll(translatedEntity.getBody());
        }

        return translated;
    }

    private int getVersion(String namespace) {
        int version = -1;
        if (VERSION_1.equals(namespace)) {
            version = 1;
        }
        return version;
    }
}
