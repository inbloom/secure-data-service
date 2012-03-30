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

import org.springframework.beans.factory.annotation.Autowired;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.validation.spring.SimpleValidatorSpring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.xml.sax.ErrorHandler;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.validation.spring.SimpleValidatorSpring;

import org.xml.sax.SAXException;
/**
 *
 * @author ablum
 *
 */
public class XsdValidator extends SimpleValidatorSpring<IngestionFileEntry> {

    @Autowired
    private Map<String, Resource> xsd;
	private static final Logger LOG = LoggerFactory.getLogger(XsdValidator.class);

	@Override
	public boolean isValid(IngestionFileEntry ingestionFileEntry, ErrorReport errorReport){
		boolean isValid = false;
       try {
		  SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
          File schemaFile = xsd.get(ingestionFileEntry.getFileType()).getFile();
          Schema schema = schemaFactory.newSchema(schemaFile);
          Validator validator = schema.newValidator();
          String sourceXml = ingestionFileEntry.getFileName();
          Source sc = new StreamSource(sourceXml);
          ErrorHandler myHandler = new XsdErrorHandler(errorReport);
          validator.setErrorHandler(myHandler);
          validator.validate(sc);
          isValid = true;

          } catch (FileNotFoundException e) {
              LOG.error("File not found: " + ingestionFileEntry.getFileName());
              errorReport.error(getFailureMessage("SL_ERR_MSG11", ingestionFileEntry.getFileName()), XsdValidator.class);
              isValid = false;
          } catch (IOException e) {
              LOG.error("Problem reading file: " + ingestionFileEntry.getFileName());
              errorReport.error(getFailureMessage("SL_ERR_MSG12", ingestionFileEntry.getFileName()), XsdValidator.class);
              isValid = false;
          } catch (SAXException e) {
              isValid = false;
          }


       return isValid;
	}


}
