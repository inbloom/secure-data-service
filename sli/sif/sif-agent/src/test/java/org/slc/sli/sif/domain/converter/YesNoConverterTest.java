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

package org.slc.sli.sif.domain.converter;

import junit.framework.Assert;
import openadk.library.common.YesNo;

import org.junit.Test;

import org.slc.sli.sif.AdkTest;

/**
 * YesNoConverter unit tests
 */
public class YesNoConverterTest extends AdkTest {

    private final YesNoConverter converter = new YesNoConverter();

    @Test
    public void testNullObject() {
        Boolean result = converter.convert(null);
        Assert.assertNull("Result should be null", result);
    }

    @Test
    public void testYes() {
        Boolean result = converter.convert(YesNo.YES);
        Assert.assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testNo() {
        Boolean result = converter.convert(YesNo.NO);
        Assert.assertEquals(Boolean.FALSE, result);
    }

    @Test
    public void testUnknown() {
        Boolean result = converter.convert(YesNo.wrap("Unknown"));
        Assert.assertNull(result);
    }

}
