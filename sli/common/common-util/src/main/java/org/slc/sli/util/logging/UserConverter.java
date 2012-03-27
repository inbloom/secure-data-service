package org.slc.sli.util.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Converts username into a string that Logback can use.
 * 
 * @author smelody
 * 
 */
public class UserConverter extends ClassicConverter {
	@Override
	public String convert(ILoggingEvent event) {

		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		if (auth != null) {
			Object oPrincipal = auth.getPrincipal();
			String principal = "";
			if (oPrincipal != null) {
				principal = oPrincipal.toString();
			}
			return principal;

		}
		return "NO_USER";
	}
}