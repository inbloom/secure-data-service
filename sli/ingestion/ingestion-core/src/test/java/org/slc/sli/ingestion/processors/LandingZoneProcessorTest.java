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

package org.slc.sli.ingestion.processors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.ingestion.tenant.TenantDA;

/**
 * Tests for LandingZoneProcessor
 *
 * @author tshewchuk
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class LandingZoneProcessorTest {

    @InjectMocks
    @Autowired
    private LandingZoneProcessor landingZoneProcessor;

    @Mock
    private TenantDA mockedTenantDA;

    @Before
    public void setup() {
        // Setup the mocks.
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test to check that an lz submitted to LandingZoneProcessor is valid.
     *
     * @throws Exception
     */
    @Test
    @Ignore
    public void testValidLz() {

        File validLzPathFile = new File("/test/lz/inbound/TEST-LZ/testFile.zip");
        String validLzPathname = validLzPathFile.getParent();
        List<String> testLzPaths = new ArrayList<String>();
        testLzPaths.add(validLzPathname);
        when(mockedTenantDA.getLzPaths(Mockito.any(String.class))).thenReturn(testLzPaths);

        // Submit a valid landing zone.
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(validLzPathFile.getPath(), String.class);
        try {
            landingZoneProcessor.process(exchange);
        } catch (Exception e) {
            fail();
        }

        // Check there is no error in the exchange.
        assertEquals("Header on exchange should indicate success", false,
                exchange.getIn().getHeader("hasErrors"));
    }

    /**
     * Test to check that an lz submitted to LandingZoneProcessor is invalid.
     *
     * @throws Exception
     */
    @Test
    @Ignore
    public void testInvalidLz() {

        File validLzPathFile = new File("/test/lz/inbound/TEST-LZ/testFile.zip");
        String validLzPathname = validLzPathFile.getParent();
        List<String> testLzPaths = new ArrayList<String>();
        testLzPaths.add(validLzPathname);
        when(mockedTenantDA.getLzPaths(Mockito.any(String.class))).thenReturn(testLzPaths);

        // Submit an invalid landing zone.
        File inValidLzPathFile = new File("/test/lz/inbound/BAD-TEST-LZ/testFile.zip");
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(inValidLzPathFile.getPath(), String.class);
        try {
            landingZoneProcessor.process(exchange);
        } catch (Exception e) {
            fail();
        }

        // Check there is an error in the exchange.
        assertEquals("Header on exchange should indicate failure", true,
                exchange.getIn().getHeader("hasErrors"));
    }

}
