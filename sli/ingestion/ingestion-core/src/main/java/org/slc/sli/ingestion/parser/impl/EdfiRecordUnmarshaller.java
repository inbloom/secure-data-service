/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slc.sli.ingestion.parser.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.stream.Location;
import javax.xml.validation.ValidatorHandler;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.xerces.stax.ImmutableLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.slc.sli.ingestion.ActionVerb;
import org.slc.sli.ingestion.ReferenceConverter;
import org.slc.sli.ingestion.parser.RecordMeta;
import org.slc.sli.ingestion.parser.RecordVisitor;
import org.slc.sli.ingestion.parser.TypeProvider;
import org.slc.sli.ingestion.parser.XmlParseException;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ElementSource;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.ElementSourceImpl;

/**
 * A reader delegate that will intercept an XML Validator's calls to nextEvent() and build the
 * document into a Map of Maps data structure.
 *
 * Additionally, the class implements ErrorHandler so
 * that the parsing of a specific entity can be aware of validation errors.
 *
 * @author dduran
 *
 */
public class EdfiRecordUnmarshaller extends EdfiRecordParser {

    private static final Logger LOG = LoggerFactory.getLogger(EdfiRecordUnmarshaller.class);
    private static final String ACTION_TYPE = "ActionType";
    private static final String CASCADE = "Cascade";
    private static final String ACTION = "Action";
    private static final String FORCE = "Force";
    private static final String LOG_VIOLATIONS = "LogViolations";

    private TypeProvider typeProvider;

    private Stack<Pair<RecordMeta, Map<String, Object>>> complexTypeStack = new Stack<Pair<RecordMeta, Map<String, Object>>>();
    private ActionVerb action = ActionVerb.NONE;
    private String originalType = null;

    private boolean currentEntityValid = false;

    private String interchange;

    private StringBuffer elementValue = new StringBuffer();

    private Locator locator;

    private List<RecordVisitor> recordVisitors = new ArrayList<RecordVisitor>();

    /**
     * Constructor.
     *
     * @param typeProvider
     *            XSD Type provider
     * @param messageReport
     *            Message report for validation warning/error reporting
     * @param reportStats
     *            Associated report statistics
     * @param source
     *            Source of the messages
     */
    public EdfiRecordUnmarshaller(TypeProvider typeProvider, AbstractMessageReport messageReport,
            ReportStats reportStats, Source source) {
        super(messageReport, reportStats, source);
        this.typeProvider = typeProvider;
    }

    /**
     * Parser an XML represented by the input stream against provided XSD, reports validation issues
     * and produces output of
     * extracted data via the provided visitor.
     *
     * @param input
     *            XML to validate
     * @param schemaResource
     *            XSD resource
     * @param typeProvider
     *            XSD Type provider
     * @param visitor
     *            Record visitor
     * @param messageReport
     *            Message report for validation warning/error reporting
     * @param reportStats
     *            Associated report statistics
     * @param source
     *            Source of the messages
     * @throws SAXException
     *             If a SAX error occurs during XSD parsing.
     * @throws IOException
     *             If a IO error occurs during XSD/XML parsing.
     * @throws XmlParseException
     *             If a SAX error occurs during XML parsing.
     */
    public static void parse(InputStream input, Resource schemaResource, TypeProvider typeProvider,
            RecordVisitor visitor, AbstractMessageReport messageReport, ReportStats reportStats, Source source)
            throws SAXException, IOException, XmlParseException {

        EdfiRecordUnmarshaller parser = new EdfiRecordUnmarshaller(typeProvider, messageReport, reportStats, source);

        parser.addVisitor(visitor);

        parser.process(input, schemaResource);
    }

    @Override
    protected void parseAndValidate(InputStream input, ValidatorHandler vHandler) throws XmlParseException, IOException {
        vHandler.setContentHandler(this);

        super.parseAndValidate(input, vHandler);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        elementValue.setLength(0);

        if (ACTION.equals(localName)) {
            action = getAction(localName, attributes);
            originalType = null;
        } else if (interchange != null) {
            parseInterchangeEvent(localName, attributes);
        } else if (localName.startsWith("Interchange")) {
            interchange = localName;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (ACTION.equals(localName)) {
            action = ActionVerb.NONE;
            return;
        }

        if (action.doDelete() && ReferenceConverter.isReferenceType(originalType) && originalType.equals(localName)) {
            return;
        }

        if (complexTypeStack.isEmpty()) {
            return;
        }

        String expectedLocalName = complexTypeStack.peek().getLeft().getName();

        if (localName.equals(expectedLocalName)) {
            if (elementValue.length() > 0) {
                String text = StringUtils.trimToEmpty(elementValue.toString());

                if (StringUtils.isNotBlank(text)) {
                    parseCharacters(text);
                }
            }

            if (complexTypeStack.size() > 1) {
                complexTypeStack.pop();
            } else if (complexTypeStack.size() == 1) {
                recordParsingComplete();
            }
        }

        elementValue.setLength(0);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        elementValue.append(ch, start, length);
    }

    private void parseInterchangeEvent(String localName, Attributes attributes) {

        boolean isFirst = false;

        if (originalType == null && action.doDelete()) {
            originalType = localName;
            isFirst = true;
        }

        if (isFirst && ReferenceConverter.isReferenceType(localName)) {
            return;
        }

        if (complexTypeStack.isEmpty()) {
            initCurrentEntity(localName, attributes, action);
        } else {
            parseStartElement(localName, attributes);
        }
    }

    private ActionVerb getAction(String localName, Attributes attributes) {
        String xsdType = typeProvider.getTypeFromInterchange(interchange, localName);
        ActionVerb doAction = ActionVerb.NONE;

        if (typeProvider.isActionType(xsdType)) {
            String action = attributes.getValue(ACTION_TYPE);
            String cascade = attributes.getValue(CASCADE);
            if (action == null || cascade == null) {
                /*
                 * Shouldn't happen - xsd validation would've failed
                 */
                LOG.warn("Could not get ActionType or Cascade properties for {}", localName);
            }

            try {
                doAction = ActionVerb.valueOf(action);
                if (doAction == ActionVerb.DELETE && Boolean.parseBoolean(cascade)) {
                    doAction = ActionVerb.CASCADE_DELETE;
                }

            } catch (Exception e) {
                /*
                 * Shouldn't happen - xsd validation would've failed
                 */
                doAction = ActionVerb.NONE;
                LOG.warn("Could not get ActionVerb for {}", action);
            }
            Map<String, String> actionAttributes = new HashMap<String, String>();
            doAction.setAttributes(actionAttributes);
            String force = attributes.getValue(FORCE);
            String logViolations = attributes.getValue(LOG_VIOLATIONS);
            if(force != null) {
                actionAttributes.put(FORCE, force);
            }
            if(logViolations != null) {
                actionAttributes.put(LOG_VIOLATIONS, logViolations);
            }
        }

        return (doAction);
    }

    private void initCurrentEntity(String localName, Attributes attributes, ActionVerb doAction) {
        String xsdType = typeProvider.getTypeFromInterchange(interchange, localName, doAction);

        RecordMetaImpl recordMeta = new RecordMetaImpl(localName, xsdType, false, doAction);
        if (originalType != null) {
            recordMeta.setOriginalType(originalType);
        }

        recordMeta.setSourceStartLocation(getCurrentLocation());

        complexTypeStack.push(createElementEntry(recordMeta));

        currentEntityValid = true;

        parseEventAttributes(attributes);
    }

    private void parseStartElement(String localName, Attributes attributes) {
        newEventToStack(localName);

        parseEventAttributes(attributes);
    }

    private void newEventToStack(String localName) {

        RecordMeta typeMeta = getRecordMetaForEvent(localName);

        Pair<RecordMeta, Map<String, Object>> subElement = createElementEntry(typeMeta);

        Object mapValue = subElement.getRight();
        if (typeMeta.isList() && complexTypeStack.peek().getRight().get(localName) == null) {
            mapValue = new ArrayList<Object>(Arrays.asList(mapValue));
        }

        insertToMap(localName, mapValue, complexTypeStack.peek().getRight());

        complexTypeStack.push(subElement);
    }

    private RecordMeta getRecordMetaForEvent(String eventName) {
        RecordMeta typeMeta = typeProvider.getTypeFromParentType(complexTypeStack.peek().getLeft(), eventName);

        if (typeMeta == null) {
            // the parser must go on building the stack
            LOG.warn(
                    "Could not determine type of element: {} with parent of type: {}. Type conversion may not be applied on its value.",
                    eventName, complexTypeStack.peek().getLeft().getType());
            typeMeta = new RecordMetaImpl(eventName, "UNKNOWN");
        }
        return typeMeta;
    }

    private void parseEventAttributes(Attributes attributes) {
        String elementType = complexTypeStack.peek().getLeft().getType();

        for (int i = 0; i < attributes.getLength(); i++) {
            String attributeName = attributes.getLocalName(i);
            Object value = typeProvider.convertAttributeType(elementType, attributeName, attributes.getValue(i));
            complexTypeStack.peek().getRight().put("a_" + attributeName, value);
        }
    }

    private void parseCharacters(String text) {
        Object convertedValue = typeProvider.convertType(complexTypeStack.peek().getLeft().getType(), text);
        complexTypeStack.peek().getRight().put("_value", convertedValue);
    }

    private void recordParsingComplete() {
        Pair<RecordMeta, Map<String, Object>> pair = complexTypeStack.pop();
        LOG.debug("Parsed record: {}", pair);

        RecordMetaImpl meta = (RecordMetaImpl) pair.getLeft();
        boolean validRecord = isValidRecord(meta);
        if (!validRecord) {
            currentEntityValid = false;
        }

        if (currentEntityValid) {
            meta.setSourceEndLocation(getCurrentLocation());
            originalType = null;

            for (RecordVisitor visitor : recordVisitors) {
                visitor.visit(meta, pair.getRight());
            }
        } else {
            for (RecordVisitor visitor : recordVisitors) {
                visitor.ignored();
            }
        }
    }

    /*
     * Cascade delete is not supported now, but we can't change the schema
     */
    private boolean isValidRecord(final RecordMeta meta) {
        boolean status = true;
        Source elementSource = new ElementSourceImpl(new ElementSource() {

            @Override
            public String getResourceId() {
                return source.getResourceId();
            }

            @Override
            public int getVisitBeforeLineNumber() {
                return meta.getSourceStartLocation().getLineNumber();
            }

            @Override
            public int getVisitBeforeColumnNumber() {
                return meta.getSourceStartLocation().getColumnNumber();
            }

            @Override
            public String getElementType() {
                return source.getResourceId();
            }
        });

        boolean isDelete = meta.getAction().doDelete();
        boolean isReference = false;

        if (!isDelete) {
            return status;
        }

        if (isDelete && meta.doCascade()) {
            messageReport.error(reportStats, elementSource, CoreMessageCode.CORE_0072);
            status = false;
        }

        // Some deletes are not implemented yet
        if (isDelete && ReferenceConverter.isReferenceType(originalType)) {
            if (ReferenceConverter.fromReferenceName(originalType) == null) {
                messageReport.error(reportStats, elementSource, CoreMessageCode.CORE_0073, originalType);
                return false;
            } else {
                isReference = true;
            }
        }


        if (isDelete && originalType != null &&  !isReference && !SupportedEntities.isSupportedForDelete(originalType)) {
            messageReport.error(reportStats, elementSource, CoreMessageCode.CORE_0073, originalType);
            return false;
        }


        return status;
    }

    /**
     * Retrieve the current Location in the XML file.
     *
     * @return Location
     */
    public Location getCurrentLocation() {
        return new ImmutableLocation(0, locator.getColumnNumber(), locator.getLineNumber(), locator.getPublicId(),
                locator.getSystemId());
    }

    private static Pair<RecordMeta, Map<String, Object>> createElementEntry(RecordMeta edfiType) {
        return new ImmutablePair<RecordMeta, Map<String, Object>>(edfiType, new HashMap<String, Object>());
    }

    @SuppressWarnings("unchecked")
    private static void insertToMap(String key, Object value, Map<String, Object> map) {
        Object stored = map.get(key);
        if (stored != null && List.class.isAssignableFrom(stored.getClass())) {
            List<Object> storage = (List<Object>) stored;
            storage.add(value);
        } else {
            map.put(key, value);
        }
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        super.error(exception);

        currentEntityValid = false;

    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        super.fatalError(exception);

        currentEntityValid = false;
    }

    /**
     * Register a visitor to retrieve extracted data.
     *
     * @param recordVisitor
     *            Record visitor
     */
    public void addVisitor(RecordVisitor recordVisitor) {
        recordVisitors.add(recordVisitor);
    }

    /*
     * We need to disallow deletes of any entities that are not listed here. This should go away
     * next sprint, as deletes of all
     * entities will be supported
     *
     */

    protected static enum SupportedEntities {
        ASSESSMENTFAMILY("AssessmentFamily"),
        ASSESSMENT("Assessment"),
        ASSESSMENTPERIODDESCRIPTOR("AssessmentPeriodDescriptor"),
        CALENDARDATE("CalendarDate"),
        COHORT("Cohort"),
        COURSE("Course"),
        COURSEOFFERING("CourseOffering"),
        GRADE("Grade"),
        GRADINGPERIOD("GradingPeriod"),
        LEARNINGOBJECTIVE("LearningObjective"),
        PARENT("Parent"),
        SCHOOL("School"),
        SECTION("Section"),
        SESSION("Session"),
        STAFFEDUCATIONORGASSIGNMENTASSOCIATION("StaffEducationOrgAssignmentAssociation"),
        STUDENT("Student"),
        STUDENTASSESSMENT("StudentAssessment"),
        STUDENTASSESSMENTITEM("StudentAssessmentItem"),
        STUDENTCOHORTASSOCIATION("StudentCohortAssociation"),
        STUDENTOBJECTIVEASSESSMENT("StudentObjectiveAssessment"),
        STUDENTPARENTASSOCIATION("StudentParentAssociation"),
        STUDENTSCHOOLASSOCIATION("StudentSchoolAssociation"),
        TEACHER("Teacher"),
        TEACHERSCHOOLASSOCIATION("TeacherSchoolAssociation"),
        TEACHERSECTIONASSOCIATION("TeacherSectionAssociation");

        private SupportedEntities(String text) {
        }

        static boolean isSupportedForDelete(String entityName) {
            try {
                SupportedEntities.valueOf(entityName.toUpperCase());
            } catch (IllegalArgumentException ex) {
                LOG.warn("Received entity unsupported for deletes: {}", entityName);
                return false;
            }
            return true;
        }

    }

}
