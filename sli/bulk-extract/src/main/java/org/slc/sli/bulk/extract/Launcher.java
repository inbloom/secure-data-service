package org.slc.sli.bulk.extract;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Launcher {
    private final static String USAGE = "Usage: bulk-extract <tenant>";
    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/application-context.xml");
        Extractor main = context.getBean(EntityExtractor.class);
        
        if (args.length != 1) {
            LOG.error(USAGE);
            return;
        }
        
        String tenantId = args[0];

        List<String> tenants = new ArrayList<String>();
        tenants.add(tenantId);
        try {
            main.init(tenants);
        } catch (FileNotFoundException e) {
            LOG.error("Invalid output directory");
            return;
        }
        
        main.execute();
        
        main.destroy();
        
    }

}
