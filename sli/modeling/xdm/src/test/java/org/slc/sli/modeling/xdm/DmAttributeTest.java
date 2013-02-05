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

package org.slc.sli.modeling.xdm;

import org.junit.Test;

import javax.xml.namespace.QName;

import static org.junit.Assert.assertTrue;

/**
 * JUnit test for DmAttribute class.
 */
public class DmAttributeTest {

    @Test
    public void testConstructorAndGetters() {
        String key = "foo";
        String value = "bar";
        QName qName = new QName(key);
        DmAttribute dmAttribute = new DmAttribute(qName, value);

        assertTrue(dmAttribute.getName() == qName);
        assertTrue(dmAttribute.getChildAxis().size() == 0);
        assertTrue(dmAttribute.getStringValue() == value);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullNameThrowsException() {
        String value = "bar";
        new DmAttribute(null, value);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullValueThrowsException() {
        String key = "foo";
        QName qName = new QName(key);
        new DmAttribute(qName, null);

    }

}
