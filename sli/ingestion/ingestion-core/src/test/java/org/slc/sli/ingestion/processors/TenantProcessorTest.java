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

package org.slc.sli.ingestion.processors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.io.Files;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.ingestion.tenant.TenantDA;

/**
 * Tests for TenantProcessor
 *
 * @author jtully
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class TenantProcessorTest {

    @InjectMocks
    @Autowired
    private TenantProcessor tenantProcessor;

    @Mock
    private TenantDA mockedTenantDA;

    @Before
    public void setup() {
        // Setup the mocks.
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test to check that a single lz that is added to the
     * tenant collection is added by processor.
     *
     * @throws Exception
     */
    @Test
    public void shouldAddNewLz() throws Exception {

        List<String> testLzPaths = new ArrayList<String>();
        testLzPaths.add("."); // this must be a path that exists on all platforms
        when(mockedTenantDA.getLzPaths()).thenReturn(testLzPaths);

        // get a test tenantRecord
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        tenantProcessor.process(exchange);

        // check there is no error on the received message
        assertEquals("Header on exchange should indicate success", TenantProcessor.TENANT_POLL_SUCCESS, exchange
                .getIn().getHeader(TenantProcessor.TENANT_POLL_HEADER));
    }

    @Test
    public void testPreLoad() {
        File landingZone = Files.createTempDir();
        try {
            assertTrue(tenantProcessor.preLoad(landingZone.getAbsolutePath(), Arrays.asList("small")));
            assertTrue(landingZone.list(new WildcardFileFilter("preload-*.zip")).length == 1);
        } finally {
            landingZone.delete();
        }
    }

    @Test
    public void testPreLoadBadDataSet() {
        File landingZone = Files.createTempDir();
        try {
            assertTrue(!tenantProcessor.preLoad(landingZone.getAbsolutePath(), Arrays.asList("smallish")));
        } finally {
            landingZone.delete();
        }
    }
}
