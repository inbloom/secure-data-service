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
package extract;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test bulk extraction into zip files.
 *
 * @author tshewchuk
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ExtractorImplTest {

    private final String TENANT1 = "Midgar";
    private final String TENANT2 = "Hyrule";
    private final String extractDir = "data/tmp";

    private final List<String> tenants = Arrays.asList(new String[] { TENANT1, TENANT2 });

    private List<String> collections;

//    @Autowired
//    private Repository<Entity> mongoEntityRepository;

//    private final ExtractorImpl extractor = new ExtractorImpl();
    @Autowired
    private  ExtractorImpl extractor;

    @Before
    public void init() throws IOException {
        collections = new ArrayList<String>();
        collections.add("student");
        collections.add("assessment");
        collections.add("staff");
        collections.add("staffEducationOrganizationAssociation");

//        extractor.setCollections(collections);
//        extractor.setEntityRepository(mongoEntityRepository);
        extractor.setTenants(tenants);
        extractor.setExtractDir(extractDir);
        extractor.init();
    }

    @After
    public void destroy() {
        extractor.destroy();
    }

    /**
     * Test extraction of multiple tenants into separate zip files.
     *
     * @throws Exception
     */
    @Test
    public void testExtractMultipleTenantsIntoSeparateZipFiles() throws Exception {
        extractor.execute();
        File zipFile1 = new File(extractDir, TENANT1 + ".zip");
        Assert.assertTrue(zipFile1.length() > 0);
        File zipFile2 = new File(extractDir, TENANT2 + ".zip");
        Assert.assertTrue(zipFile2.length() > 0);
    }

}
