package org.slc.sli.ingestion.idrefresolver;

import java.io.File;

import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.LoggingErrorReport;
import org.slc.sli.ingestion.xml.idref.IdRefResolutionHandler;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author ifaybyshev
 *
 */
public class IdRefResolverController {
 
    private static Logger logger = LoggerUtil.getLogger();
    
    private static ErrorReport errorReport = new LoggingErrorReport(logger);
    
    public void doIdRefResolution(File path) {
        if (path.isFile() && path.getName().endsWith(".xml")) {
            processXmlFile(path);
        } else {
            logger.error("Invalid input: No xml file found.");
        }
    }
    
    public void processXmlFile(File xmlFile) {
        logger.info("Processing an xml file [{}] ...", xmlFile.getAbsolutePath());

        FileFormat format = FileFormat.findByCode("edfi-xml");
        IngestionFileEntry fe = new IngestionFileEntry(format, FileType.XML_EDUCATION_ORG_CALENDAR, xmlFile.getName(), "f2bbf6ccf1af3e0c46adafb0a97c9d4e");
        fe.setFile(xmlFile);
        
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/applicationContext-idrefresolver.xml");
        IdRefResolutionHandler idRefResolutionHandler = context.getBean("IdReferenceResolutionHandler", IdRefResolutionHandler.class);
        
        idRefResolutionHandler.handle(fe, new FaultsReport());
    }

}
