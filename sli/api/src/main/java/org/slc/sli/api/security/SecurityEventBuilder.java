package org.slc.sli.api.security;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.logging.SecurityEvent;


/**
 * Utility class to fill in common SecurityEvent details
 */
@Component
public class SecurityEventBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityEventBuilder.class);

    @Autowired
    private CallingApplicationInfoProvider callingApplicationInfoProvider;

    private String thisNode;
    private String thisProcess;

    public SecurityEventBuilder() {
        try {
            InetAddress host = InetAddress.getLocalHost();
            thisNode = host.getHostName();
        } catch (UnknownHostException e) {
            LOG.info("Could not find hostname/process for SecurityEventLogging!");
        }
        thisProcess = ManagementFactory.getRuntimeMXBean().getName();
    }

    public SecurityEvent createSecurityEvent(String loggingClass, UriInfo requestUri, String slMessage) {
        SecurityEvent event = new SecurityEvent();

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                SLIPrincipal principal = (SLIPrincipal) auth.getPrincipal();
                if (principal != null) {
                    event.setTenantId(principal.getTenantId());
                    event.setUser(principal.getExternalId() + ", " + principal.getName());
                }
                Object credential = auth.getCredentials();
                if (credential != null) {
                    event.setCredential(credential.toString());
                }
            }

            event.setTargetEdOrg("UnknownTragetEdOrg");
            if (requestUri != null) {
                event.setActionUri(requestUri.getRequestUri().toString());
            }
            event.setAppId(callingApplicationInfoProvider.getClientId());
            event.setTimeStamp(new Date());
            event.setProcessNameOrId(thisProcess);
            event.setExecutedOn(thisNode);
            //event.setOrigin(String origin);
            //event.setUserOrigin(String userOrigin);
            event.setClassName(loggingClass);
            event.setLogLevel(LogLevelType.TYPE_INFO);
            event.setLogMessage(slMessage);

            if (LOG.isDebugEnabled()) {
                LOG.debug(event.toString());
            }
        } catch (Exception e) {
            LOG.info("Could not build SecurityEvent for [" + requestUri + "] [" + slMessage + "]");
        }
        return event;
    }

    public CallingApplicationInfoProvider getCallingApplicationInfoProvider() {
        return callingApplicationInfoProvider;
    }

    public void setCallingApplicationInfoProvider(
            CallingApplicationInfoProvider callingApplicationInfoProvider) {
        this.callingApplicationInfoProvider = callingApplicationInfoProvider;
    }

}
