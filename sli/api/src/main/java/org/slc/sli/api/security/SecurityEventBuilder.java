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
import java.util.*;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.security.context.APIAccessDeniedException;
import org.slc.sli.api.security.context.EdOrgOwnershipArbiter;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.common.constants.ParameterConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.context.resolver.EdOrgHelper;
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

    @Autowired
    private EdOrgHelper edOrgHelper;

    @Autowired
    private PagingRepositoryDelegate<Entity> repository;

    @Autowired
    private EdOrgOwnershipArbiter arbiter;

    @Autowired
    private EntityDefinitionStore entityDefinitionStore;

    private final String unknownEdOrg = "UNKNOWN";

    public SecurityEventBuilder() {
        try {
            InetAddress host = InetAddress.getLocalHost();
            thisNode = host.getHostName();
        } catch (UnknownHostException e) {
            info("Could not find hostname/process for SecurityEventLogging!");
        }
        thisProcess = ManagementFactory.getRuntimeMXBean().getName();
    }


    /**
     * Creates a security event when targetEdOrgs are irrelevant (targetEdOrgList is NOT set)
     *
     * @param loggingClass  java class logging the security event
     * @param requestUri    relevant URI
     * @param slMessage     security event message text
     * @param defaultTargetToUserEdOrg  whether or not to set targetEdOrgList to be userEdOrg by default
     * @return  security event with no targetEdOrgList set
     */
    public SecurityEvent createSecurityEvent(String loggingClass, URI requestUri, String slMessage, boolean defaultTargetToUserEdOrg) {
        return createSecurityEvent( loggingClass,  requestUri,  slMessage, null, null, null, defaultTargetToUserEdOrg);
    }

    /**
     * Creates a security event with explicitly specified targetEdOrgList based on the passed entity ids
     *
     * @param loggingClass      java class logging the security event
     * @param requestUri        relevant URI
     * @param slMessage         security event message text
     * @param entityType        type of the entity ids used to determine targetEdOrgs
     * @param entityIds         entity ids used to determine targetEdOrgs
     * @return security event with targetEdOrgList determined from the entities
     */
    public SecurityEvent createSecurityEvent(String loggingClass, URI requestUri, String slMessage,
                                             String entityType, String[] entityIds) {
        Set<String> targetEdOrgs = getTargetEdOrgStateIds(entityType, entityIds);
        return createSecurityEvent(loggingClass, requestUri, slMessage, null, null, targetEdOrgs, false);
    }

    /**
     * Creates a security event with explicitly specified targetEdOrgList based on the passed entities
     *
     * @param loggingClass      java class logging the security event
     * @param requestUri        relevant URI
     * @param slMessage         security event message text
     * @param entityType        type of the entity ids used to determine targetEdOrgs
     * @param entities          entities used to determine targetEdOrgs
     * @return security event with targetEdOrgList determined from the entities
     */
    public SecurityEvent createSecurityEvent(String loggingClass, URI requestUri, String slMessage,
                                             String entityType, Set<Entity> entities) {
        debug("Creating security event with targetEdOrgList determined from entities of type " + entityType);
        Set<String> targetEdOrgs = getTargetEdOrgStateIds(entityType, entities);
        return createSecurityEvent(loggingClass, requestUri, slMessage, null, null, targetEdOrgs, false);
    }

    /**
     * Creates a security event with explicitly specified targetEdOrgList via targetEdOrgIds
     *
     * @param loggingClass              java class logging the security event
     * @param requestUri                relevant URI
     * @param slMessage                 security event message text
     * @param explicitPrincipal         used instead of the principle from the security context
     * @param explicitRealmEntity       used instead of the realm from the security context
     * @param targetEdOrgs              targetEdOrg stateOrganizationId values (note these are not db ids)
     * @param defaultTargetToUserEdOrg  whether or not to set targetEdOrgList to be userEdOrg by default
     * @return  security event
     */
    public SecurityEvent createSecurityEvent(String loggingClass, URI requestUri, String slMessage, SLIPrincipal explicitPrincipal, Entity explicitRealmEntity,
                                             Set<String> targetEdOrgs, boolean defaultTargetToUserEdOrg) {
        SecurityEvent event = new SecurityEvent();

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                SLIPrincipal principal = explicitPrincipal;
                if (principal == null) {
                    principal = (SLIPrincipal) auth.getPrincipal();
                }
                if (principal != null) {
                    event.setTenantId(principal.getTenantId());
                    event.setUser(principal.getExternalId() + ", " + principal.getName());
                    event.setUserEdOrg(principal.getRealmEdOrg());

                    // override the userEdOrg if explicit realm passed
                    if (explicitRealmEntity != null) {
                        debug("Using explicit realm entity to get userEdOrg");
                        Map<String, Object> body = explicitRealmEntity.getBody();
                        if (body != null && body.get("edOrg") != null) {
                            event.setUserEdOrg((String) body.get("edOrg"));
                        }
                    } else if (event.getUserEdOrg() == null) {
                        debug("Determining userEdOrg from the current session");
                        Entity realmEntity = realmHelper.getRealmFromSession(principal.getSessionId());
                        Map<String, Object> body = realmEntity.getBody();
                        if (body != null && body.get("edOrg") != null) {
                            event.setUserEdOrg((String) body.get("edOrg"));
                        }
                    }
                }
            }

            // set targetEdOrgList
            if (targetEdOrgs != null && !targetEdOrgs.isEmpty()) {
                debug("Setting targetEdOrgList explicitly: " + targetEdOrgs);
                event.setTargetEdOrgList(new ArrayList<String>(targetEdOrgs));
            } else if (defaultTargetToUserEdOrg) {
                debug("Setting targetEdOrgList to be userEdOrg" + event.getUserEdOrg());
                if (event.getUserEdOrg() != null) {
                    List<String> defaultTargetEdOrgs = new ArrayList<String>();
                    defaultTargetEdOrgs.add(event.getUserEdOrg());
                    event.setTargetEdOrgList(defaultTargetEdOrgs);
                }
            } else {
                debug("Not explicitly specified, doing a best effort determination of targetEdOrg based on the request uri path: " + requestUri.getPath());
                Set<String> stateOrgIds = getTargetEdOrgStateIdsFromURI(requestUri);
                if (stateOrgIds != null && !stateOrgIds.isEmpty()) {
                    event.setTargetEdOrgList(new ArrayList<String>(stateOrgIds));
                }
            }

            if (auth != null) {
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

    private Set<String> getTargetEdOrgStateIdsFromURI(URI requestURI) {
        Set<String> targetEdOrgStateIds = null;

        if (requestURI != null) {
            String uriPath = requestURI.getPath();
            debug("Using URI path: " + uriPath + " to determine targetEdOrgs");
            if (uriPath != null) {
                String[] uriPathSegments = uriPath.split("/");

                // starting from the last uri segment, find the first entity id segment (non-resource value)
                // the preceding segment is the associated resource
                for (int i = uriPathSegments.length -1; i >= 0; i--) {
                    if (!ResourceNames.SINGULAR_LINK_NAMES.containsKey(uriPathSegments[i])) {
                        if (i > 0 && ResourceNames.SINGULAR_LINK_NAMES.containsKey(uriPathSegments[i-1])) {
                            String[] entityIds = uriPathSegments[i].split(",");  // some uri patterns can contain comma separated id lists
                            String entityType = ResourceNames.toEntityName(uriPathSegments[i-1]);
                            targetEdOrgStateIds = getTargetEdOrgStateIds(entityType, entityIds);
                        }
                    }
                }

            }
        }
        debug("From URI: " + requestURI + " got targetEdOrg state ids: " + targetEdOrgStateIds);
        return targetEdOrgStateIds;
    }

    private Set<String> getTargetEdOrgStateIds(String entityType, String[] entityIds) {
        Set<Entity> entities = getEntities(entityType, entityIds);

        return getTargetEdOrgStateIds(entityType, entities);
    }

    private Set<String> getTargetEdOrgStateIds(String entityType, Set<Entity> entities) {
        Set<String> targetEdOrgStateIds = null;

        // entityType should be set
        if (entityType == null) {
            return null;
        }

        if (entities == null || entities.isEmpty()) {
            return null;
        }

        try {
            targetEdOrgStateIds = getEdOrgStateIds(arbiter.findEdorgs(entities, entityType, false));
        } catch (APIAccessDeniedException nestedE) {
            // we were unable to determine the targetEdOrgs
            warn(nestedE.getMessage());
            return null;
        } catch (RuntimeException nestedE) {
            // we were unable to determine the targetEdOrgs
            warn(nestedE.getMessage());
            return null;
        }

        return targetEdOrgStateIds;
    }

    private Set<String> getEdOrgStateIds(Collection<Entity> edOrgs) {
        Set<String> edorgs = new HashSet<String>();

        for (Entity edOrg : edOrgs) {
            Map<String, Object> body = edOrg.getBody();
            if (body != null) {
                String stateId = (String) body.get(ParameterConstants.STATE_ORGANIZATION_ID);
                if (stateId != null && !stateId.isEmpty()) {
                    edorgs.add(stateId);
                }
            }
        }

        return edorgs;
    }

    private Set<Entity> getEntities(String entityType, String[] entityIds) {
        Set<Entity> entities = null;

        if (entityType != null && entityIds.length != 0) {
            entities = new HashSet<Entity>();
            for (int i = 0; i < entityIds.length; i++) {
                String id = entityIds[i];
                if (id == null || id.length() <= 0) {
                    continue;
                } else {
                    String collectionName = entityDefinitionStore.lookupByEntityType(entityType).getStoredCollectionName();
                    Entity entity = repository.findById(collectionName, id.trim());
                    if (entity == null) {
                        warn("Entity of type " + entityType + " with id " + id + " could not be found in the database.");
                    } else {
                        entities.add(entity);
                    }
                }
            }
        }
        return entities;
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
