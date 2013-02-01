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

package org.slc.sli.modeling.rest.helpers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.junit.Test;
import org.slc.sli.modeling.rest.Param;
import org.slc.sli.modeling.rest.ParamStyle;
import org.slc.sli.modeling.rest.Resource;

/**
 * JUnit test for RestHelper class.
 *
 * @author wscott
 *
 */
public class TestRestHelper {

    private static final String R1P1_ID = "r1p1";
    private static final String R2P1_ID = "r2p1";
    private static final String R2P2_ID = "r2p2";
    private static final String R3P1_ID = "r3p1";
    private static final String R3P2_ID = "r3p2";

    @Test
    public void testConstructor() {
        new RestHelper();
    }

    @Test
    public void testComputeRequestTemplateParamsEmptyAncestors() {
        Stack<Resource> resources = new Stack<Resource>();

        Param r1p1 = mock(Param.class);
        when(r1p1.getStyle()).thenReturn(ParamStyle.TEMPLATE);
        when(r1p1.getId()).thenReturn(R1P1_ID);

        List<Param> r1Params = new ArrayList<Param>(1);
        r1Params.add(r1p1);

        Resource r1 = mock(Resource.class);
        when(r1.getParams()).thenReturn(r1Params);

        List<Param> templateParams = RestHelper.computeRequestTemplateParams(r1, resources);
        assertEquals(1, templateParams.size());
        assertEquals(R1P1_ID, templateParams.get(0).getId());
    }

    @Test
    public void testComputeRequestTemplateParams() {

        Stack<Resource> resources = new Stack<Resource>();

        // mock first resource
        Param r1p1 = mock(Param.class);
        when(r1p1.getStyle()).thenReturn(ParamStyle.TEMPLATE);
        when(r1p1.getId()).thenReturn(R1P1_ID);

        List<Param> r1Params = new ArrayList<Param>(1);
        r1Params.add(r1p1);

        Resource r1 = mock(Resource.class);
        when(r1.getParams()).thenReturn(r1Params);

        // mock second resource
        Param r2p1 = mock(Param.class);
        when(r2p1.getStyle()).thenReturn(ParamStyle.TEMPLATE);
        when(r2p1.getId()).thenReturn(R2P1_ID);
        Param r2p2 = mock(Param.class);
        when(r2p2.getStyle()).thenReturn(ParamStyle.QUERY);
        when(r2p2.getId()).thenReturn(R2P2_ID);

        List<Param> r2Params = new ArrayList<Param>(2);
        r2Params.add(r2p1);
        r2Params.add(r2p2);

        Resource r2 = mock(Resource.class);
        when(r2.getParams()).thenReturn(r2Params);

        // mock third resource
        Param r3p1 = mock(Param.class);
        when(r3p1.getStyle()).thenReturn(ParamStyle.TEMPLATE);
        when(r3p1.getId()).thenReturn(R3P1_ID);
        Param r3p2 = mock(Param.class);
        when(r3p2.getStyle()).thenReturn(ParamStyle.TEMPLATE);
        when(r3p2.getId()).thenReturn(R3P2_ID);

        List<Param> r3Params = new ArrayList<Param>(2);
        r3Params.add(r3p1);
        r3Params.add(r3p2);

        Resource r3 = mock(Resource.class);
        when(r3.getParams()).thenReturn(r3Params);

        resources.push(r2);
        resources.push(r3);

        List<Param> templateParams = RestHelper.computeRequestTemplateParams(r1, resources);
        assertEquals(4, templateParams.size());
        assertEquals(R3P1_ID, templateParams.get(0).getId());
        assertEquals(R3P2_ID, templateParams.get(1).getId());
        assertEquals(R2P1_ID, templateParams.get(2).getId());
        assertEquals(R1P1_ID, templateParams.get(3).getId());
    }

    @Test
    public void testReverse() {
        List<Integer> numbers = new ArrayList<Integer>(3);
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);

        List<Integer> reversedNumbers = RestHelper.reverse(numbers);
        Integer crntNumber = 3;
        assertEquals(3, reversedNumbers.size());
        for (Integer i : reversedNumbers) {
            assertEquals(crntNumber--, i);
        }
    }

    @Test
    public void testReverseEmpty() {
        List<Integer> numbers = new ArrayList<Integer>(0);

        List<Integer> reversedNumbers = RestHelper.reverse(numbers);
        assertEquals(0, reversedNumbers.size());
    }

    @Test
    public void testReverseOne() {
        List<Integer> numbers = new ArrayList<Integer>(1);
        numbers.add(9);

        List<Integer> reversedNumbers = RestHelper.reverse(numbers);
        assertEquals(1, reversedNumbers.size());
        assertEquals((Integer) 9, (Integer) reversedNumbers.get(0));
    }

}
