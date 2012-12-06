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

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.util.LogUtil;

/**
 *
 * @author tke
 *
 */

public class SmooksExtendedReferenceResolver implements ReferenceResolutionStrategy {
    public static final Logger LOG = LoggerFactory.getLogger(SmooksExtendedReferenceResolver.class);

    private static ThreadLocal<Map<String, Smooks>> threadLocalIdRefConfigs = new ThreadLocal<Map<String, Smooks>>() {
        @Override
        protected Map<String, Smooks> initialValue() {
            return new HashMap<String, Smooks>();
        }
    };

    /**
     * resolve the reference
     *
     * @param interchange
     *            : Name of interchange
     * @param element
     *            : name of element
     * @param reference
     *            : name of the reference
     * @param content
     *            : the content of the referenced element in XML format
     * @return : the resolved content in XML format. Null if the reference is not supported yet.
     */
    @Override
    public String resolve(String xPath, String content) {
        Smooks smooks = getIdRefConfigs().get(xPath);

        if (smooks == null) {
            return null;
        }

        String convertedContent = null;

        try {

            StreamSource source = new StreamSource(new StringReader(content));

            StringWriter stringWriter = new StringWriter();
            StreamResult result = new StreamResult(stringWriter);

            smooks.filterSource(source, result);

            convertedContent = stringWriter.toString();

        } catch (SmooksException se) {
            LogUtil.error(LOG, "Exception filtering idref xml through smooks", se);
        }
        return convertedContent;
    }

    public Map<String, Smooks> getIdRefConfigs() {
        return threadLocalIdRefConfigs.get();
    }
}
