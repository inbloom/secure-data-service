package org.slc.sli.dal.migration.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.dal.versioning.SliSchemaVersionValidator;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface MigrateEntity {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface MigrateEntityCollection {
    }

    @Around(value = "@annotation(annotation)")
    public Object migrateEntiy(final ProceedingJoinPoint proceedingJoinPoint, final MigrateEntity annotation) throws Throwable {
        Object obj = proceedingJoinPoint.proceed();

        if (sliSchemaVersionValidator != null) {
            return sliSchemaVersionValidator.migrate((Entity) obj, repository);
        } else {
            return obj;
        }
    }

    @Around(value = "@annotation(annotation)")
    public Object migrateEntiyCollection(final ProceedingJoinPoint proceedingJoinPoint, final MigrateEntityCollection annotation) throws Throwable {
        Object obj = proceedingJoinPoint.proceed();

        if (sliSchemaVersionValidator != null) {
            return sliSchemaVersionValidator.migrate((Iterable<Entity>) obj, repository);
        } else {
            return obj;
        }
    }
}
