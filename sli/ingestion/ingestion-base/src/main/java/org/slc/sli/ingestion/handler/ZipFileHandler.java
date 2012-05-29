package org.slc.sli.ingestion.handler;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.landingzone.ZipFileUtil;
import org.slc.sli.ingestion.util.LogUtil;
import org.slc.sli.ingestion.util.spring.MessageSourceHelper;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * @author ablum
 *
 */
public class ZipFileHandler extends AbstractIngestionHandler<File, File> implements MessageSourceAware {
    private static final Logger LOG = LoggerFactory.getLogger(ZipFileHandler.class);

    private MessageSource messageSource;

    File doHandling(File zipFile, ErrorReport errorReport) {
        return doHandling(zipFile, errorReport, null);
    }

    @Override
    protected File doHandling(File zipFile, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {
        try {
            File dir = ZipFileUtil.extract(zipFile);
            LOG.info("Extracted zip file to {}", dir.getAbsolutePath());

            // find manifest (ctl file)
            return ZipFileUtil.findCtlFile(dir);
        } catch (IOException ex) {

            String message = MessageSourceHelper.getMessage(messageSource, "SL_ERR_MSG4", zipFile.getName());
            LogUtil.error(LOG, message, ex);
            errorReport.error(message, this);
        }

        return null;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

}
