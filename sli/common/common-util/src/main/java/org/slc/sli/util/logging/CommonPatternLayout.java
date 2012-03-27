package org.slc.sli.util.logging;

import ch.qos.logback.classic.PatternLayout;

/**
 * Provides custom patterns for including additional values in our log
 * statements.
 * 
 * @author smelody
 * 
 */
public class CommonPatternLayout extends PatternLayout {
	// TODO - shouldn't this be in our config files in 1 place?
	private static final String PATTERN = "%date{dd MMM yyyy HH:mm:ss.SSSZ} %-5level [%thread] %logger{10} [%user] - %msg%n";

	// Registers custom converters
	static {
		PatternLayout.defaultConverterMap.put("user",
				UserConverter.class.getName());
	}

	public CommonPatternLayout() {
		setPattern(PATTERN);
	}

}
