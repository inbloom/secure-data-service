package org.slc.sli.modeling.tools.xmicomp.cmdline;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;

/**
 * A hack/program to compare what is in Ed-Fi schema with what is in the SLI
 * model.
 */
public final class XmiCompChecker {
    
    @SuppressWarnings("unused")
    private static final Map<String, String> classRenames = classMapping();
    
    @SuppressWarnings("unused")
    private static final Set<String> investigate = investigate().keySet();
    @SuppressWarnings("unused")
    private static final QName NOTHING = new QName("NOTHING");
    @SuppressWarnings("unused")
    private static final Set<String> planned = planned();
    
    private static final Map<QName, QName> applyClassMappings(final Map<QName, QName> map,
            final Map<String, String> renames) {
        final Map<QName, QName> changed = new HashMap<QName, QName>();
        for (final QName name : map.keySet()) {
            final QName value = map.get(name);
            if (renames.containsKey(value.getNamespaceURI())) {
                changed.put(name, new QName(renames.get(value.getNamespaceURI()), value.getLocalPart()));
            } else {
                changed.put(name, value);
            }
        }
        return changed;
    }
    
    private static final Map<String, String> toLower(final Map<String, String> map) {
        final Map<String, String> result = new HashMap<String, String>();
        for (final String key : map.keySet()) {
            result.put(key.toLowerCase(), map.get(key).toLowerCase());
        }
        return Collections.unmodifiableMap(result);
    }
    
    private static final Map<QName, QName> toLowerCase(final Map<QName, QName> map) {
        final Map<QName, QName> result = new HashMap<QName, QName>();
        for (final QName key : map.keySet()) {
            result.put(toLowerCase(key), toLowerCase(map.get(key)));
        }
        return Collections.unmodifiableMap(result);
    }
    
    private static final QName toLowerCase(final QName name) {
        return new QName(name.getNamespaceURI().toLowerCase(), name.getLocalPart().toLowerCase());
    }
    
    private static final Map<QName, QName> applyAttributeMappings(final Map<QName, QName> map,
            final Map<QName, QName> renames) {
        
        final Map<QName, QName> changed = new HashMap<QName, QName>();
        
        for (final QName name : map.keySet()) {
            final QName value = map.get(name);
            if (renames.containsKey(value)) {
                changed.put(name, renames.get(value));
            } else {
                changed.put(name, value);
            }
        }
        return changed;
    }
    
    private static final Map<QName, QName> attributeMapping() {
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
        return Collections.unmodifiableMap(renames);
    }
    
    /**
     * Computes a set of class-qualified attribute names and adds to it the association end names.
     */
    private static final Set<QName> attributeNames(final Iterable<ClassType> classTypes, final ModelIndex mapper) {
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
    
    /**
     * The mapping from EdFi class name to SLI class name.
     * 
     * This excludes names that already map 1:1.
     */
    private static final Map<String, String> classMapping() {
        final Map<String, String> renames = new HashMap<String, String>();
        renames.put("CourseOffering", "CourseOffering");
        renames.put("CourseTranscript", "StudentTranscriptAssociation");
        renames.put("StudentAssessment", "StudentAssessmentAssociation");
        renames.put("StudentGradebookEntry", "StudentSectionGradebookEntry");
        renames.put("StaffEducationOrgAssignmentAssociation", "StaffEducationOrganizationAssociation");
        return Collections.unmodifiableMap(renames);
    }
    
    @SuppressWarnings("unused")
    private static final Set<String> classNames(final Iterable<ClassType> classTypes) {
        final Set<String> names = new HashSet<String>();
        for (final ClassType classType : classTypes) {
            names.add(classType.getName());
        }
        return Collections.unmodifiableSet(names);
    }
    
    @SuppressWarnings("unused")
    private static final void compareAttributes(final ModelIndex slim, final ModelIndex edfi) {
        
        // This is the entire list of SLI feature names.
        final Set<QName> slimNames = attributeNames(slim.getClassTypes(), slim);
        // This is the entire list of EdFi feature names.
        final Set<QName> edfiNames = attributeNames(edfi.getClassTypes(), edfi);
        // Remove those attributes corresponding to classes that we don't care about.
        final Set<QName> keepNames = filter(
                filter(filter(filter(edfiNames, outsideScope()), groups()), identityTypes()), ignorable().keySet());
        // Create a trial mapping then keep on refining it until no exceptions.
        final Map<QName, QName> lhsMap01 = trialMapping(keepNames);
        final Map<QName, QName> rhsMap = decodeMapping(slimNames);
        final Set<QName> rhsCI = rhsMap.keySet();
        
        final Map<QName, QName> lhsMap02 = applyClassMappings(lhsMap01, toLower(classMapping()));
        final Map<QName, QName> map03 = normalize(lhsMap02, rhsCI);
        final Map<QName, QName> map04 = applyAttributeMappings(map03, toLowerCase(attributeMapping()));
        final Set<QName> compNames = valueSet(map04);
        
        // final Set<QName> compNames = rename(keepNames, attributeMapping());
        final Set<QName> moreNames = subtract(rhsCI, compNames);
        
        System.out.println("edfiNames.size=" + edfiNames.size());
        System.out.println("slimNames.size=" + slimNames.size());
        int mapped = 0;
        int dropped = 0;
        for (final QName edfiName : sortNames(map04.keySet())) {
            final QName ciName = map04.get(edfiName);
            if (rhsMap.containsKey(ciName)) {
                final QName slimName = rhsMap.get(ciName);
                System.out.println("mapped: " + edfiName + " => " + slimName);
                mapped += 1;
            } else {
                System.out.println("******: " + edfiName);
                dropped += 1;
            }
        }
        System.out.println("count(mapped) : " + mapped);
        System.out.println("count(dropped) : " + dropped);
        System.out.println("count(excluded) : " + (edfiNames.size() - (mapped + dropped)));
        // print(sortNames(todoNames));
        // print(sortNames(slimNames));
        // print(sortNames(rhsCI));
        // print(lhsMap02);
    }
    
    private static final Set<QName> filter(final Set<QName> names, final Set<String> classNames) {
        final Set<QName> filtrate = new HashSet<QName>();
        for (final QName name : names) {
            if (!classNames.contains(name.getNamespaceURI())) {
                filtrate.add(name);
            }
        }
        return Collections.unmodifiableSet(filtrate);
    }
    
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
    
    private static final Map<String, String> ignorable() {
        final Map<String, String> ignorable = new HashMap<String, String>();
        ignorable.put("ClassPeriod", "This is just a wrapper around ClassPeriodNameType.");
        ignorable.put("ComplexObjectType", "This is only used to handle ID/IDREF in Ed-Fi interchanges.");
        ignorable
                .put("GradePointAverage",
                        "Not used by Ed-Fi. Should be used in StudentAcademicRecord or dropped. S/B simpleType? Are the facets OK?");
        ignorable.put("FeederSchoolAssociation", "This is a kind of EducationOrgAssociation. ");
        ignorable.put("AssessmentPeriodDescriptorType",
                "Ed-Fi seems to generally not have consolidated its *Descriptor and *DescriptorType classes?");
        ignorable.put("BehaviorDescriptorType",
                "Ed-Fi seems to generally not have consolidated its *Descriptor and *DescriptorType classes?");
        return Collections.unmodifiableMap(ignorable);
    }
    
    @SuppressWarnings("unused")
    private static final Map<String, String> invert(final Map<String, String> mapping) {
        final Map<String, String> inversion = new HashMap<String, String>();
        for (final String lhs : mapping.keySet()) {
            final String rhs = mapping.get(lhs);
            inversion.put(rhs, lhs);
        }
        return Collections.unmodifiableMap(inversion);
    }
    
    private static final Map<String, String> investigate() {
        final Map<String, String> investigate = new HashMap<String, String>();
        investigate.put("PerformanceLevelDescriptorType",
                "SLI PerformanceLevelDescriptor is merge with stronger cardinality requirements.");
        investigate.put("StateEducationAgency",
                "Make sure SLI models this correctly taking into account the EdOrg categories.");
        investigate.put("LocalEducationAgency",
                "Make sure SLI models this correctly taking into account the EdOrg categories.");
        investigate.put("DisciplineDescriptorType",
                "Minor cardinality differences as well as missing reference to EdOrgs.");
        investigate.put("CredentialFieldDescriptorType",
                "SLI seems to have over-simplified the academic subject and ed-org references?");
        return Collections.unmodifiableMap(investigate);
    }
    
    /**
     * @param args
     */
    public static void main(final String[] args) {
        try {
            final ModelIndex slim = new DefaultModelIndex(XmiReader.readModel("SLI.xmi"));
            final ModelIndex edfi = new DefaultModelIndex(XmiReader.readModel("ED-Fi-Core.xmi"));
            compareAttributes(slim, edfi);
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static final Map<QName, QName> normalize(final Map<QName, QName> map, final Set<QName> ciNames) {
        final Map<QName, QName> changed = new HashMap<QName, QName>();
        for (final QName name : map.keySet()) {
            final QName value = map.get(name);
            @SuppressWarnings("unused")
            final String className = value.getNamespaceURI();
            final String localName = value.getLocalPart();
            if (localName.endsWith("reference")) {
                final QName trial01 = new QName(value.getNamespaceURI(), localName.substring(0, localName.length() - 9));
                if (ciNames.contains(trial01)) {
                    changed.put(name, trial01);
                } else {
                    final QName trial02 = new QName(trial01.getNamespaceURI(), trial01.getLocalPart().concat("id"));
                    if (ciNames.contains(trial02)) {
                        changed.put(name, trial02);
                    } else {
                        final QName trial03 = new QName(trial01.getNamespaceURI(), trial01.getLocalPart().concat("s"));
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
    private static final Set<String> toLower(final Set<String> strings) {
        final Set<String> lower = new HashSet<String>();
        for (final String s : strings) {
            lower.add(s.toLowerCase());
        }
        return Collections.unmodifiableSet(lower);
    }
    
    private static final Set<String> planned() {
        final Set<String> planned = new HashSet<String>();
        planned.add("CompetencyLevelDescriptor");       // Parallax
        planned.add("CompetencyLevelDescriptorType");   // Parallax
        // planned.add("CTEProgram"); //
        planned.add("ReportCard");                      // Wolverine
        planned.add("StudentCompetency");               // Wolverine
        planned.add("StudentCompetencyObjective");      // Wolverine
        // planned.add("StudentCTEProgramAssociation");
        // planned.add("StudentSpecialEdProgramAssociation");
        return Collections.unmodifiableSet(planned);
    }
    
    @SuppressWarnings("unused")
    private static final <T> void print(final List<T> strings) {
        for (final T s : strings) {
            System.out.println("" + s);
        }
    }
    
    @SuppressWarnings("unused")
    private static final <T> void print(final Map<T, T> strings) {
        for (final T key : strings.keySet()) {
            System.out.println(key + " => " + strings.get(key));
        }
    }
    
    @SuppressWarnings("unused")
    private static final void printGMT() {
        Calendar c = Calendar.getInstance();
        System.out.println("" + c.getTime());
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
    
    @SuppressWarnings("unused")
    private static final List<String> sort(final Set<String> strings) {
        final List<String> sortNames = new ArrayList<String>(strings);
        Collections.sort(sortNames);
        return sortNames;
    }
    
    private static final List<QName> sortNames(final Set<QName> strings) {
        final List<QName> sortNames = new ArrayList<QName>(strings);
        Collections.sort(sortNames, QNameComparator.SINGLETON);
        return sortNames;
    }
    
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
    
    private static final Map<QName, QName> trialMapping(final Set<QName> names) {
        final Map<QName, QName> trial = new HashMap<QName, QName>();
        for (final QName name : names) {
            trial.put(name, new QName(name.getNamespaceURI().toLowerCase(), name.getLocalPart().toLowerCase()));
        }
        return Collections.unmodifiableMap(trial);
    }
    
    private static final Map<QName, QName> decodeMapping(final Set<QName> names) {
        final Map<QName, QName> trial = new HashMap<QName, QName>();
        for (final QName name : names) {
            trial.put(new QName(name.getNamespaceURI().toLowerCase(), name.getLocalPart().toLowerCase()), name);
        }
        return trial;
    }
    
    private static final Set<QName> valueSet(final Map<QName, QName> map) {
        final Set<QName> values = new HashSet<QName>();
        for (final QName name : map.values()) {
            values.add(name);
        }
        return Collections.unmodifiableSet(values);
    }
    
    private XmiCompChecker() {
        // Prevent instantiation, even through reflection.
        throw new RuntimeException();
    }
}
