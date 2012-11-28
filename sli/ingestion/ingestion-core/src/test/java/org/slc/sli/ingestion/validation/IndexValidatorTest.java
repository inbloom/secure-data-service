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

package org.slc.sli.ingestion.validation;

import java.util.HashMap;
import java.util.regex.Matcher;

import junitx.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author unavani
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class IndexValidatorTest {

    @Autowired
    IndexValidator indexValidator;

    @Test
    public void compareEnsureIndexStatement() {
        Matcher matcher = indexValidator.ensureIndexStatement("db[\"assessment\"].ensureIndex({\"batchJobId\" : 1, \"creationTime\":1});");
        Assert.assertFalse(matcher == null);
    }

    @Test
    public void parseJson() {
        HashMap<String, Object> value = (HashMap<String, Object>) indexValidator.parseJson("{\"batchJobId\" : 1, \"creationTime\":1}");
        Assert.assertFalse(value == null);
        Assert.assertFalse(!value.get("batchJobId").equals(1));
        Assert.assertFalse(!value.get("creationTime").equals(1));
    }

}
