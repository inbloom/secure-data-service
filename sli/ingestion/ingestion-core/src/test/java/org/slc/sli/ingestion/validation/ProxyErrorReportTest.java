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
