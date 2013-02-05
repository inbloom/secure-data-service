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

package org.slc.sli.modeling.wadl.helpers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;

/**
 * JUnit test for WadlWalker class.
 * 
 * @author wscott
 * 
 */
public class WadlWalkerTest {

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullHandler() {
        new WadlWalker(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWalkNullApplication() {
        WadlHandler mockHandler = mock(WadlHandler.class);
        WadlWalker ww = new WadlWalker(mockHandler);
        ww.walk(null);
    }

    @Test
    public void testWalk() {
        WadlHandler wadlHandler = mock(WadlHandler.class);
        WadlWalker ww = new WadlWalker(wadlHandler);

        List<Resource> emptyResourcesList = new ArrayList<Resource>(0);

        Resources mockResources = mock(Resources.class);
        when(mockResources.getResources()).thenReturn(emptyResourcesList);

        Application mockApplication = mock(Application.class);
        when(mockApplication.getResources()).thenReturn(mockResources);

        ww.walk(mockApplication);

        verify(wadlHandler).beginApplication(mockApplication);
        verify(wadlHandler).endApplication(mockApplication);
    }

}
