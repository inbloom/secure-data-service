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
package extract;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Main class for Bulk Extractor.
 *
 * @author tshewchuk
 *
 */
@Component
public class Launcher {

    private static final Logger LOG = LoggerFactory.getLogger(ExtractorImpl.class);

    private ExtractorImpl extractor;

    /**
     * Main method for Bulk Extractor.
     *
     * @param args
     *            List of tenants to process.
     *
     */
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");
        Launcher launcher = context.getBean(Launcher.class);
        launcher.start(args);
    }

    /**
     * REAL Main method for Bulk Extractor.
     *
     * @param args
     *            List of tenants to process.
     *
     */
    public void start(String[] args) {
        extractor.setRunOnStartup(true);
        extractor.setExtractDir("data");

        List<String> tenants = new ArrayList<String>();
        for (String tenant : args) {
            tenants.add(tenant);
        }
        extractor.setTenants(tenants);

        try {
            extractor.init();
        } catch (FileNotFoundException e) {
            LOG.error("Error running Bulk Extractor", e);
        }
    }

    public void setExtractor(ExtractorImpl extractor) {
        this.extractor = extractor;
    }

}
