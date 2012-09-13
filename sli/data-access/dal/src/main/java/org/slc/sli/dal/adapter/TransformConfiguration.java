package org.slc.sli.dal.adapter;

import org.codehaus.jackson.map.ObjectMapper;
import org.slc.sli.dal.adapter.transform.DatabaseTransformStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Configuration for schema mappings
 *
 * @author srupasinghe
 *
 */
@Configuration
public class TransformConfiguration {

    @Autowired
    @Qualifier("mongoTemplate")
    private MongoTemplate template;

    @Autowired
    private DatabaseTransformStore databaseTransformStore;

    private ConcurrentMap<String, List<Mappable>> schemaMappings = new ConcurrentHashMap<String, List<Mappable>>();

    @Bean (name = "schemaMappings")
    public Map<String, List<Mappable>> getSchemaMappings() throws IOException {

        loadMappings();

        return  schemaMappings;
    }

    protected void loadMappings() throws IOException {
        InputStream fileStream = getClass().getResourceAsStream("/mappings/mappings.json");
        ObjectMapper objectMapper = new ObjectMapper();

        SchemaTransform[] transforms = objectMapper.readValue(fileStream, SchemaTransform[].class);

        for (SchemaTransform schemaTransform : transforms) {
            String elemType = schemaTransform.getType();
            List<SchemaMapping> mappings = schemaTransform.getTransforms();

            schemaMappings.putIfAbsent(elemType, new ArrayList<Mappable>());

            for (SchemaMapping mapping : mappings) {
                if (mapping.getType().equals("Location")) {
                    schemaMappings.get(elemType).add(new LocationMapper(template, mapping.getAttributes().get("type"),
                            mapping.getAttributes().get("collection"), mapping.getAttributes().get("key"),
                            mapping.getAttributes().get("subCollection")));
                } else if (mapping.getType().equals("Generic")) {
                    schemaMappings.get(elemType).add(new GenericMapper(databaseTransformStore, mapping.getAttributes().get("type")));
                }
            }

        }

    }





}
