package org.slc.sli.ingestion.handler;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.delivery.ContentHandlerConfigMapTable;
import org.milyn.delivery.VisitorConfigMap;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.smooks.SliSmooksFactory;
import org.slc.sli.ingestion.smooks.SmooksEdFiVisitor;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

/**
 * smooks handler for edfi files
 *
 * @author dduran
 *
 */
@Component
public class SmooksFileHandler extends AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(SmooksFileHandler.class);

    @Autowired
    private SliSmooksFactory sliSmooksFactory;

    @Override
    IngestionFileEntry doHandling(IngestionFileEntry fileEntry, ErrorReport errorReport,
            FileProcessStatus fileProcessStatus) {
        try {

            generateNeutralRecord(fileEntry, errorReport, fileProcessStatus);

        } catch (IOException e) {
            LOG.error("IOException", e);
            errorReport.fatal("Could not instantiate smooks, unable to read configuration file.",
                    SmooksFileHandler.class);
        } catch (SAXException e) {
            errorReport.fatal("Could not instantiate smooks, problem parsing configuration file.",
                    SmooksFileHandler.class);
        }

        return fileEntry;
    }

    void generateNeutralRecord(IngestionFileEntry ingestionFileEntry, ErrorReport errorReport,
            FileProcessStatus fileProcessStatus) throws IOException, SAXException {
        //LandingZone landingZone = new LocalFileSystemLandingZone(new File(
        //        ingestionFileEntry.getTopLevelLandingZonePath()));
        //String lzDirectory = "";
        //if (landingZone != null && landingZone.getLZId() != null) {
        //    lzDirectory = landingZone.getLZId();
        //} else {
        //    lzDirectory = ingestionFileEntry.getFile().getParent();
        //}
        // File neutralRecordOutFile = createTempFile(lzDirectory);

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
            

            try {
                Field f = smooks.getClass().getDeclaredField("visitorConfigMap");
                f.setAccessible(true);
                VisitorConfigMap map = (VisitorConfigMap) f.get(smooks);
                ContentHandlerConfigMapTable<SAXVisitAfter> visitAfters = map.getSaxVisitAfters();
                SmooksEdFiVisitor visitAfter = (SmooksEdFiVisitor) visitAfters.getAllMappings().get(0).getContentHandler();
                
                fileProcessStatus.setTotalRecordCount(visitAfter.getRecordsPerisisted());
                
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            
        } catch (SmooksException se) {
            LOG.error("smooks exception: encountered problem with " + ingestionFileEntry.getFile().getName() + "\n", se);
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

    // private static File createTempFile(String lzDirectory) throws IOException {
    // File landingZone = new File(lzDirectory);
    // File outputFile = landingZone.exists() ? File.createTempFile("neutralRecord_", ".tmp",
    // landingZone) : File
    // .createTempFile("neutralRecord_", ".tmp");
    // return outputFile;
    // }

}
