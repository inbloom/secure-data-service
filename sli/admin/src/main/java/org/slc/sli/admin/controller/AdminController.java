package org.slc.sli.admin.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.admin.client.RESTClient;

/**
 * Base class for any controller needing access to the {@link RESTClient}
 *
 * @author pwolf
 */
public abstract class AdminController {

    @Autowired
    protected RESTClient restClient;

    protected String getToken(HttpSession session) {
        return (String) session.getAttribute("ADMIN_SESSION_ID");
    }

    public void setRESTClient(RESTClient client) {
        this.restClient = client;
    }
}
