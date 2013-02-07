package org.slc.sli.ingestion.validation;

import javax.annotation.PostConstruct;

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
    private Validator<?> systemValidatorStartUp;

    @Autowired
    private LoggingMessageReport loggingMessageReport;

	@PostConstruct
	public void init() throws IndexValidationException{

		loggingMessageReport.setLogger(LOG);
		Source source = new JobSource(null);
		ReportStats reportStats = new SimpleReportStats();

		boolean indexValidated = systemValidatorStartUp.isValid(null,
				loggingMessageReport, reportStats, source);

		if (!indexValidated) {
			throw new IndexValidationException(
					"Indexes validation error, some indexes are missing in the database.");
		}
	}

	public void setValidator(Validator<?> systemValidator)
	{
		this.systemValidatorStartUp = systemValidator;
	}

	public void setLoggingMessageReport(LoggingMessageReport loggingMessageReport)
	{
		this.loggingMessageReport = loggingMessageReport;
	}
}
