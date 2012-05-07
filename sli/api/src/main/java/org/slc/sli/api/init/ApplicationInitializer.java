package org.slc.sli.api.init;

import java.io.FileInputStream;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.mongodb.util.JSON;

/**
 * 
 * @author pwolf
 *
 */
@Component
public class ApplicationInitializer {


    @Value("${sli.conf}")
    private String propsFile;

    @Autowired
    private Repository<Entity> repository;

    private static final String APP_RESOURCE = "application";


    @PostConstruct
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void init() {
        //not yet ready to enable
        /*try {
            FileInputStream fis = new FileInputStream(propsFile);
            Properties props = new Properties();
            props.load(fis);
            fis.close();

            String[] appKeys = props.getProperty("bootstrap.app.keys").split(",");
            for (String key : appKeys) {
                String templateKey = "bootstrap.app." + key + ".template";
                if (props.containsKey(templateKey)) {
                    InputStream is = new ClassPathResource(props.getProperty(templateKey)).getInputStream();
                    Map appData = loadJsonFile(is, key, props);
                    is.close();
                    writeApplicationToMongo(appData);
                }

            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }

    private void writeApplicationToMongo(Map appData) {
        Entity app = findExistingApp(appData);
        if (app == null) {
            info("Creating boostrap application data for {}", appData.get("name"));
            repository.create(APP_RESOURCE, appData);
        } else {
            //check if we need to update it
            long oldAppHash = InitializerUtils.quickHash(app.getBody());
            long newAppHash = InitializerUtils.quickHash(appData);
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
    Entity findExistingApp(Map appData) {
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

    Map loadJsonFile(InputStream is, String appKey, Properties props) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer);
        String template = writer.toString();
        template = replaceVars(template, appKey, props);

        return (Map) JSON.parse(template);
    }

    private String replaceVars(String template, String appKey, Properties props) {
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            if (((String) entry.getKey()).startsWith("bootstrap.app." + appKey)) {
                template = replaceVar(template, ((String) entry.getKey()).substring(14), (String) entry.getValue());
            }
        }

        return template;
    }

    private String replaceVar(String template, String key, String value) {
        return template.replaceAll("\\$\\{" + key + "\\}", value);
    }
}
