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

    private Map<String, List<Mappable>> schemaMappings = new ConcurrentHashMap<String, List<Mappable>>();

    @Bean (name = "schemaMappings")
    public Map<String, List<Mappable>> getSchemaMappings() throws IOException {

        loadMappings();

        //TODO - this is temporary, should come up with a better way to define mappings
        List<Mappable> ssaMaps = new ArrayList<Mappable>();
        ssaMaps.add(new LocationMapper(template, "studentSectionAssociation",
                "student", "studentId", "sections"));
        schemaMappings.put("studentSectionAssociation", ssaMaps);

        List<Mappable> sectionMaps = new ArrayList<Mappable>();
        sectionMaps.add(new GenericMapper(databaseTransformStore, "section"));
        schemaMappings.put("section", sectionMaps);

        return  schemaMappings;
    }

    protected void loadMappings() throws IOException {
        InputStream fileStream = getClass().getResourceAsStream("/mappings/mappings.json");
        ObjectMapper mapper = new ObjectMapper();

        Map<String, List<Mappable>> objects = mapper.readValue(fileStream, Map.class);

        System.out.print(objects);
    }





}
