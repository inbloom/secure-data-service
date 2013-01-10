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

package org.slc.sli.modeling.sdkgen;

import org.junit.Test;
import org.slc.sli.modeling.jgen.JavaType;

import javax.xml.namespace.QName;

import static junit.framework.Assert.assertEquals;

/**
 * @author jstokes
 */
public class GenericJavaHelperTest {
    @Test
    public void testGetJavaType() throws Exception {
        GenericJavaHelper helper = new GenericJavaHelper();

        JavaType actual = GenericJavaHelper.getJavaType(
                new QName("http://www.w3.org/2001/XMLSchema", "string"));
        assertEquals(JavaType.JT_STRING, actual);
    }

    @Test(expected = AssertionError.class)
    public void testSad() {
        GenericJavaHelper.getJavaType(new QName("test"));
    }
}
