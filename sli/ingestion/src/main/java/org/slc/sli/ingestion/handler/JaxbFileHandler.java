package org.slc.sli.ingestion.handler;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.jaxb.domain.edfi.Assessment;
import org.slc.sli.ingestion.jaxb.domain.edfi.ComplexObjectType;
import org.slc.sli.ingestion.jaxb.domain.edfi.InterchangeAssessmentMetadata;
import org.slc.sli.ingestion.jaxb.mapping.AssessmentMappings;
import org.slc.sli.ingestion.jaxb.util.MappingUtils;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.util.FileRecordWriter;
import org.slc.sli.ingestion.util.NeutralRecordJsonStreamer;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * jaxb file handler
 *
 * @author dduran
 *
 */
@Component
public class JaxbFileHandler extends AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(JaxbFileHandler.class);

    @Override
    IngestionFileEntry doHandling(IngestionFileEntry fileEntry, ErrorReport errorReport) {
        try {

            mapAndWriteAsNeutralRecord(fileEntry, errorReport);

        } catch (IOException e) {
            errorReport
                    .fatal("Could not instantiate smooks, unable to read configuration file.", JaxbFileHandler.class);
        } catch (SAXException e) {
            errorReport.fatal("Could not instantiate smooks, problem parsing configuration file.",
                    JaxbFileHandler.class);
        }

        return fileEntry;
    }

    void mapAndWriteAsNeutralRecord(IngestionFileEntry fileEntry, ErrorReport errorReport) throws IOException,
            SAXException {

        File neutralRecordOutFile = createTempFile();
        fileEntry.setNeutralRecordFile(neutralRecordOutFile);

        FileRecordWriter<NeutralRecord> nrFileWriter = new NeutralRecordJsonStreamer(neutralRecordOutFile);

        try {
            NeutralRecord neutralRecord = mapToSliNeutralRecord(fileEntry);

            nrFileWriter.writeRecord(neutralRecord);

        } catch (Exception e) {
            LOG.error("exception encountered.", e);
            errorReport.error("Problem encountered while filtering input.", JaxbFileHandler.class);
        } finally {
            nrFileWriter.close();
        }
    }

    private NeutralRecord mapToSliNeutralRecord(IngestionFileEntry fileEntry) throws JAXBException, IOException {
        NeutralRecord neutralRecord = new NeutralRecord();

        Unmarshaller unmarshaller = MappingUtils.createUnmarshallerForPackage("org.slc.sli.ingestion.jaxb.domain.edfi");

        long timeNow = System.currentTimeMillis();
        Object edFiObject = unmarshaller.unmarshal(fileEntry.getFile());
        LOG.info("time taken jaxb unmarshal: " + (System.currentTimeMillis() - timeNow));

        if (fileEntry.getFileType() == FileType.XML_ASSESSMENT_METADATA) {

            mapAssessmentMetaData(neutralRecord, edFiObject);

        } else if (fileEntry.getFileType() == FileType.XML_STUDENT_ASSESSMENT) {

            mapStudentAssessment(neutralRecord, edFiObject);
        }
        return neutralRecord;
    }

    private void mapStudentAssessment(NeutralRecord neutralRecord, Object edFiObject) {
        // TODO Auto-generated method stub

    }

    private void mapAssessmentMetaData(NeutralRecord neutralRecord, Object edFiObject) throws IOException {
        ObjectMapper jsonObjectMapper = new ObjectMapper();
        jsonObjectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);

        // use jaxb annotations
        AnnotationIntrospector jaxbIntrospector = new JaxbAnnotationIntrospector();
        jsonObjectMapper.getDeserializationConfig().setAnnotationIntrospector(jaxbIntrospector);
        jsonObjectMapper.getSerializationConfig().setAnnotationIntrospector(jaxbIntrospector);

        InterchangeAssessmentMetadata interchangeAssessmentMetadata = (InterchangeAssessmentMetadata) edFiObject;

        for (ComplexObjectType assessmentComplexObject : interchangeAssessmentMetadata
                .getAssessmentFamilyOrAssessmentOrAssessmentPeriodDescriptor()) {

            if (assessmentComplexObject instanceof Assessment) {
                org.slc.sli.ingestion.jaxb.domain.sli.Assessment sliAssessment = AssessmentMappings
                        .mapAssessment((Assessment) assessmentComplexObject);

                // convert to neutral record
                neutralRecord.setRecordType("assessment");
                neutralRecord.setLocalId(assessmentComplexObject.getId());
                Map<String, Object> attributes = jsonObjectMapper.readValue(
                        jsonObjectMapper.writeValueAsString(sliAssessment), Map.class);
                neutralRecord.setAttributes(attributes);
            }
        }
    }

    private File createTempFile() throws IOException {
        File outputFile = File.createTempFile("camel_", ".tmp");
        outputFile.deleteOnExit();
        return outputFile;
    }

}
