package org.slc.sli.dal.adapter;

import org.slc.sli.dal.adapter.transform.DatabaseTransform;
import org.slc.sli.dal.adapter.transform.LocationTransform;
import org.slc.sli.dal.adapter.transform.TransformStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Configuration for schema mappings
 *
 * @author srupasinghe
 *
 */
@Configuration
public class TransformConfiguration {

    @Bean(name = "databaseTransformStore")
    public TransformStore<DatabaseTransform> getDatabaseTransformStore() {
        return new TransformStore<DatabaseTransform>(DatabaseTransform.class);
    }

    @Bean(name = "locationTransformStore")
    public TransformStore<LocationTransform> getLocationTransformStore() {
        return new TransformStore<LocationTransform>(LocationTransform.class);
    }
}
