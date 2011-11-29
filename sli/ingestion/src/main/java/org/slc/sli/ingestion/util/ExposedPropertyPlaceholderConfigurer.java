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
 * @see http://forum.springsource.org/showthread.php?69605-Spring-3M2-Value-and-PropertyPlaceholderConfigurer&highlight=@Value%20propertyplaceholderconfigurer
 * 
 */
public class ExposedPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
	private Map<String, String> properties;

	@SuppressWarnings("unchecked")
	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
		Map<String, String> tmpProperties = new HashMap<String, String>(props.size());
		super.processProperties(beanFactoryToProcess, props);
		for (Entry entry : props.entrySet()) {
			tmpProperties.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
		}
		this.properties = Collections.unmodifiableMap(tmpProperties);
	}

	public Map<String, String> getProperties() {
		return this.properties;
	}

}
