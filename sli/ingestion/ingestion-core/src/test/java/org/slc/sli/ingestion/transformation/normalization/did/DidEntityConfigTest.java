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

package org.slc.sli.ingestion.transformation.normalization.did;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * DidEntityConfig unit tests.
 *
 */
public class DidEntityConfigTest {
    @Test
    public void shouldParseJson() throws Exception {
        Resource jsonFile = new ClassPathResource("DeterministicIdResolverConfigs/test_DID_entity_config.json");
        DidEntityConfig testEntityConfig = DidEntityConfig.parse(jsonFile.getInputStream());

        Assert.assertNotNull(testEntityConfig);

        Assert.assertNotNull(testEntityConfig.getReferenceSources());
        Assert.assertEquals(1, testEntityConfig.getReferenceSources().size());

        DidRefSource refSource = testEntityConfig.getReferenceSources().get(0);
        Assert.assertNotNull(refSource);
        Assert.assertEquals("entity_type", refSource.getEntityType());
        Assert.assertEquals("source_ref_path", refSource.getSourceRefPath());
    }
}
