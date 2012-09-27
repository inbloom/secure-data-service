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
import org.slc.sli.modeling.jgen.JavaCollectionKind;
import org.slc.sli.modeling.jgen.JavaParam;
import org.slc.sli.modeling.jgen.JavaType;
import org.slc.sli.modeling.rest.Param;
import org.slc.sli.modeling.rest.ParamStyle;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author jstokes
 */
public class LevelNClientJavaHelperTest {

    @Test
    public void testComputeParams() throws Exception {
        List<Param> paramList = new ArrayList<Param>();
        Param mockParam = mock(Param.class);
        paramList.add(mockParam);

        JavaParam mockToken = mock(JavaParam.class);
        JavaParam mockRequest = mock(JavaParam.class);

        when(mockParam.getStyle()).thenReturn(ParamStyle.TEMPLATE);
        when(mockParam.getType()).thenReturn(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        when(mockParam.getName()).thenReturn("MockParam");

        List<JavaParam> retList = LevelNClientJavaHelper.computeParams(mockToken, paramList, mockRequest);

        assertNotNull(retList);
        assertFalse(retList.isEmpty());
        assertEquals(3, retList.size());
        assertEquals("MockParam", retList.get(1).getName());
    }

    @Test
    public void testComputeGenericType() throws Exception {
        JavaType mockType = mock(JavaType.class);

        when(mockType.getCollectionKind()).thenReturn(JavaCollectionKind.LIST);
        JavaType actual = LevelNClientJavaHelper.computeGenericType(mockType, true);

        assertEquals(LevelNClientJavaHelper.JT_LIST_OF_ENTITY, actual);

        when(mockType.getCollectionKind()).thenReturn(JavaCollectionKind.MAP);
        actual = LevelNClientJavaHelper.computeGenericType(mockType, true);
        assertEquals(LevelNClientJavaHelper.JT_MAP_STRING_TO_OBJECT, actual);
    }

    @Test
    public void testIsMapStringToObject() throws Exception {
        JavaType mockType = mock(JavaType.class);

        when(mockType.getCollectionKind()).thenReturn(JavaCollectionKind.LIST);
        assertFalse(LevelNClientJavaHelper.isMapStringToObject(mockType));

        when(mockType.getCollectionKind()).thenReturn(JavaCollectionKind.MAP);
        assertTrue(LevelNClientJavaHelper.isMapStringToObject(mockType));
    }

    @Test
    public void testIsEntityList() throws Exception {
        JavaType mockType = mock(JavaType.class);

        when(mockType.getCollectionKind()).thenReturn(JavaCollectionKind.LIST);
        when(mockType.primeType()).thenReturn(LevelNClientJavaHelper.JT_ENTITY);
        assertTrue(LevelNClientJavaHelper.isEntityList(mockType));

        when(mockType.getCollectionKind()).thenReturn(JavaCollectionKind.LIST);
        when(mockType.primeType()).thenReturn(LevelNClientJavaHelper.JT_LIST_OF_ENTITY);
        assertFalse(LevelNClientJavaHelper.isEntityList(mockType));

        when(mockType.getCollectionKind()).thenReturn(JavaCollectionKind.MAP);
        assertFalse(LevelNClientJavaHelper.isEntityList(mockType));
    }

    @Test
    public void testIsSingleton() throws Exception {
        JavaType mockType = mock(JavaType.class);

        when(mockType.getCollectionKind()).thenReturn(JavaCollectionKind.NONE);
        assertTrue(LevelNClientJavaHelper.isSingleton(mockType));

        when(mockType.getCollectionKind()).thenReturn(JavaCollectionKind.MAP);
        assertFalse(LevelNClientJavaHelper.isSingleton(mockType));
    }
}
