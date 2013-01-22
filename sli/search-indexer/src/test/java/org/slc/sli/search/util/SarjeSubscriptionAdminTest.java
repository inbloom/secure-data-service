package org.slc.sli.search.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.Before;
import org.junit.Test;
import org.slc.sli.search.config.IndexConfigStore;
import org.slc.sli.search.util.SarjeSubscriptionAdmin.Subscription;
import org.slc.sli.search.util.SarjeSubscriptionAdmin.SubscriptionTrigger;

public class SarjeSubscriptionAdminTest {
    private SarjeSubscriptionAdmin sarjeSubscriptionAdmin;
    private static final String QUEUE_NAME = "search";
    
    @Before
    public void init() throws JsonParseException, JsonMappingException, IOException {
        sarjeSubscriptionAdmin = new SarjeSubscriptionAdmin();
        sarjeSubscriptionAdmin.setDbJobsCollection("jobs");
        sarjeSubscriptionAdmin.setIndexConfigStore(new IndexConfigStore("sarje-test-index-config.json"));
        sarjeSubscriptionAdmin.setSearchQueue(QUEUE_NAME);
    }
    
    @Test
    public void testGetSearchSubscription() {
        List<SubscriptionTrigger> triggers = Arrays.asList(
                new SubscriptionTrigger("student"),  new SubscriptionTrigger("section"),  new SubscriptionTrigger("test"),
                new SubscriptionTrigger("test1"),  new SubscriptionTrigger("test2"));
        Subscription subscription = new Subscription(SarjeSubscriptionAdmin.SEARCH_EVENT_ID, QUEUE_NAME, true, triggers);
        Assert.assertEquals(subscription, sarjeSubscriptionAdmin.getSearchSubscription());
    }
}
