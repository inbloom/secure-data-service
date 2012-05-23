package org.slc.sli.modeling.tools.edfisli.cmdline;

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

import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;

/**
 * A hack/program to compare what is in Ed-Fi schema with what is in the SLI
 * model.
 */
public final class EdFiSLICmdLine {
    private static final Set<String> outsideScope = outsideScope();
    private static final Set<String> planned = planned();
    private static final Set<String> groups = groups();
    private static final Set<String> ignorable = ignorable().keySet();
    private static final Set<String> investigate = investigate().keySet();
    private static final Map<String, String> classRenames = classRenames();

    private static final Set<QName> attributeNames(final Iterable<ClassType> classTypes) {
        final Set<QName> names = new HashSet<QName>();
        for (final ClassType classType : classTypes) {
            for (final Attribute attribute : classType.getAttributes()) {
                names.add(new QName(classType.getName(), attribute.getName()));
            }
        }
        return names;
    }

    private static final Set<String> classNames(final Iterable<ClassType> classTypes) {
        final Set<String> names = new HashSet<String>();
        for (final ClassType classType : classTypes) {
            names.add(classType.getName());
        }
        return Collections.unmodifiableSet(names);
    }

    private static final Map<String, String> classRenames() {
        final Map<String, String> renames = new HashMap<String, String>();
        renames.put("CourseOffering", "CourseOffering");
        renames.put("CourseTranscript", "StudentTranscriptAssociation");
        renames.put("StudentAssessment", "StudentAssessmentAssociation");
        renames.put("StudentGradebookEntry", "StudentSectionGradebookEntry");
        renames.put("StaffEducationOrgAssignmentAssociation", "StaffEducationOrganizationAssociation");
        return Collections.unmodifiableMap(renames);
    }

    @SuppressWarnings("unused")
    private static final void compareAttributes(final ModelIndex slim, final ModelIndex edfi) {

        final Set<QName> slimNames = attributeNames(slim.getClassTypes());
        System.out.println("slimNames.size=" + slimNames.size());
        System.out.println("slimNames:" + slimNames);

        final Set<QName> edfiNames = attributeNames(edfi.getClassTypes());
        System.out.println("edfiNames.size=" + edfiNames.size());
        System.out.println("edfiNames:" + edfiNames);

        edfiNames.removeAll(slimNames);
        System.out.println("edfiNames.size=" + edfiNames.size());
        for (final QName name : edfiNames) {
            System.out.println("" + name);
        }
    }

    private static final void compareClasses(final ModelIndex slimModel, final ModelIndex edfiModel) {
        System.out.println("Ed-Fi - SLI deviations from UML models.");
        printGMT();
        System.out.println("");
        System.out.println("SLI:");
        final Set<String> slimRaw = classNames(slimModel.getClassTypes());
        System.out.println("Raw complexTypes . . . . . : " + slimRaw.size());
        final Set<String> slim = rename(subtractEndsWith(subtractEndsWith(slimRaw, "ReferenceType"), "IdentityType"),
                invert(classRenames));
        System.out.println("Normalized . . . . . . . . : " + slim.size() + " (remove *ReferenceType or *IdentityType)");

        System.out.println("");
        System.out.println("Ed-Fi-Core:");
        final Set<String> edfiRaw = classNames(edfiModel.getClassTypes());
        System.out.println("Raw complexTypes . . . . . : " + edfiRaw.size());
        final Set<String> edfi = subtractEndsWith(subtractEndsWith(edfiRaw, "ReferenceType"), "IdentityType");
        System.out.println("Normalized . . . . . . . . : " + edfi.size() + " (remove *ReferenceType or *IdentityType)");

        System.out.println("");
        System.out.println("Adjustments:");
        System.out.println("");
        System.out.println("Outside Scope (" + outsideScope.size() + ")");
        System.out.println("-------------");
        print(sort(outsideScope));
        System.out.println("");
        System.out.println("Planned (" + planned.size() + ")");
        System.out.println("-------");
        print(sort(planned));
        System.out.println("");
        System.out.println("Groups (" + groups.size() + ")");
        System.out.println("------");
        print(sort(groups));
        System.out.println("");
        System.out.println("Ignorable (" + ignorable.size() + ")");
        System.out.println("------");
        print(ignorable());
        System.out.println("");
        System.out.println("Investigate differences (" + investigate.size() + ")");
        System.out.println("-----------------------");
        print(investigate());
        System.out.println("");
        System.out.println("Renamed (" + classRenames.size() + ")");
        System.out.println("-------");
        print(classRenames);
        final Set<String> edfiOutstanding = subtract(
                subtract(subtract(subtract(subtract(edfi, outsideScope), planned), groups), ignorable), investigate);

        final Set<String> edfiOutstandingMinusSLI = subtract(edfiOutstanding, slim);
        System.out.println("");
        System.out.println("How does Ed-Fi-Core exceed SLI? (" + edfiOutstandingMinusSLI.size() + ")");
        System.out.println("-------------------------------");
        print(sort(edfiOutstandingMinusSLI));

        final Set<String> slimMinusEdfiOutstanding = subtract(slim, edfiOutstanding);
        System.out.println("");
        System.out.println("How does SLI exceed Ed-Fi-Core? (" + slimMinusEdfiOutstanding.size() + ")");
        System.out.println("-------------------------------");
        print(sort(slimMinusEdfiOutstanding));

        System.out.println("");
        System.out.println("Summary:");
        final int missing = edfiOutstandingMinusSLI.size();
        final int common = edfiOutstanding.size() - missing + planned.size();
        final int excess = slimMinusEdfiOutstanding.size();
        System.out.println("common: " + common);
        System.out.println("missing: " + missing);
        System.out.println("excess: " + excess);

        System.out.println("coverage: " + (common * 100.0d) / (common + missing) + "%");
        System.out.println("");
        System.out.println("Disclaimer: This assumes that attributes of classes are 100% covered.");
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

    private static final Map<String, String> invert(final Map<String, String> mapping) {
        final Map<String, String> inversion = new HashMap<String, String>();
        for (final String lhs : mapping.keySet()) {
            final String rhs = mapping.get(lhs);
            inversion.put(rhs, lhs);
        }
        return Collections.unmodifiableMap(inversion);
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        try {
            final ModelIndex slim = new DefaultModelIndex(XmiReader.readModel("SLI.xmi")); // was
                                                                                   // ../data/SLI.xmi
            final ModelIndex edfi = new DefaultModelIndex(XmiReader.readModel("ED-Fi-Core.xmi"));
            compareClasses(slim, edfi);
            // compareAttributes(slim, edfi);
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Set<String> outsideScope() {
        final Set<String> outsideScope = new HashSet<String>();
        outsideScope.add("Account");
        outsideScope.add("AccountCodeDescriptor");
        outsideScope.add("AccountCodeDescriptorType");
        outsideScope.add("Actual");
        outsideScope.add("Budget");
        outsideScope.add("ContractedStaff");
        outsideScope.add("EducationServiceCenter");
        outsideScope.add("Payroll");
        outsideScope.add("StaffEducationOrgEmploymentAssociation");
        outsideScope.add("StudentTitleIPartAProgramAssociation");
        return Collections.unmodifiableSet(outsideScope);
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

    private static final void print(final List<String> strings) {
        for (final String s : strings) {
            System.out.println("" + s);
        }
    }

    private static final void print(final Map<String, String> strings) {
        for (final String key : strings.keySet()) {
            System.out.println(key + " => " + strings.get(key));
        }
    }

    private static final void printGMT() {
        Calendar c = Calendar.getInstance();
        System.out.println("" + c.getTime());
    }

    private static final Set<String> rename(final Set<String> originals, final Map<String, String> renames) {
        final Set<String> result = new HashSet<String>();
        for (final String original : originals) {
            if (renames.containsKey(original)) {
                final String rename = renames.get(original);
                result.add(rename);
            } else {
                result.add(original);
            }

        }
        return Collections.unmodifiableSet(result);
    }

    private static final List<String> sort(final Set<String> strings) {
        final List<String> sortNames = new ArrayList<String>(strings);
        Collections.sort(sortNames);
        return sortNames;
    }

    private static final <T> Set<T> subtract(final Set<T> lhs, final Set<T> rhs) {
        final Set<T> copy = new HashSet<T>(lhs);
        copy.removeAll(rhs);
        return Collections.unmodifiableSet(copy);
    }

    private static final Set<String> subtractEndsWith(final Set<String> strings, final String s) {
        final Set<String> result = new HashSet<String>();
        for (final String name : strings) {
            if (!name.endsWith(s)) {
                result.add(name);
            }
        }
        return Collections.unmodifiableSet(result);
    }

    private EdFiSLICmdLine() {
        // Prevent instantiation, even through reflection.
        throw new RuntimeException();
    }
}
