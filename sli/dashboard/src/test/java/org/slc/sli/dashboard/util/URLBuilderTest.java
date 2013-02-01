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


/**
 *
 */
package org.slc.sli.dashboard.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author tosako
 *
 */
public class URLBuilderTest {

    @Test
    public void test() {
        String actualUrl = "http://www.wirelessgeneration.com/test_path/test_path2?key1=fake_value1&key2=fake%20value2";
        URLBuilder url = new URLBuilder("http://www.wirelessgeneration.com");
        url.addPath("test_path/");
        url.addPath("test_path2");
        url.addQueryParam("key1", "fake_value1");
        url.addQueryParam("key2", "fake value2");
        assertEquals("URL should be \"" + actualUrl + "\"", actualUrl, url.toString());
    }

}
