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

package org.slc.sli.modeling.xmi.comp;

import org.junit.Test;

import javax.xml.namespace.QName;

import org.junit.Assert;

/**
 * JUnit test for QNameComparator class.
 */
public class QNameComparatorTest {

    @Test
    public void test() {
        for (QNameComparator qNameComparator : QNameComparator.values()) {
            Assert.assertEquals(qNameComparator, QNameComparator.valueOf(qNameComparator.toString()));
        }
    }

    @Test
    public void testSingletonCompare() {
        String firstNamespace = "FIRST_NAMESPACE";
        String secondNamespace = "SECOND_NAMESPACE";
        String prefix = "PREFIX";

        QName absolutelyFirstQName = new QName(firstNamespace, "zzzzz", prefix);
        QName firstQName = new QName(secondNamespace, "first", prefix);
        QName secondQName = new QName(secondNamespace, "second", prefix);

        Assert.assertTrue(QNameComparator.SINGLETON.compare(absolutelyFirstQName, firstQName) < 0);
        Assert.assertTrue(QNameComparator.SINGLETON.compare(absolutelyFirstQName, secondQName) < 0);
        Assert.assertTrue(QNameComparator.SINGLETON.compare(firstQName, secondQName) < 0);
        Assert.assertTrue(QNameComparator.SINGLETON.compare(firstQName, firstQName) == 0);
        Assert.assertTrue(QNameComparator.SINGLETON.compare(secondQName, firstQName) > 0);
    }
}
