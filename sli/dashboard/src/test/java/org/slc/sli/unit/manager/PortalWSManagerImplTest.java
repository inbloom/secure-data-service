package org.slc.sli.unit.manager;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.client.RESTClient;
import org.slc.sli.manager.impl.PortalWSManagerImpl;

/**
 * Testing PortalWSManagerImpl
 * @author agrebneva
 *
 */
public class PortalWSManagerImplTest {
    private PortalWSManagerImpl portalWSManager = new PortalWSManagerImpl();
    private RESTClient goodRestClient = new RESTClient() {
        @Override
        public String getJsonRequest(String path, boolean timeout) {
            return "";
        }
    };

    private RESTClient badRestClient = new RESTClient() {
        @Override
        public String getJsonRequest(String path, boolean timeout) {
            throw new IllegalArgumentException();
        }
    };

    @Before
    public void setUp() {
        portalWSManager.setPortalFooterUrl("");
        portalWSManager.setPortalHeaderUrl("");
    }

    @Test
    public void testHeaderFooterNoThrowingErrors() {
        portalWSManager.setRestClient(goodRestClient);
        Assert.assertNotNull(portalWSManager.getFooter(true));
        Assert.assertNotNull(portalWSManager.getHeader(true));
        portalWSManager.setRestClient(badRestClient);
        Assert.assertNotNull(portalWSManager.getFooter(true));
        Assert.assertNotNull(portalWSManager.getHeader(true));
    }
}
