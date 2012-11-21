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

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.slc.sli.modeling.jgen.JavaGenConfig;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.index.ModelIndex;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author jstokes
 */
public class Level3ClientPojoGeneratorTest {
    private Level3ClientPojoGenerator gen;

    @Before
    public void setup() {
        gen = new Level3ClientPojoGenerator();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDoModel() throws Exception {
        File mockDir = new File("sli/modeling/sdkgen/test");
        if (!mockDir.exists()) {
        	assertTrue(mockDir.mkdirs());
        }

        String targetPkgName = "com.slc.sli.test";
        ModelIndex mockIndex = mock(ModelIndex.class);
        JavaGenConfig jGenConfig = new JavaGenConfig(true);
        Map<String, ClassType> mockClassMap = mock(Map.class);
        Map mockDataMap = mock(Map.class);

        List<EnumType> mockEnumList = new ArrayList<EnumType>();
        EnumType mockEnum = mock(EnumType.class);
        mockEnumList.add(mockEnum);

        List<DataType> mockDataList = new ArrayList<DataType>();
        DataType mockDataType = mock(DataType.class);
        mockDataList.add(mockDataType);

        List<ClassType> mockClassList = new ArrayList<ClassType>();
        ClassType mockClassType = mock(ClassType.class);
        mockClassList.add(mockClassType);

        when(mockIndex.getDataTypes()).thenReturn(mockDataMap);
        when(mockIndex.getEnumTypes()).thenReturn(mockEnumList);
        when(mockIndex.getClassTypes()).thenReturn(mockClassMap);
        when(mockClassMap.values()).thenReturn(mockClassList);
        when(mockClassType.isClassType()).thenReturn(true);
        when(mockClassType.getName()).thenReturn("TestClass");
        when(mockDataMap.values()).thenReturn(mockDataList);
        when(mockEnum.getName()).thenReturn("TestEnum");
        when(mockDataType.getName()).thenReturn("TestDataType");

        Level3ClientPojoGenerator.doModel(mockIndex, mockDir, targetPkgName, jGenConfig);

        List<String> files = Arrays.asList(mockDir.list());
        assertNotNull(files);
        assertFalse(files.isEmpty());

        assertTrue(files.contains("TestClass.java"));
        assertTrue(files.contains("TestEnum.java"));
        assertTrue(files.contains("TestDataType.java"));

        FileUtils.deleteDirectory(mockDir);
    }
}
