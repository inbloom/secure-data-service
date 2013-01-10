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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slc.sli.modeling.jgen.MockJavaStreamWriter;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Grammars;
import org.slc.sli.modeling.rest.Include;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author jstokes
 */
public class Level2ClientImplementationWriterTest {
    private Level2ClientImplementationWriter writer;

    @Mock
    private Application mockApp;

    private MockJavaStreamWriter jsw;

    @Before
    public void setup() throws URISyntaxException, UnsupportedEncodingException {
        initMocks(this);
        jsw = new MockJavaStreamWriter();

        List<String> interfaceList = new ArrayList<String>();
        File wadlFile = new File(getClass().getResource("/test_wadl.wadl").toURI());

        Grammars mockGrammars = mock(Grammars.class);
        List<Include> mockList = new ArrayList<Include>();
        Include mockInclude = mock(Include.class);
        when(mockInclude.getHref()).thenReturn("test_sli.xsd");
        mockList.add(mockInclude);

        when(mockGrammars.getIncludes()).thenReturn(mockList);
        when(mockApp.getGrammars()).thenReturn(mockGrammars);
        writer = new Level2ClientImplementationWriter("org.slc.test", "MyClass", interfaceList, wadlFile, jsw);
    }

    @Test
    public void testBeginApplication() throws Exception {
        writer.beginApplication(mockApp);

        String output = jsw.read();

        assertTrue(output.contains("import java.io.IOException;"));
        assertTrue(output.contains("import java.net.URISyntaxException;"));
        assertTrue(output.contains("import java.net.URI;"));
        assertTrue(output.contains("import java.util.List;"));
        assertTrue(output.contains("import java.util.Map;"));

        assertTrue(output.contains("public MyClass"));
    }


    @Test
    @SuppressWarnings("unchecked")
    public void testBeginResource() throws Exception {
        writer.beginResource(mock(Resource.class), mock(Resources.class), mock(Application.class), mock(Stack.class));
    }

    @Test
    public void testEndApplication() throws Exception {
        writer.endApplication(mockApp);

        String output = jsw.read();

        assertTrue(output.contains("}"));
    }

    @Test
    public void testEndResource() throws Exception {
        writer.endResource(mock(Resource.class), mock(Resources.class), mock(Application.class), mock(Stack.class));
    }
}
