package org.slc.sli.ingestion.validation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.JobSource;
import org.slc.sli.ingestion.reporting.impl.LoggingMessageReport;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class IndexValidatorExecutorTest {

    //@Autowired
    //private IndexValidatorExecutor indexValidatorExecutor;

    @Ignore
    public void testInit() throws Exception {

        //Validator<?> mockedSystemValidator = Mockito.mock(Validator.class);
        //LoggingMessageReport mockedLoggingMessageReport = Mockito.mock(LoggingMessageReport.class);
        //Source source = new JobSource(null);
        //ReportStats reportStats = new SimpleReportStats();
        //indexValidatorExecutor.setLoggingMessageReport(mockedLoggingMessageReport);
        //indexValidatorExecutor.setValidator(mockedSystemValidator);
        //Mockito.when(mockedSystemValidator.isValid(null, mockedLoggingMessageReport, reportStats, source)).thenReturn(false);
        //try{
            //indexValidatorExecutor.init();
            //fail("Indexes validation error, some indexes are missing in the database.");
        //} catch(IndexValidationException e) {
            //assertNotNull(e.getMessage());
        //}
//      Mockito.verify(mockedSystemValidator, times(1)).isValid(anyObject(), any(AbstractMessageReport.class), any(ReportStats.class), any(Source.class));
    }

}