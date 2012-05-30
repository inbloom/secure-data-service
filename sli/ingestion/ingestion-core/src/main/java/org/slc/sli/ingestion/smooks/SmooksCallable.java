package org.slc.sli.ingestion.smooks;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.Callable;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.handler.SmooksFileHandler;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.LogUtil;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * The Smooks of the future..
 * 
 * @author dduran
 * 
 */
public class SmooksCallable implements Callable<Boolean> {
    
    private static final Logger LOG = LoggerFactory.getLogger(SmooksCallable.class);
    
    private SliSmooksFactory sliSmooksFactory;
    
    private final NewBatchJob newBatchJob;
    private final IngestionFileEntry fe;
    private final Stage stage;
    private final BatchJobDAO batchJobDAO;
    
    public SmooksCallable(NewBatchJob newBatchJob, IngestionFileEntry fe, Stage stage, BatchJobDAO batchJobDAO,
            SliSmooksFactory sliSmooksFactory) {
        this.newBatchJob = newBatchJob;
        this.fe = fe;
        this.stage = stage;
        this.batchJobDAO = batchJobDAO;
        this.sliSmooksFactory = sliSmooksFactory;
    }
    
    @Override
    public Boolean call() throws Exception {
        return runSmooksFuture();
    }
    
    public boolean runSmooksFuture() {
        LOG.info("Starting SmooksCallable for: " + fe.getFileName());
        Metrics metrics = Metrics.newInstance(fe.getFileName());
        stage.addMetrics(metrics);
        
        FileProcessStatus fileProcessStatus = new FileProcessStatus();
        ErrorReport errorReport = fe.getErrorReport();
        
        // actually do the processing
        processFileEntry(fe, errorReport, fileProcessStatus);
        
        metrics.setRecordCount(fileProcessStatus.getTotalRecordCount());
        
        int errorCount = aggregateAndLogProcessingErrors(newBatchJob.getId(), fe);
        metrics.setErrorCount(errorCount);
        
        ResourceEntry resource = BatchJobUtils.createResourceForOutputFile(fe, fileProcessStatus);
        newBatchJob.addResourceEntry(resource);
        
        LOG.info("Finished SmooksCallable for: " + fe.getFileName());
        return (errorCount > 0);
    }
    
    public void processFileEntry(IngestionFileEntry fe, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {
        
        if (fe.getFileType() != null) {
            FileFormat fileFormat = fe.getFileType().getFileFormat();
            if (fileFormat == FileFormat.EDFI_XML) {
                
                doHandling(fe, errorReport, fileProcessStatus);
                
            } else {
                throw new IllegalArgumentException("Unsupported file format: " + fe.getFileType().getFileFormat());
            }
        } else {
            throw new IllegalArgumentException("FileType was not provided.");
        }
    }
    
    private void doHandling(IngestionFileEntry fileEntry, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {
        try {
            
            generateNeutralRecord(fileEntry, errorReport, fileProcessStatus);
            
        } catch (IOException e) {
            LogUtil.error(LOG,
                    "Error generating neutral record: Could not instantiate smooks, unable to read configuration file",
                    e);
            errorReport.fatal("Could not instantiate smooks, unable to read configuration file.",
                    SmooksFileHandler.class);
        } catch (SAXException e) {
            LOG.error("Could not instantiate smooks, problem parsing configuration file");
            errorReport.fatal("Could not instantiate smooks, problem parsing configuration file.",
                    SmooksFileHandler.class);
        }
    }
    
    void generateNeutralRecord(IngestionFileEntry ingestionFileEntry, ErrorReport errorReport,
            FileProcessStatus fileProcessStatus) throws IOException, SAXException {
        
        // LandingZone landingZone = new LocalFileSystemLandingZone(new File(
        // ingestionFileEntry.getTopLevelLandingZonePath()));
        
        // File neutralRecordOutFile = createTempFile(resolveLzDirecotry(ingestionFileEntry,
        // landingZone));
        
        // fileProcessStatus.setOutputFilePath(neutralRecordOutFile.getAbsolutePath());
        // fileProcessStatus.setOutputFileName(neutralRecordOutFile.getName());
        
        // NeutralRecordFileWriter nrFileWriter = new NeutralRecordFileWriter(neutralRecordOutFile);
        
        // set the IngestionFileEntry NeutralRecord file we just wrote
        // ingestionFileEntry.setNeutralRecordFile(neutralRecordOutFile);
        
        // create instance of Smooks (with visitors already added)
        Smooks smooks = sliSmooksFactory.createInstance(ingestionFileEntry, errorReport);
        
        InputStream inputStream = new BufferedInputStream(new FileInputStream(ingestionFileEntry.getFile()));
        try {
            // filter fileEntry inputStream, converting into NeutralRecord entries as we go
            smooks.filterSource(new StreamSource(inputStream));
        } catch (SmooksException se) {
            LOG.error("smooks exception - encountered problem with " + ingestionFileEntry.getFile().getName() + "\n"
                    + Arrays.toString(se.getStackTrace()));
            errorReport.error("SmooksException encountered while filtering input.", SmooksFileHandler.class);
        } finally {
            IOUtils.closeQuietly(inputStream);
            
            // long count = 0L;
            // Hashtable<String, Long> counts = nrFileWriter.getNRCount();
            // for (String type : counts.keySet()) {
            // count += counts.get(type);
            // }
            
            // fileProcessStatus.setTotalRecordCount(count);
            // nrFileWriter.close();
        }
    }
    
    // private String resolveLzDirecotry(IngestionFileEntry ingestionFileEntry, LandingZone
    // landingZone) {
    // String lzDirectory = null;
    // if (landingZone != null && landingZone.getLZId() != null) {
    // lzDirectory = landingZone.getLZId();
    // } else {
    // lzDirectory = ingestionFileEntry.getFile().getParent();
    // }
    // return lzDirectory;
    // }
    
    private int aggregateAndLogProcessingErrors(String batchJobId, IngestionFileEntry fe) {
        int errorCount = 0;
        for (Fault fault : fe.getFaultsReport().getFaults()) {
            if (fault.isError()) {
                errorCount++;
            }
            String faultMessage = fault.getMessage();
            String faultLevel = fault.isError() ? FaultType.TYPE_ERROR.getName()
                    : fault.isWarning() ? FaultType.TYPE_WARNING.getName() : "Unknown";
            
            Error error = Error.createIngestionError(batchJobId, fe.getFileName(), stage.getStageName(), null, null,
                    null, faultLevel, faultLevel, faultMessage);
            batchJobDAO.saveError(error);
        }
        return errorCount;
    }
    
    // private static File createTempFile(String lzDirectory) throws IOException {
    // File landingZone = new File(lzDirectory);
    // File outputFile = landingZone.exists() ? File.createTempFile("neutralRecord_", ".tmp",
    // landingZone) : File
    // .createTempFile("neutralRecord_", ".tmp");
    // return outputFile;
    // }
    
}
