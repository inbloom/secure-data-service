package org.slc.sli.ingestion.validation;

import java.io.File;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.validation.spring.SimpleValidatorSpring;

/**
 *Validates the xml file against an xsd. Returns false if there is any error else it will always return true. The error messages would be reported by the error handler.
 * @author ablum
 *
 */
public class XsdValidator extends SimpleValidatorSpring<IngestionFileEntry> {

    private Map<String, Resource> xsd;

    @Autowired
    private XsdErrorHandlerInterface errorHandler;

    private static final Logger LOG = LoggerFactory.getLogger(XsdValidator.class);

    @Override
    public boolean isValid(IngestionFileEntry ingestionFileEntry, ErrorReport errorReport) {

        errorHandler.setErrorReport(errorReport);

        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Resource xsdResource = xsd.get(ingestionFileEntry.getFileType().getName());
            Schema schema = schemaFactory.newSchema(xsdResource.getURL());
            Validator validator = schema.newValidator();
            File xmlFile = ingestionFileEntry.getFile();
            if (xmlFile == null) {
                throw new FileNotFoundException();
            }
            validator.setResourceResolver(new ExternalEntityResolver());
            String sourceXml = ingestionFileEntry.getFile().getAbsolutePath();
            Source sc = new StreamSource(sourceXml);
            validator.setErrorHandler(errorHandler);
            validator.validate(sc);
            return true;
        } catch (FileNotFoundException e) {
            LOG.error("File not found: " + ingestionFileEntry.getFileName());
            errorReport.error(getFailureMessage("SL_ERR_MSG11", ingestionFileEntry.getFileName()), XsdValidator.class);
        } catch (IOException e) {
            LOG.error("Problem reading file: " + ingestionFileEntry.getFileName());
            errorReport.error(getFailureMessage("SL_ERR_MSG12", ingestionFileEntry.getFileName()), XsdValidator.class);
        } catch (SAXException e) {
            LOG.error("SAXException");
        } catch (RuntimeException e) {
            LOG.error("Problem ingesting file: " + ingestionFileEntry.getFileName());
        } catch (Exception e) {
            LOG.error("Exception", e);
        }

        return false;

    }

    public Map<String, Resource> getXsd() {
        return xsd;
    }

    public void setXsd(Map<String, Resource> xsd) {
        this.xsd = xsd;
    }

}
