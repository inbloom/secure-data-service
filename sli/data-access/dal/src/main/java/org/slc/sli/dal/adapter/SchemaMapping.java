package org.slc.sli.dal.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Configuration for schema mappings
 *
 * @author srupasinghe
 *
 */
@Configuration
public class SchemaMapping {

    @Autowired
    @Qualifier("mongoTemplate")
    private MongoTemplate template;

    private Map<String, Mappable> schemaMappings = new ConcurrentHashMap<String, Mappable>();

    @Bean (name = "schemaMappings")
    public Map<String, Mappable> getSchemaMappings() {

        //TODO - this is temporary, should come up with a better way to define mappings
        schemaMappings.put("section", new AttributeMapper("section", "populationServed", "adaptedAttribute"));
        schemaMappings.put("studentSectionAssociation", new LocationMapper(template, "studentSectionAssociation",
                "student", "studentId", "sections"));

        return  schemaMappings;
    }





}
