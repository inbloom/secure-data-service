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


package org.slc.sli.ingestion.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * A property resource configurer that exposes the configured properties.
 *
 * @author Pablo Krause
 * @see http://forum.springsource.org/showthread.php?69605-Spring-3M2-Value-and-
 *      PropertyPlaceholderConfigurer&highlight=@Value%20propertyplaceholderconfigurer
 *
 */
public class ExposedPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
    private Map<String, String> properties;

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
            throws BeansException {
        Map<String, String> tmpProperties = new HashMap<String, String>(props.size());
        super.processProperties(beanFactoryToProcess, props);
        for (Entry<Object, Object> entry : props.entrySet()) {
            tmpProperties.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }
        this.properties = Collections.unmodifiableMap(tmpProperties);
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

}
