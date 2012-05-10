package org.slc.sli.ingestion.processors;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Tests for TenantPollingTimer
 *
 * @author slee
 *
 */
public class TenantPollingTimerTest extends CamelTestSupport {

    private static int expectedCount = 10;
    
    @Test
    public void testTenantPollingTimerRoute() throws Exception {
        setUp();
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(expectedCount);

        assertMockEndpointsSatisfied();
        tearDown();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("quartz://tenantPollingTimer?trigger.fireNow=true&trigger.repeatCount=" + TenantPollingTimerTest.expectedCount + "&trigger.repeatInterval=100")
                .setBody().simple("TenantPollingTimer fired: ${header.firedTime}")
                .log(LoggingLevel.DEBUG, "Job.PerformanceMonitor", "TenantPollingTimer fired: ${header.firedTime}")
                .to("mock:result");
            }
        };
    }
}
