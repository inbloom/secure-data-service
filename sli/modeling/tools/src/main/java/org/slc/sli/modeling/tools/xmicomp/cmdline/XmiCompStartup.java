package org.slc.sli.modeling.tools.xmicomp.cmdline;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.slc.sli.modeling.tools.xmi2Java.cmdline.CloseableHelper;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;
import org.slc.sli.modeling.xml.IndentingXMLStreamWriter;

/**
 * A hack/program to create a file that contains the majority of the mappings.
 */
public final class XmiCompStartup {
    
    private static final Map<QName, CaseInsensitiveQName> applyAttributeMappings(
            final Map<QName, CaseInsensitiveQName> map, final Map<CaseInsensitiveQName, CaseInsensitiveQName> renames) {
        
        final Map<QName, CaseInsensitiveQName> changed = new HashMap<QName, CaseInsensitiveQName>();
        
        for (final QName name : map.keySet()) {
            final CaseInsensitiveQName value = map.get(name);
            if (renames.containsKey(value)) {
                changed.put(name, renames.get(value));
            } else {
                changed.put(name, value);
            }
        }
        return changed;
    }
    
    private static final Map<QName, CaseInsensitiveQName> applyClassMappings(
            final Map<QName, CaseInsensitiveQName> map, final Map<CaseInsensitiveString, CaseInsensitiveString> renames) {
        final Map<QName, CaseInsensitiveQName> changed = new HashMap<QName, CaseInsensitiveQName>();
        for (final QName name : map.keySet()) {
            final CaseInsensitiveQName value = map.get(name);
            if (renames.containsKey(value.getNamespaceURI())) {
                changed.put(name, new CaseInsensitiveQName(renames.get(value.getNamespaceURI()), value.getLocalPart()));
            } else {
                changed.put(name, value);
            }
        }
        return changed;
    }
    
    private static final Map<CaseInsensitiveQName, CaseInsensitiveQName> attributeMapping() {
        final Map<QName, QName> renames = new HashMap<QName, QName>();
        // renames.put(new QName("AccountabilityRating", "schoolYear"), NOTHING);
        renames.put(new QName("address", "begindate"), new QName("address", "opendate"));
        renames.put(new QName("address", "enddate"), new QName("address", "closedate"));
        renames.put(new QName("AttendanceEvent", "AttendanceEventCategory"), new QName("AttendanceEvent", "event"));
        renames.put(new QName("AttendanceEvent", "AttendanceEventReason"), new QName("AttendanceEvent", "reason"));
        renames.put(new QName("AttendanceEvent", "EventDate"), new QName("AttendanceEvent", "date"));
        renames.put(new QName("BehaviorDescriptor", "EducationOrganizationReference"), new QName("BehaviorDescriptor",
                "educationOrganizations"));
        renames.put(new QName("CourseOffering", "localCourseCode"), new QName("CourseOffering",
                "localCourseCode"));
        renames.put(new QName("CourseOffering", "localCourseTitle"), new QName("CourseOffering",
                "localCourseTitle"));
        renames.put(new QName("CourseOffering", "courseReference"), new QName("CourseOffering", "course"));
        renames.put(new QName("CourseOffering", "sessionReference"), new QName("CourseOffering", "session"));
        renames.put(new QName("Staff", "staffUniqueStateId"), new QName("AbstractStaff", "staffUniqueStateId"));
        renames.put(new QName("Staff", "staffIdentificationCode"),
                new QName("AbstractStaff", "staffIdentificationCode"));
        renames.put(new QName("Staff", "name"), new QName("AbstractStaff", "name"));
        renames.put(new QName("staff", "address"), new QName("abstractstaff", "address"));
        renames.put(new QName("staff", "birthdate"), new QName("abstractstaff", "birthdate"));
        renames.put(new QName("Staff", "credentials"), new QName("AbstractStaff", "credentials"));
        renames.put(new QName("Staff", "electronicMail"), new QName("AbstractStaff", "electronicMail"));
        renames.put(new QName("Staff", "highestLevelOfEducationCompleted"), new QName("AbstractStaff",
                "highestLevelOfEducationCompleted"));
        renames.put(new QName("Staff", "hispanicLatinoEthnicity"),
                new QName("AbstractStaff", "hispanicLatinoEthnicity"));
        renames.put(new QName("Staff", "oldEthnicity"), new QName("AbstractStaff", "oldEthnicity"));
        renames.put(new QName("Staff", "otherName"), new QName("AbstractStaff", "otherName"));
        renames.put(new QName("Staff", "race"), new QName("AbstractStaff", "race"));
        renames.put(new QName("Staff", "sex"), new QName("AbstractStaff", "sex"));
        renames.put(new QName("Staff", "telephone"), new QName("AbstractStaff", "telephone"));
        renames.put(new QName("Staff", "yearsOfPriorProfessionalExperience"), new QName("AbstractStaff",
                "yearsOfPriorProfessionalExperience"));
        renames.put(new QName("Staff", "yearsOfPriorTeachingExperience"), new QName("AbstractStaff",
                "yearsOfPriorTeachingExperience"));
        renames.put(new QName("Staff", "loginId"), new QName("AbstractStaff", "loginId"));
        renames.put(new QName("StaffCohortAssociation", "CohortReference"), new QName("StaffCohortAssociation",
                "cohorts"));
        renames.put(new QName("StaffCohortAssociation", "StaffReference"),
                new QName("StaffCohortAssociation", "staffs"));
        renames.put(new QName("StaffProgramAssociation", "ProgramReference"), new QName("StaffProgramAssociation",
                "programs"));
        renames.put(new QName("StaffProgramAssociation", "StaffReference"), new QName("StaffProgramAssociation",
                "staffs"));
        return Collections.unmodifiableMap(toLowerCase(renames));
    }
    
    private static final XmiComparison build(final XmiDefinition lhsDef,
            final Map<QName, CaseInsensitiveQName> lhsFwdMap, final XmiDefinition rhsDef,
            final Map<CaseInsensitiveQName, QName> rhsRevMap) {
        final Map<CaseInsensitiveQName, QName> lhsRevMap = reverse(lhsFwdMap);
        @SuppressWarnings("unused")
        final Map<QName, CaseInsensitiveQName> rhsFwdMap = reverse(rhsRevMap);
        final Set<CaseInsensitiveQName> names = new HashSet<CaseInsensitiveQName>();
        names.addAll(lhsRevMap.keySet());
        names.addAll(rhsRevMap.keySet());
        int mapped = 0;
        final List<XmiMapping> mappings = new LinkedList<XmiMapping>();
        for (final CaseInsensitiveQName ciName : sort(names)) {
            if (lhsRevMap.containsKey(ciName)) {
                final QName lhsName = lhsRevMap.get(ciName);
                if (rhsRevMap.containsKey(ciName)) {
                    final QName rhsName = rhsRevMap.get(ciName);
                    final XmiFeature lhsFeature = new XmiFeature(lhsName.getLocalPart(), lhsName.getNamespaceURI());
                    final XmiFeature rhsFeature = new XmiFeature(rhsName.getLocalPart(), rhsName.getNamespaceURI());
                    mappings.add(new XmiMapping(lhsFeature, rhsFeature, computeMappingStatus(lhsFeature, rhsFeature),
                            ""));
                    mapped += 1;
                } else {
                    final XmiFeature lhsFeature = new XmiFeature(lhsName.getLocalPart(), lhsName.getNamespaceURI());
                    mappings.add(new XmiMapping(lhsFeature, null, computeMappingStatus(lhsFeature, null), ""));
                }
            } else {
                if (rhsRevMap.containsKey(ciName)) {
                    final QName rhsName = rhsRevMap.get(ciName);
                    final XmiFeature rhsFeature = new XmiFeature(rhsName.getLocalPart(), rhsName.getNamespaceURI());
                    mappings.add(new XmiMapping(null, rhsFeature, computeMappingStatus(null, rhsFeature), ""));
                } else {
                    throw new AssertionError();
                }
            }
        }
        System.out.println("count(mapped) : " + mapped);
        return new XmiComparison(lhsDef, rhsDef, Collections.unmodifiableList(mappings));
    }
    
    private static final XmiMappingStatus computeMappingStatus(final XmiFeature lhs, final XmiFeature rhs) {
        if (lhs != null) {
            if (rhs != null) {
                return XmiMappingStatus.MATCH;
            } else {
                if (lhs.getType().endsWith("IdentityType")) {
                    return XmiMappingStatus.TRANSIENT;
                } else if (lhs.getType().equals("Account")) {
                    return XmiMappingStatus.IGNORABLE;
                } else if (lhs.getType().equals("Actual")) {
                    return XmiMappingStatus.IGNORABLE;
                } else if (lhs.getType().equals("Budget")) {
                    return XmiMappingStatus.IGNORABLE;
                } else if (lhs.getType().equals("AccountCodeDescriptor")) {
                    return XmiMappingStatus.IGNORABLE;
                } else if (lhs.getType().equals("AccountCodeDescriptorType")) {
                    return XmiMappingStatus.IGNORABLE;
                } else if (lhs.getType().equals("Payroll")) {
                    return XmiMappingStatus.IGNORABLE;
                } else {
                    return XmiMappingStatus.UNKNOWN;
                }
            }
        } else {
            if (rhs == null) {
                throw new IllegalArgumentException();
            } else {
                return XmiMappingStatus.UNKNOWN;
            }
        }
    }
    
    /**
     * The mapping from EdFi class name to SLI class name.
     * 
     * This excludes names that already map 1:1.
     */
    private static final Map<CaseInsensitiveString, CaseInsensitiveString> classMapping() {
        final Map<String, String> renames = new HashMap<String, String>();
        renames.put("CourseOffering", "CourseOffering");
        renames.put("CourseTranscript", "StudentTranscriptAssociation");
        renames.put("StudentAssessment", "StudentAssessmentAssociation");
        renames.put("StudentGradebookEntry", "StudentSectionGradebookEntry");
        renames.put("StaffEducationOrgAssignmentAssociation", "StaffEducationOrganizationAssociation");
        return Collections.unmodifiableMap(toLower(renames));
    }
    
    @SuppressWarnings("unused")
    private static final Set<String> classNames(final Iterable<ClassType> classTypes) {
        final Set<String> names = new HashSet<String>();
        for (final ClassType classType : classTypes) {
            names.add(classType.getName());
        }
        return Collections.unmodifiableSet(names);
    }
    
    /**
     * This looks symmetrical, but in fact we expect Ed-Fi on the LHS and SLI on the RHS.
     */
    private static final XmiComparison compareFeatures(final XmiDefinition lhsDef, final ModelIndex lhsModel,
            final XmiDefinition rhsDef, final ModelIndex rhsModel) {
        
        // This is the entire list of EdFi feature names.
        final Set<QName> lhsNames = featureNames(lhsModel.getClassTypes(), lhsModel);
        // This is the entire list of SLI feature names.
        final Set<QName> rhsNames = featureNames(rhsModel.getClassTypes(), rhsModel);
        // Remove those attributes corresponding to classes that we don't care about.
        // final Set<QName> keepNames = filter(filter(filter(lhsNames, outsideScope()), groups()),
        // identityTypes());
        // Create a trial mapping then keep on refining it until no exceptions.
        final Map<CaseInsensitiveQName, QName> rhsRevMap = createRevMap(rhsNames);
        // The strategy is to change the case-insensitive values in the LHS map to align with the
        // RHS keys.
        // 1. Align the class names.
        final Map<QName, CaseInsensitiveQName> lhsFwdMap = applyAttributeMappings(
                normalize(applyClassMappings(createFwdMap(lhsNames), classMapping()), rhsRevMap.keySet()),
                attributeMapping());
        
        System.out.println("edfiNames.size=" + lhsNames.size());
        System.out.println("slimNames.size=" + rhsNames.size());
        return build(lhsDef, lhsFwdMap, rhsDef, rhsRevMap);
    }
    
    private static final Map<QName, CaseInsensitiveQName> createFwdMap(final Set<QName> names) {
        final Map<QName, CaseInsensitiveQName> fwdMap = new HashMap<QName, CaseInsensitiveQName>();
        for (final QName name : names) {
            fwdMap.put(name, toCaseInsensitive(name));
        }
        return Collections.unmodifiableMap(fwdMap);
    }
    
    private static final Map<CaseInsensitiveQName, QName> createRevMap(final Set<QName> names) {
        final Map<CaseInsensitiveQName, QName> revMap = new HashMap<CaseInsensitiveQName, QName>();
        for (final QName name : names) {
            revMap.put(toCaseInsensitive(name), name);
        }
        return Collections.unmodifiableMap(revMap);
    }
    
    /**
     * Computes a set of class-qualified attribute names and adds to it the association end names.
     */
    private static final Set<QName> featureNames(final Iterable<ClassType> classTypes, final ModelIndex mapper) {
        final Set<QName> names = new HashSet<QName>();
        for (final ClassType classType : classTypes) {
            for (final Attribute feature : classType.getAttributes()) {
                names.add(new QName(classType.getName(), feature.getName()));
            }
            for (final AssociationEnd feature : mapper.getAssociationEnds(classType.getId())) {
                names.add(new QName(classType.getName(), feature.getName()));
            }
        }
        return names;
    }
    
    @SuppressWarnings("unused")
    private static final Set<QName> filter(final Set<QName> names, final Set<String> classNames) {
        final Set<QName> filtrate = new HashSet<QName>();
        for (final QName name : names) {
            if (!classNames.contains(name.getNamespaceURI())) {
                filtrate.add(name);
            }
        }
        return Collections.unmodifiableSet(filtrate);
    }
    
    @SuppressWarnings("unused")
    private static final Set<String> groups() {
        final Set<String> groups = new HashSet<String>();
        groups.add("CourseLevelCharacteristicsType");
        groups.add("EducationalPlansType");
        groups.add("EducationOrganizationCategoriesType");
        groups.add("GradeLevelsType");
        groups.add("LanguagesType");
        groups.add("LinguisticAccommodationsType");
        groups.add("MeetingDaysType");
        groups.add("RaceType");
        groups.add("SchoolCategoriesType");
        groups.add("Section504DisabilitiesType");
        groups.add("SpecialAccommodationsType");
        return Collections.unmodifiableSet(groups);
    }
    
    @SuppressWarnings("unused")
    private static final Set<String> identityTypes() {
        final Set<String> identityTypes = new HashSet<String>();
        identityTypes.add("AccountIdentityType");
        identityTypes.add("AssessmentIdentityType");
        identityTypes.add("AssessmentFamilyIdentityType");
        identityTypes.add("AssessmentItemIdentityType");
        identityTypes.add("BellScheduleIdentityType");
        identityTypes.add("CalendarDateIdentityType");
        identityTypes.add("ClassPeriodIdentityType");
        identityTypes.add("CohortIdentityType");
        identityTypes.add("CourseOfferingIdentityType");
        identityTypes.add("DisciplineIncidentIdentityType");
        identityTypes.add("GradingPeriodIdentityType");
        identityTypes.add("LocationIdentityType");
        identityTypes.add("ParentIdentityType");
        identityTypes.add("ProgramIdentityType");
        identityTypes.add("SectionIdentityType");
        identityTypes.add("SessionIdentityType");
        identityTypes.add("StaffIdentityType");
        identityTypes.add("StudentIdentityType");
        identityTypes.add("StudentCompetencyObjectiveIdentityType");
        identityTypes.add("StudentSectionAssociationIdentityType");
        // Also add DescriptorType
        identityTypes.add("AssessmentPeriodDescriptorType");
        identityTypes.add("BehaviorDescriptorType");
        identityTypes.add("CredentialFieldDescriptorType");
        identityTypes.add("DisciplineDescriptorType");
        identityTypes.add("PerformanceLevelDescriptorType");
        // ReferenceType
        identityTypes.add("AssessmentReferenceType");
        identityTypes.add("AssessmentFamilyReferenceType");
        identityTypes.add("AssessmentItemReferenceType");
        identityTypes.add("CalendarDateReferenceType");
        identityTypes.add("CalendarPeriodReferenceType");
        identityTypes.add("ClassPeriodReferenceType");
        identityTypes.add("StudentReferenceType");
        identityTypes.add("CohortReferenceType");
        identityTypes.add("CourseReferenceType");
        identityTypes.add("DisciplineIncidentReferenceType");
        identityTypes.add("EducationalOrgReferenceType");
        identityTypes.add("GradingPeriodReferenceType");
        identityTypes.add("LearningObjectiveReferenceType");
        identityTypes.add("LearningStandardReferenceType");
        identityTypes.add("LocationReferenceType");
        identityTypes.add("ObjectiveAssessmentReferenceType");
        identityTypes.add("ParentReferenceType");
        identityTypes.add("ProgramReferenceType");
        identityTypes.add("SessionReferenceType");
        identityTypes.add("StaffReferenceType");
        identityTypes.add("StudentCompetencyObjectiveReferenceType");
        identityTypes.add("StudentReferenceType");
        identityTypes.add("StudentSectionAssociationReferenceType");
        return Collections.unmodifiableSet(identityTypes);
    }
    
    /**
     * @param args
     */
    public static void main(final String[] args) {
        final XmiDefinition slide = new XmiDefinition("SLI", "0.9");
        final XmiDefinition edude = new XmiDefinition("Ed-Fi-Core", "1.x");
        try {
            final ModelIndex slim = new DefaultModelIndex(XmiReader.readModel("SLI.xmi"));
            final ModelIndex edfi = new DefaultModelIndex(XmiReader.readModel("ED-Fi-Core.xmi"));
            final XmiComparison mappings = compareFeatures(edude, edfi, slide, slim);
            writeBootstrapFile(mappings, new File("edfi-sli-mapping.xml"));
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Change the values of the map so that we get semantic alignment.
     */
    private static final Map<QName, CaseInsensitiveQName> normalize(final Map<QName, CaseInsensitiveQName> map,
            final Set<CaseInsensitiveQName> ciNames) {
        final Map<QName, CaseInsensitiveQName> changed = new HashMap<QName, CaseInsensitiveQName>();
        for (final QName name : map.keySet()) {
            final CaseInsensitiveQName value = map.get(name);
            @SuppressWarnings("unused")
            final CaseInsensitiveString className = value.getNamespaceURI();
            final CaseInsensitiveString localName = value.getLocalPart();
            if (localName.endsWith(new CaseInsensitiveString("reference"))) {
                final CaseInsensitiveQName trial01 = new CaseInsensitiveQName(value.getNamespaceURI(),
                        localName.substring(0, localName.length() - 9));
                if (ciNames.contains(trial01)) {
                    changed.put(name, trial01);
                } else {
                    final CaseInsensitiveQName trial02 = new CaseInsensitiveQName(trial01.getNamespaceURI(), trial01
                            .getLocalPart().concat(new CaseInsensitiveString("id")));
                    if (ciNames.contains(trial02)) {
                        changed.put(name, trial02);
                    } else {
                        final CaseInsensitiveQName trial03 = new CaseInsensitiveQName(trial01.getNamespaceURI(),
                                trial01.getLocalPart().concat(new CaseInsensitiveString("s")));
                        if (ciNames.contains(trial03)) {
                            changed.put(name, trial03);
                        } else {
                            changed.put(name, value);
                        }
                    }
                }
            } else {
                changed.put(name, value);
            }
        }
        return changed;
    }
    
    @SuppressWarnings("unused")
    private static final Set<String> outsideScope() {
        final Set<String> outsideScope = new HashSet<String>();
        outsideScope.add("Account");
        outsideScope.add("AccountCodeDescriptor");
        outsideScope.add("AccountCodeDescriptorType");
        outsideScope.add("AccountReferenceType");
        outsideScope.add("Actual");
        outsideScope.add("Budget");
        outsideScope.add("ContractedStaff");
        outsideScope.add("EducationServiceCenter");
        outsideScope.add("Payroll");
        outsideScope.add("StaffEducationOrgEmploymentAssociation");
        outsideScope.add("StudentTitleIPartAProgramAssociation");
        return Collections.unmodifiableSet(outsideScope);
    }
    
    @SuppressWarnings("unused")
    private static final <T> Set<T> rename(final Set<T> originals, final Map<T, T> renames) {
        final Set<T> result = new HashSet<T>();
        for (final T original : originals) {
            if (renames.containsKey(original)) {
                final T rename = renames.get(original);
                result.add(rename);
            } else {
                result.add(original);
            }
            
        }
        return Collections.unmodifiableSet(result);
    }
    
    /**
     * This function
     * 
     * @param mapping
     * @return
     */
    private static final <T, V> Map<V, T> reverse(final Map<T, V> mapping) {
        final Map<V, T> inversion = new HashMap<V, T>();
        for (final T lhs : mapping.keySet()) {
            final V rhs = mapping.get(lhs);
            inversion.put(rhs, lhs);
        }
        return Collections.unmodifiableMap(inversion);
    }
    
    private static final <T extends Comparable<? super T>> List<T> sort(final Set<T> set) {
        final List<T> sortNames = new ArrayList<T>(set);
        Collections.sort(sortNames);
        return sortNames;
    }
    
    @SuppressWarnings("unused")
    private static final <T> Set<T> subtract(final Set<T> lhs, final Set<T> rhs) {
        final Set<T> copy = new HashSet<T>(lhs);
        copy.removeAll(rhs);
        return Collections.unmodifiableSet(copy);
    }
    
    @SuppressWarnings("unused")
    private static final Set<String> subtractEndsWith(final Set<String> strings, final String s) {
        final Set<String> result = new HashSet<String>();
        for (final String name : strings) {
            if (!name.endsWith(s)) {
                result.add(name);
            }
        }
        return Collections.unmodifiableSet(result);
    }
    
    private static final Map<CaseInsensitiveString, CaseInsensitiveString> toLower(final Map<String, String> map) {
        final Map<CaseInsensitiveString, CaseInsensitiveString> result = new HashMap<CaseInsensitiveString, CaseInsensitiveString>();
        for (final String key : map.keySet()) {
            result.put(new CaseInsensitiveString(key), new CaseInsensitiveString(map.get(key)));
        }
        return Collections.unmodifiableMap(result);
    }
    
    @SuppressWarnings("unused")
    private static final Set<String> toLower(final Set<String> strings) {
        final Set<String> lower = new HashSet<String>();
        for (final String s : strings) {
            lower.add(s.toLowerCase());
        }
        return Collections.unmodifiableSet(lower);
    }
    
    private static final Map<CaseInsensitiveQName, CaseInsensitiveQName> toLowerCase(final Map<QName, QName> map) {
        final Map<CaseInsensitiveQName, CaseInsensitiveQName> result = new HashMap<CaseInsensitiveQName, CaseInsensitiveQName>();
        for (final QName key : map.keySet()) {
            result.put(toCaseInsensitive(key), toCaseInsensitive(map.get(key)));
        }
        return Collections.unmodifiableMap(result);
    }
    
    private static final CaseInsensitiveQName toCaseInsensitive(final QName name) {
        return new CaseInsensitiveQName(new CaseInsensitiveString(name.getNamespaceURI()), new CaseInsensitiveString(
                name.getLocalPart()));
    }
    
    @SuppressWarnings("unused")
    private static final Set<QName> valueSet(final Map<QName, QName> map) {
        final Set<QName> values = new HashSet<QName>();
        for (final QName name : map.values()) {
            values.add(name);
        }
        return Collections.unmodifiableSet(values);
    }
    
    private static final void writeBootstrapFile(final XmiComparison mappings, final File file) {
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(file));
            try {
                writeBootstrapFile(mappings, outstream);
            } finally {
                CloseableHelper.closeQuiet(outstream);
            }
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public static final void writeBootstrapFile(final XmiComparison comparison, final OutputStream outstream) {
        final XMLOutputFactory xof = XMLOutputFactory.newInstance();
        try {
            final XMLStreamWriter xsw = new IndentingXMLStreamWriter(xof.createXMLStreamWriter(outstream, "UTF-8"));
            xsw.writeStartDocument("UTF-8", "1.0");
            xsw.writeProcessingInstruction("xml-stylesheet", "type='text/xsl' href='xmi-mapping.xsl'");
            try {
                xsw.writeStartElement("mappings");
                xsw.writeNamespace("xmi", "http://www.w3.org/2001/XMLSchema-instance");
                xsw.writeAttribute("xmi", "http://www.w3.org/2001/XMLSchema-instance", "noNamespaceSchemaLocation",
                        "xmi-mapping.xsd");
                try {
                    final XmiDefinition lhsDefinition = comparison.getLhsDefinition();
                    xsw.writeStartElement("lhsModel");
                    try {
                        xsw.writeStartElement("name");
                        try {
                            xsw.writeCharacters(lhsDefinition.getName());
                        } finally {
                            xsw.writeEndElement();
                        }
                        xsw.writeStartElement("version");
                        try {
                            xsw.writeCharacters(lhsDefinition.getVersion());
                        } finally {
                            xsw.writeEndElement();
                        }
                    } finally {
                        xsw.writeEndElement();
                    }
                    final XmiDefinition rhsDefinition = comparison.getRhsDefinition();
                    xsw.writeStartElement("rhsModel");
                    try {
                        xsw.writeStartElement("name");
                        try {
                            xsw.writeCharacters(rhsDefinition.getName());
                        } finally {
                            xsw.writeEndElement();
                        }
                        xsw.writeStartElement("version");
                        try {
                            xsw.writeCharacters(rhsDefinition.getVersion());
                        } finally {
                            xsw.writeEndElement();
                        }
                    } finally {
                        xsw.writeEndElement();
                    }
                    for (final XmiMapping mapping : comparison.getMappings()) {
                        xsw.writeStartElement("mapping");
                        try {
                            final XmiFeature lhs = mapping.getLhs();
                            if (lhs != null) {
                                writeFeature("lhs", lhs, xsw);
                            } else {
                                writeMissingFeature("lhs", xsw);
                            }
                            final XmiFeature rhs = mapping.getRhs();
                            if (rhs != null) {
                                writeFeature("rhs", rhs, xsw);
                            } else {
                                writeMissingFeature("rhs", xsw);
                            }
                            xsw.writeStartElement("status");
                            xsw.writeCharacters(mapping.getStatus().getName());
                            xsw.writeEndElement();
                            xsw.writeStartElement("comment");
                            xsw.writeCharacters(mapping.getComment());
                            xsw.writeEndElement();
                        } finally {
                            xsw.writeEndElement();
                        }
                        
                    }
                } finally {
                    xsw.writeEndElement();
                }
            } finally {
                xsw.writeEndDocument();
            }
            xsw.flush();
        } catch (final XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static void writeFeature(final String side, final XmiFeature feature, final XMLStreamWriter xsw)
            throws XMLStreamException {
        xsw.writeStartElement(side);
        try {
            xsw.writeStartElement("type");
            try {
                xsw.writeCharacters(feature.getType());
            } finally {
                xsw.writeEndElement();
            }
            xsw.writeStartElement("name");
            try {
                xsw.writeCharacters(feature.getName());
            } finally {
                xsw.writeEndElement();
            }
        } finally {
            xsw.writeEndElement();
        }
    }
    
    private static void writeMissingFeature(final String side, final XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(side.concat("Missing"));
        xsw.writeEndElement();
    }
    
    private XmiCompStartup() {
        // Prevent instantiation, even through reflection.
        throw new RuntimeException();
    }
}
