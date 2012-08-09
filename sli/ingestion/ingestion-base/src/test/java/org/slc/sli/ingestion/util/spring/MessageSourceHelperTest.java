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


package org.slc.sli.ingestion.util.spring;

import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.context.support.StaticMessageSource;

/**
 * MessageSourceHelper unit-tests
 *
 * @author okrook
 *
 */
public class MessageSourceHelperTest {

    @Test
    public void testMessageSourceHelper() {
        StaticMessageSource ms = new StaticMessageSource();

        ms.addMessage("MSG1", Locale.getDefault(), "This is message 1");

        ms.addMessage("MSG2", Locale.getDefault(), "This is message 2 with param: {0}");

        String actual = MessageSourceHelper.getMessage(ms, "MSG1");
        Assert.assertEquals("This is message 1", actual);

        actual = MessageSourceHelper.getMessage(ms, "MSG1", "Test Param");
        Assert.assertEquals("This is message 1", actual);

        actual = MessageSourceHelper.getMessage(ms, "MSG2", "Test Param");
        Assert.assertEquals("This is message 2 with param: Test Param", actual);
    }

    @Test(expected = NullPointerException.class)
    public void testNoMessageSource() {
        MessageSourceHelper.getMessage(null, "CODE");
    }
}
