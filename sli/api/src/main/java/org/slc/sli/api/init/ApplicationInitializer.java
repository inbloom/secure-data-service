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


package org.slc.sli.api.init;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.PropertyPlaceholderHelper;

import com.mongodb.util.JSON;

/**
 * 
 * Bootstraps application data during API startup.
 * 
 * The application data is contained within template files in the API, e.g. applications/admin.json.
 * 
 * During startup we look for two types of properties
 * <ol>
 * <li>bootstrap.app.keys - comma-separated list identifying which apps to bootstrap</li>
 * <li>bootstrap.app.<key>.template - path within API of the template file for a given app key</li>
 * </ol>
 * 
 * Before loading the template file into mongo, it performs string substitution on the ${...} tokens.
 *
 */
@Component
public class ApplicationInitializer {

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repository;
    
    @Value("file:${bootstrap.app.conf}")
    protected Resource bootstrapProperties;


    private static final String APP_RESOURCE = "application";

    @PostConstruct
    public void init() {
        if (bootstrapProperties.isReadable()) {
            Properties sliProp;
            try {
                sliProp = PropertiesLoaderUtils.loadProperties(bootstrapProperties);
                processTemplates(sliProp);
            } catch (IOException e) {
                error("Could not load boostrap properties.", e);
            }
        } else {
            warn("Could not find bootstrap properties at {}.", bootstrapProperties);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void processTemplates(Properties sliProps) {
        if (sliProps.containsKey("bootstrap.app.keys")) {
            String[] appKeys = sliProps.getProperty("bootstrap.app.keys").split(",");
            for (String key : appKeys) {
                String templateKey = "bootstrap.app." + key + ".template";
                if (sliProps.containsKey(templateKey)) {
                    try {
                        InputStream is = null;

                        try {
                            is = new ClassPathResource(sliProps.getProperty(templateKey)).getInputStream();
                            Map appData = loadJsonFile(is, sliProps);
                            writeApplicationToMongo(appData);
                        } finally {
                            is.close();
                        }
                    } catch (IOException e) {
                        error("Problem loading JSON template.", e);
                    }
                }
            }
        }
    }

    /**
     * Determine if an app needs to be created/updated in mongo, and if so, performs the operation
     * @param appData
     */
    private void writeApplicationToMongo(Map<String, Object> appData) {
        Entity app = findExistingApp(appData);

        if (app == null) {
            info("Creating boostrap application data for {}", appData.get("name"));
            repository.create(APP_RESOURCE, appData);
        } else {
            //check if we need to update it
            long oldAppHash = InitializerUtils.checksum(app.getBody());
            long newAppHash = InitializerUtils.checksum(appData);
            if (oldAppHash != newAppHash) {
                info("Updating bootstrap application data for {}", appData.get("name"));
                app.getBody().clear();
                app.getBody().putAll(appData);
                repository.update(APP_RESOURCE, app, false);
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

    /**
     * Load the JSON template and perform string substitution on the ${...} tokens
     * @param is
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    Map<String, Object> loadJsonFile(InputStream is, Properties sliProps) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer);
        String template = writer.toString();
        PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}"); 
        template = helper.replacePlaceholders(template, sliProps);
        return (Map<String, Object>) JSON.parse(template);
    }
}
