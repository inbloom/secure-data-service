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


package org.slc.sli.modeling.tools.edfisli.cmdline;

import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A hack/program to compare what is in Ed-Fi schema with what is in the SLI
 * model.
 */
public final class EdFiSLICmdLine {
    private static final Logger LOG = LoggerFactory.getLogger(EdFiSLICmdLine.class);

    private static final Set<String> OUTSIDE_SCOPE = outsideScope();
    private static final Set<String> PLANNED = planned();
    private static final Set<String> GROUPS = groups();
    private static final Set<String> IGNORABLE = ignorable().keySet();
    private static final Set<String> INVESTIGATE = investigate().keySet();
    private static final Map<String, String> CLASS_RENAMES = classRenames();

    public static final String DEFAULT_SLI_INPUT_FILENAME = "SLI.xmi";
    public static final String DEFAULT_EDFI_INPUT_FILENAME = "ED-Fi-Core.xmi";


    /**
     * @param args
     */
    public static void main(final String[] args) {

        String sliInputFilename = (args.length == 2) ? args[0] : DEFAULT_SLI_INPUT_FILENAME;
        String edfiInputFilename = (args.length == 2) ? args[1] : DEFAULT_EDFI_INPUT_FILENAME;

        try {
            final ModelIndex slim = new DefaultModelIndex(XmiReader.readModel(sliInputFilename));
            final ModelIndex edfi = new DefaultModelIndex(XmiReader.readModel(edfiInputFilename));
            compareClasses(slim, edfi);
        } catch (final FileNotFoundException e) {
            throw new EdFiSLIRuntimeException(e);
        }
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
        renames.put("StudentAssessment", "StudentAssessmentAssociation");
        renames.put("StaffEducationOrgAssignmentAssociation", "StaffEducationOrganizationAssociation");
        return Collections.unmodifiableMap(renames);
    }

    private static final void compareClasses(final ModelIndex slimModel, final ModelIndex edfiModel) {
        LOG.info("Ed-Fi - SLI deviations from UML models.");
        printGMT();
        LOG.info("");
        LOG.info("SLI:");
        final Set<String> slimRaw = classNames(slimModel.getClassTypes().values());
        LOG.info("Raw complexTypes . . . . . : " + slimRaw.size());
        final Set<String> slim = rename(subtractEndsWith(subtractEndsWith(slimRaw, "ReferenceType"), "IdentityType"),
                invert(CLASS_RENAMES));
        LOG.info("Normalized . . . . . . . . : " + slim.size() + " (remove *ReferenceType or *IdentityType)");

        LOG.info("");
        LOG.info("Ed-Fi-Core:");
        final Set<String> edfiRaw = classNames(edfiModel.getClassTypes().values());
        LOG.info("Raw complexTypes . . . . . : " + edfiRaw.size());
        final Set<String> edfi = subtractEndsWith(subtractEndsWith(edfiRaw, "ReferenceType"), "IdentityType");
        LOG.info("Normalized . . . . . . . . : " + edfi.size() + " (remove *ReferenceType or *IdentityType)");

        LOG.info("");
        LOG.info("Adjustments:");
        LOG.info("");
        LOG.info("Outside Scope (" + OUTSIDE_SCOPE.size() + ")");
        LOG.info("-------------");
        print(sort(OUTSIDE_SCOPE));
        LOG.info("");
        LOG.info("Planned (" + PLANNED.size() + ")");
        LOG.info("-------");
        print(sort(PLANNED));
        LOG.info("");
        LOG.info("Groups (" + GROUPS.size() + ")");
        LOG.info("------");
        print(sort(GROUPS));
        LOG.info("");
        LOG.info("Ignorable (" + IGNORABLE.size() + ")");
        LOG.info("------");
        print(ignorable());
        LOG.info("");
        LOG.info("Investigate differences (" + INVESTIGATE.size() + ")");
        LOG.info("-----------------------");
        print(investigate());
        LOG.info("");
        LOG.info("Renamed (" + CLASS_RENAMES.size() + ")");
        LOG.info("-------");
        print(CLASS_RENAMES);
        final Set<String> edfiOutstanding = subtract(
                subtract(subtract(subtract(subtract(edfi, OUTSIDE_SCOPE), PLANNED), GROUPS), IGNORABLE), INVESTIGATE);

        final Set<String> edfiOutstandingMinusSLI = subtract(edfiOutstanding, slim);
        LOG.info("");
        LOG.info("How does Ed-Fi-Core exceed SLI? (" + edfiOutstandingMinusSLI.size() + ")");
        LOG.info("-------------------------------");
        print(sort(edfiOutstandingMinusSLI));

        final Set<String> slimMinusEdfiOutstanding = subtract(slim, edfiOutstanding);
        LOG.info("");
        LOG.info("How does SLI exceed Ed-Fi-Core? (" + slimMinusEdfiOutstanding.size() + ")");
        LOG.info("-------------------------------");
        print(sort(slimMinusEdfiOutstanding));

        LOG.info("");
        LOG.info("Summary:");
        final int missing = edfiOutstandingMinusSLI.size();
        final int common = edfiOutstanding.size() - missing + PLANNED.size();
        final int excess = slimMinusEdfiOutstanding.size();
        LOG.info("common: " + common);
        LOG.info("missing: " + missing);
        LOG.info("excess: " + excess);

        LOG.info("coverage: " + (common * 100.0d) / (common + missing) + "%");
        LOG.info("");
        LOG.info("Disclaimer: This assumes that attributes of classes are 100% covered.");
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
        for (final Map.Entry<String, String> entry : mapping.entrySet()) {
            final String lhs = entry.getKey();
            final String rhs = entry.getValue();
            inversion.put(rhs, lhs);
        }
        return Collections.unmodifiableMap(inversion);
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
        // PLANNED.add("CTEProgram"); //
        planned.add("ReportCard");                      // Wolverine
        planned.add("StudentCompetency");               // Wolverine
        planned.add("StudentCompetencyObjective");      // Wolverine
        // PLANNED.add("StudentCTEProgramAssociation");
        // PLANNED.add("StudentSpecialEdProgramAssociation");
        return Collections.unmodifiableSet(planned);
    }

    private static final void print(final List<String> strings) {
        for (final String s : strings) {
            LOG.info("" + s);
        }
    }

    private static final void print(final Map<String, String> strings) {
        for (final Map.Entry<String, String> entry : strings.entrySet()) {
            LOG.info(entry.getKey() + " => " + entry.getValue());
        }
    }

    private static final void printGMT() {
        Calendar c = Calendar.getInstance();
        LOG.info("" + c.getTime());
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

    public EdFiSLICmdLine() {
        // Prevent instantiation, even through reflection.
        throw new UnsupportedOperationException();
    }
}
