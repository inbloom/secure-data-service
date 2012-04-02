package org.slc.sli.ingestion.validation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.validation.spring.SimpleValidatorSpring;

/**
 *
 * @author ablum
 *
 */
public class XsdValidator extends SimpleValidatorSpring<IngestionFileEntry> {

    private Map<String, Resource> xsd;
    private static final Logger LOG = LoggerFactory.getLogger(XsdValidator.class);

    @Override
    public boolean isValid(IngestionFileEntry ingestionFileEntry, ErrorReport errorReport) {

        XsdErrorHandlerInterface errorHandler = new XsdErrorHandler(errorReport);

        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Resource xsdResource = xsd.get(ingestionFileEntry.getFileType().getName());
            Schema schema = schemaFactory.newSchema(xsdResource.getURL());

            Validator validator = schema.newValidator();
            String sourceXml = ingestionFileEntry.getFileName();
            Source sc = new StreamSource(sourceXml);

            validator.setErrorHandler(errorHandler);
            validator.validate(sc);

        } catch (FileNotFoundException e) {
            LOG.error("File not found: " + ingestionFileEntry.getFileName());
            errorReport.error(getFailureMessage("SL_ERR_MSG11", ingestionFileEntry.getFileName()), XsdValidator.class);
            errorHandler.setIsValid(false);
        } catch (IOException e) {
            LOG.error("Problem reading file: " + ingestionFileEntry.getFileName());
            errorReport.error(getFailureMessage("SL_ERR_MSG12", ingestionFileEntry.getFileName()), XsdValidator.class);
            errorHandler.setIsValid(false);
        } catch (SAXException e) {
            errorHandler.setIsValid(false);
        }

        return errorHandler.isValid();
    }

    public Map<String, Resource> getXsd() {
        return xsd;
    }

    public void setXsd(Map<String, Resource> xsd) {
        this.xsd = xsd;
    }

}
