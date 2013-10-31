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
package org.slc.sli.common.util.datetime;


import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: npandey
 */
public class DateHelperTest {

    DateHelper dateHelper;

    @Before
    public void setup() {
        dateHelper = new DateHelper();
    }

    @Test
    public void testIsFieldExpired() {
        Map<String, Object> body = generateEntityBody();
        Assert.assertTrue(dateHelper.isFieldExpired(body, "endDate", false));

        body.put("exitWithdrawDate", "2070-10-25");
        Assert.assertFalse(dateHelper.isFieldExpired(body, "exitWithdrawDate", false));
    }

    @Test
    public void testGetDate() {
        Map<String, Object> body = generateEntityBody();

        DateTime result = dateHelper.getDate(body, "endDate");

        Assert.assertEquals(2010, result.getYear());
        Assert.assertEquals(07, result.getMonthOfYear());
        Assert.assertEquals(10, result.getDayOfMonth());

        result = dateHelper.getDate(body, "expirationDate");

        Assert.assertNull(result);
    }

    private Map<String, Object> generateEntityBody() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("id", "1234");
        body.put("endDate", "2010-07-10");
        return body;
    }

}
