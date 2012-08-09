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


package org.slc.sli.ingestion.validation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;


/**
 * Proxy ErrorReport. Plays as a proxy. Keeps local hasError flag.
 *
 * @author dduran
 *
 */

public final class ProxyErrorReportTest {

    @Test
    public void testFatal() {
        ErrorReport mockedEr = mock(ErrorReport.class);
        ProxyErrorReport er = new ProxyErrorReport(mockedEr);

        er.fatal("Fatal Error", this);

        verify(mockedEr).fatal("Fatal Error", this);
        Assert.assertTrue(er.hasErrors());
    }

    @Test
    public void testError() {
        ErrorReport mockedEr = mock(ErrorReport.class);
        ProxyErrorReport er = new ProxyErrorReport(mockedEr);

        er.error("Error", this);

        verify(mockedEr).error("Error", this);
        Assert.assertTrue(er.hasErrors());
    }

    @Test
    public void warning() {
        ErrorReport mockedEr = mock(ErrorReport.class);
        ProxyErrorReport er = new ProxyErrorReport(mockedEr);

        er.warning("Warning", this);

        verify(mockedEr).warning("Warning", this);
        Assert.assertFalse(er.hasErrors());
    }

    @Test
    public void testHasErrors() {
        ErrorReport mockedEr = mock(ErrorReport.class);
        ProxyErrorReport er = new ProxyErrorReport(mockedEr);

        when(mockedEr.hasErrors()).thenReturn(false);
        verify(mockedEr, never()).hasErrors();
        er.warning("Error", this);
        Assert.assertFalse(er.hasErrors());

        er.error("Error", this);

        verify(mockedEr).error("Error", this);
        Assert.assertTrue(er.hasErrors());
    }

}
