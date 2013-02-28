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


package org.slc.sli.api.security;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.context.resolver.RealmHelper;
import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.domain.Entity;

/**
 * Utility class to fill in common SecurityEvent details
 */
@Component
public class SecurityEventBuilder {
    @Autowired
    private CallingApplicationInfoProvider callingApplicationInfoProvider;

    private String thisNode;
    private String thisProcess;

    @Autowired
    private RealmHelper realmHelper;

    public SecurityEventBuilder() {
        try {
            InetAddress host = InetAddress.getLocalHost();
            thisNode = host.getHostName();
        } catch (UnknownHostException e) {
            info("Could not find hostname/process for SecurityEventLogging!");
        }
        thisProcess = ManagementFactory.getRuntimeMXBean().getName();
    }

    public SecurityEvent createSecurityEvent(String loggingClass, URI requestUri, String slMessage) {
        SecurityEvent event = new SecurityEvent();

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                SLIPrincipal principal = (SLIPrincipal) auth.getPrincipal();
                if (principal != null) {
                    event.setTenantId(principal.getTenantId());
                    event.setUser(principal.getExternalId() + ", " + principal.getName());
                    Entity realmEntity = realmHelper.getRealmFromSession(principal.getSessionId());
            		if (realmEntity != null) {
                        String realmId = realmEntity.getEntityId();
                        Map<String, Object> body = realmEntity.getBody();
                        if (body != null) {
                            event.setUserEdOrg((String) body.get("edOrg"));
            				String stateOrgId = (String) body.get("edOrg");
        		            event.setTargetEdOrg(stateOrgId);
				            event.setTargetEdOrgList(Arrays.asList(stateOrgId));
            			}
            		}
                }
                Object credential = auth.getCredentials();
                if (credential != null) {
                    event.setCredential(credential.toString());
                }
            }

            setSecurityEvent(loggingClass, requestUri, slMessage, event);

            debug(event.toString());

        } catch (Exception e) {
            info("Could not build SecurityEvent for [" + requestUri + "] [" + slMessage + "]");
        }
        return event;
    }


    public SecurityEvent createSecurityEvent(String loggingClass, URI requestUri, String slMessage, SLIPrincipal principal, Entity realmEntity) {
        SecurityEvent event = new SecurityEvent();

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                if (principal != null) {
                    event.setTenantId(principal.getTenantId());
                    event.setUser(principal.getExternalId() + ", " + principal.getName());
            		if (realmEntity != null) {
                        String realmId = realmEntity.getEntityId();
                        Map<String, Object> body = realmEntity.getBody();
                        if (body != null) {
                            event.setUserEdOrg((String) body.get("edOrg"));
            				String stateOrgId = (String) body.get("edOrg");
				            event.setTargetEdOrg(stateOrgId);
				            event.setTargetEdOrgList(Arrays.asList(stateOrgId));
            			}
            		}
                }
                Object credential = auth.getCredentials();
                if (credential != null) {
                    event.setCredential(credential.toString());
                }
            }

            setSecurityEvent(loggingClass, requestUri, slMessage, event);

            debug(event.toString());

        } catch (Exception e) {
            info("Could not build SecurityEvent for [" + requestUri + "] [" + slMessage + "]");
        }
        return event;
    }

	private void setSecurityEvent(String loggingClass, URI requestUri,
			String slMessage, SecurityEvent event) {
		if (requestUri != null) {
		    event.setActionUri(requestUri.toString());
		}
		event.setAppId(callingApplicationInfoProvider.getClientId());
		event.setTimeStamp(new Date());
		event.setProcessNameOrId(thisProcess);
		event.setExecutedOn(thisNode);
		event.setClassName(loggingClass);
		event.setLogLevel(LogLevelType.TYPE_INFO);
		event.setLogMessage(slMessage);
	}

    public CallingApplicationInfoProvider getCallingApplicationInfoProvider() {
        return callingApplicationInfoProvider;
    }

    public void setCallingApplicationInfoProvider(CallingApplicationInfoProvider callingApplicationInfoProvider) {
        this.callingApplicationInfoProvider = callingApplicationInfoProvider;
    }

}
