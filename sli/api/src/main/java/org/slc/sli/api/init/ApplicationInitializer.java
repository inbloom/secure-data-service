package org.slc.sli.api.init;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.PropertyPlaceholderHelper;

import com.mongodb.util.JSON;

/**
 * 
 * @author pwolf
 *
 */
@Component
public class ApplicationInitializer {

    @Autowired
    private Repository<Entity> repository;

    @Resource(name = "sliProperties")
    private Properties sliProps;

    private static final String APP_RESOURCE = "application";

    @PostConstruct
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void init() {
        try {

            String[] appKeys = sliProps.getProperty("bootstrap.app.keys").split(",");
            for (String key : appKeys) {
                String templateKey = "bootstrap.app." + key + ".template";
                if (sliProps.containsKey(templateKey)) {
                    InputStream is = null;
                    Map appData = null;
                    try {
                        is = new ClassPathResource(sliProps.getProperty(templateKey)).getInputStream();
                        appData = loadJsonFile(is);
                    } finally {
                        is.close();
                    }

                    writeApplicationToMongo(appData, sliProps.getProperty("bootstrap.app." + key + ".guid"));
                }

            }

        } catch (IOException e) {
            error("Error loading JSON template", e);
        }
    }

    private void writeApplicationToMongo(Map<String, Object> appData, String guid) {
        Entity app = findExistingApp(appData);
        
        if (guid != null && app != null && !app.getEntityId().equals(guid)) {
            repository.delete(APP_RESOURCE, guid);
            app = null;
        }
        
        if (app == null) {
            info("Creating boostrap application data for {}", appData.get("name"));
            
            if (guid != null) {
                Entity entity = new MongoEntity(APP_RESOURCE, guid, appData, new HashMap<String, Object>());
                repository.update(APP_RESOURCE, entity);
            } else {
                repository.create(APP_RESOURCE, appData);
            }
        } else {
            //check if we need to update it
            long oldAppHash = InitializerUtils.checksum(app.getBody());
            long newAppHash = InitializerUtils.checksum(appData);
            if (oldAppHash != newAppHash) {
                info("Updating bootstrap application data for {}", appData.get("name"));
                app.getBody().clear();
                app.getBody().putAll(appData);
                repository.update(APP_RESOURCE, app);
            }
        }
    }

    /**
     * Try to look up an app in mongo first by client ID and then by app name
     * @param appData
     * @return the app entity, or null if not found
     */
    Entity findExistingApp(Map<String, Object> appData) {
        //first try to find it by client_id
        Entity app = repository.findOne(APP_RESOURCE, new NeutralQuery(
                new NeutralCriteria("client_id", NeutralCriteria.OPERATOR_EQUAL, appData.get("client_id"))));

        //next try it by app name
        if (app == null) {
            app = repository.findOne(APP_RESOURCE, new NeutralQuery(
                    new NeutralCriteria("name", NeutralCriteria.OPERATOR_EQUAL, appData.get("name"))));
        }
        return app;
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> loadJsonFile(InputStream is) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer);
        String template = writer.toString();
        PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}"); 
        template = helper.replacePlaceholders(template, sliProps);
        return (Map<String, Object>) JSON.parse(template);
    }
}
