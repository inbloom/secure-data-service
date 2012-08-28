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

package org.slc.sli.api.resources.security;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.mongodb.DBCollection;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;

/**
 * Resource available to SEA and Sandbox administrators to perform a tenant-level purge of the
 * database.
 *
 * @author tshewchuk
 */
@Component
@Scope("request")
@Path("/purge")
@Produces({ Resource.JSON_MEDIA_TYPE + ";charset=utf-8" })
public class TenantPurgeResource {

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    /**
     * Purge a tenant from the database.
     *
     * @param reqBody
     *            Request to perform tenant-level purge
     * @param uriInfo
     *            URI information
     *
     * @return Response
     *         Result of the purge request
     */
    @POST
    public Response purge(Map<String, String> reqBody, @Context final UriInfo uriInfo) {
        String tenantId = reqBody.get(ResourceConstants.ENTITY_METADATA_TENANT_ID);

        // Ensure the user is the correct admin.
        if (!SecurityUtil.hasRight(Right.TENANT_PURGE)) {
            EntityBody body = new EntityBody();
            body.put("response", "You are not authorized to purge tenant " + tenantId);
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }

        Response resp = purgeTenant(tenantId);
        return resp;
    }

    /**
     * Perform a tenant-level purge of the database
     *
     * @param tenantId
     *            The tenant to purge
     *
     * @return Response
     *         Result of the purge request
     */
    public Response purgeTenant(final String tenantId) {
        // Ensure the declared tenant exists.
        if (!tenantExists(tenantId)) {
            return Response.status(Status.NOT_FOUND).build();
        }

        // // Create a thread to purge the declared tenant.
        // TenantPurgeRunnable runnable = new TenantPurgeRunnable(tenantId, repo);

        // Send a JMS message to SARJE to implement purge in a separate thread.
        boolean sent = sendPurgeRequest(tenantId);
        if (!sent) {
            return Response.status(Status.EXPECTATION_FAILED).build();
        }
        return Response.status(Status.OK).build();
    }

    /**
     * Check if the specified tenant exists in the database
     *
     * @param tenantId
     *            The tenant to purge
     *
     * @return boolean
     *         Whether tenant exists or not
     */
    private boolean tenantExists(final String tenantId) {
        // Ensure the declared tenant exists.
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("body." + ResourceConstants.ENTITY_METADATA_TENANT_ID, "=", tenantId,
                false));
        Entity entity = repo.findOne(EntityNames.TENANT, query);
        if (entity == null) {
            return false;
        }
        return true;
    }

    /**
     * Create and send to ActiveMQ a JMS message to tell SARJE to initiate purge.
     *
     * @param tenantId
     *            The tenant to purge
     *
     * @return boolean
     *         Whether message was sent or not
     */
    private boolean sendPurgeRequest(final String tenantId) {
        try {
            // Create a queue sender
            ConnectionFactory connFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            Connection conn = connFactory.createConnection();
            conn.start();
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("ingestion.pit");
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            ObjectMessage objMessage = session.createObjectMessage("purge " + tenantId);

            // Send the message
            producer.send(objMessage);

            // Close the queue connection
            conn.close();
        } catch (JMSException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}

/**
 * Create a thread to execute a tenant-level purge of the database.
 *
 * @author tshewchuk
 */
class TenantPurgeRunnable implements Runnable {

    Thread t;
    private String tenantId;
    private Repository<Entity> repo;

    private List<String> excludeCollections;

    TenantPurgeRunnable(String tenantId, Repository<Entity> repo) {
        this.tenantId = tenantId;
        this.repo = repo;
        excludeCollections = new LinkedList<String>();
        t = new Thread(this, "purgeTenant");
        t.start();
    }

    @Override
    public void run() {
        // Purge the declared tenant.
        NeutralQuery searchTenantId = new NeutralQuery();
        searchTenantId.addCriteria(new NeutralCriteria("metaData." + ResourceConstants.ENTITY_METADATA_TENANT_ID, "=",
                tenantId, false));
        Iterable<DBCollection> collections = repo.getCollections(false);
        String collectionName;
        for (DBCollection collection : collections) {
            collectionName = collection.getName();
            if (isExcludedCollection(collectionName)) {
                continue;
            }
            for (Entity entity : repo.findAll(collectionName, searchTenantId)) {
                repo.delete(collectionName, entity.getEntityId());
            }
        }
    }

    private boolean isExcludedCollection(String collectionName) {
        for (String excludedCollectionName : excludeCollections) {
            if (collectionName.equals(excludedCollectionName)) {
                return true;
            }
        }
        return false;
    }

}
