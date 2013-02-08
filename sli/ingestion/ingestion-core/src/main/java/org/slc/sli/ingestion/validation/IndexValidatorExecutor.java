package org.slc.sli.ingestion.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.JobSource;
import org.slc.sli.ingestion.reporting.impl.LoggingMessageReport;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;

@Component
public class IndexValidatorExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(IndexValidatorExecutor.class);

    @Autowired
    private Validator<?> systemValidator;

    @Autowired
    private LoggingMessageReport loggingMessageReport;

	public void init() throws IndexValidationException{

		loggingMessageReport.setLogger(LOG);
		Source source = new JobSource(null);
		ReportStats reportStats = new SimpleReportStats();

		boolean indexValidated = systemValidator.isValid(null,
				loggingMessageReport, reportStats, source);

		if (!indexValidated) {
			throw new IndexValidationException(
					"Indexes validation error, some indexes are missing in the database.");
		}
	}

	public void setValidator(Validator<?> systemValidator)
	{
		this.systemValidator = systemValidator;
	}

	public void setLoggingMessageReport(LoggingMessageReport loggingMessageReport)
	{
		this.loggingMessageReport = loggingMessageReport;
	}
}
