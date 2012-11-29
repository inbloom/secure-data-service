/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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


package org.slc.sli.ingestion.referenceresolution;

import java.util.Map;

import org.milyn.Smooks;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.templating.TemplatingConfiguration;
import org.milyn.templating.freemarker.FreeMarkerTemplateProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author tke
 *
 */

public class FreeMarkerExtendedReferenceResolver implements ReferenceResolutionStrategy {
    public static final Logger LOG = LoggerFactory.getLogger(FreeMarkerExtendedReferenceResolver.class);

    private static final String DOCUMENT = "#document";

    private final SmooksExtendedReferenceResolver smooksResolver = new SmooksExtendedReferenceResolver();

    private Map<String, String> idRefConfigs;

    /**
     * resolve the reference
     *
     * @param xPath
     *            XPath-like element identifier
     * @param content
     *            the content of the referenced element in XML format
     * @return Resolved content in XML format. Null if the reference is not supported yet.
     */
    @Override
    public String resolve(String xPath, String content) {
        if (!reassureSmooksResolver(xPath)) {
            return null;
        }

        return smooksResolver.resolve(xPath, content);
    }

    private boolean reassureSmooksResolver(String xPath) {
        String ftlFile = idRefConfigs.get(xPath);

        if (ftlFile == null) {
            return false;
        }

        Smooks smooks = smooksResolver.getIdRefConfigs().get(xPath);

        if (smooks == null) {
            smooks = new Smooks();

            smooks.addConfiguration(new SmooksResourceConfiguration(DOCUMENT, "org.milyn.delivery.DomModelCreator"));

            FreeMarkerTemplateProcessor visitor = new FreeMarkerTemplateProcessor(new TemplatingConfiguration(ftlFile));

            smooks.addVisitor(visitor, DOCUMENT);

            //Not using putIfAbsent.
            //Atomicity is not as beneficial here.
            if (!smooksResolver.getIdRefConfigs().containsKey(xPath)) {
                smooksResolver.getIdRefConfigs().put(xPath, smooks);
            }
        }

        return true;
    }

    public Map<String, String> getIdRefConfigs() {
        return idRefConfigs;
    }

    public void setIdRefConfigs(Map<String, String> idRefConfigs) {
        this.idRefConfigs = idRefConfigs;
    }
}
