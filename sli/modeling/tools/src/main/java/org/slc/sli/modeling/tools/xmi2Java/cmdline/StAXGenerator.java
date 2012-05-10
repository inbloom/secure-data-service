package org.slc.sli.modeling.tools.xmi2Java.cmdline;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.index.DefaultMapper;
import org.slc.sli.modeling.uml.index.Mapper;
import org.slc.sli.modeling.xmi.reader.XmiReader;

public class StAXGenerator {

    private static final boolean isTemporalClass(final String name) {
        if (name.endsWith("IdentityType")) {
            return true;
        } else if (name.endsWith("ReferenceType")) {
            return true;
        } else if ("Account".equals(name)) {
            return true;
        } else if ("AccountCodeDescriptor".equals(name)) {
            return true;
        } else if ("AccountCodeDescriptorType".equals(name)) {
            return true;
        } else if ("Actual".equals(name)) {
            return true;
        } else if ("AssessmentFamily".equals(name)) {
            return true;
        } else if ("AssessmentPeriodDescriptorType".equals(name)) {
            return true;
        } else if ("Budget".equals(name)) {
            return true;
        } else if ("ClassPeriod".equals(name)) {
            return true;
        } else if ("ComplexObjectType".equals(name)) {
            return true;
        } else if ("ContractedStaff".equals(name)) {
            return true;
        } else if ("CourseLevelCharacteristicsType".equals(name)) {
            return true;
        } else if ("CourseOffering".equals(name)) {
            return true;
        } else if ("CourseTranscript".equals(name)) {
            return true;
        } else if ("CredentialFieldDescriptorType".equals(name)) {
            return true;
        } else if ("EducationalPlansType".equals(name)) {
            return true;
        } else if ("EducationOrganizationCategoriesType".equals(name)) {
            return true;
        } else if ("EducationServiceCenter".equals(name)) {
            return true;
        } else if ("EducationServiceCenter".equals(name)) {
            return true;
        } else if ("FeederSchoolAssociation".equals(name)) {
            return true;
        } else if ("GradeLevelsType".equals(name)) {
            return true;
        } else if ("GradePointAverage".equals(name)) {
            return true;
        } else if ("LanguagesType".equals(name)) {
            return true;
        } else if ("LinguisticAccommodationsType".equals(name)) {
            return true;
        } else if ("LocalEducationAgency".equals(name)) {
            return true;
        } else if ("MeetingDaysType".equals(name)) {
            return true;
        } else if ("Payroll".equals(name)) {
            return true;
        } else if ("PerformanceLevelDescriptorType".equals(name)) {
            return true;
        } else if ("RaceType".equals(name)) {
            return true;
        } else if ("SchoolCategoriesType".equals(name)) {
            return true;
        } else if ("Section504DisabilitiesType".equals(name)) {
            return true;
        } else if ("SpecialAccommodationsType".equals(name)) {
            return true;
        } else if ("StaffEducationOrgAssignmentAssociation".equals(name)) {
            return true;
        } else if ("StaffEducationOrgEmploymentAssociation".equals(name)) {
            return true;
        } else if ("StateEducationAgency".equals(name)) {
            return true;
        } else if ("StudentAssessment".equals(name)) {
            return true;
        } else if ("StudentGradebookEntry".equals(name)) {
            return true;
        } else if ("StudentTitleIPartAProgramAssociation".equals(name)) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(final String[] args) {
        try {
            final Mapper edfi = new DefaultMapper(XmiReader.readModel("Ed-Fi-Core.xmi"));
            final String dirName = "/Users/dholmes/Development/SLI/sli/sli/modeling/tools/src/main/java/org/slc/sli/modeling/ninja/parser";
            final File dir = new File(dirName);
            {
                final String fileName = "EdFiCoreParser.java";
                final File file = new File(dir, fileName);
                writeParserClass("EdFiCoreParser", edfi, file);
            }
            {
                final String fileName = "EdFiCoreElementName.java";
                final File file = new File(dir, fileName);
                writeElementsClass("EdFiCoreElementName", edfi, file);
            }
            for (final ClassType classType : edfi.getClassTypes()) {
                if (isTemporalClass(classType.getName())) {
                    final String fileName = classType.getName().concat(".java");
                    final File file = new File(dir, fileName);
                    final List<String> importNames = new ArrayList<String>();
                    importNames.add("java.math.BigDecimal");
                    importNames.add("java.math.BigInteger");
                    importNames.add("org.slc.sli.modeling.ninja.*");
                    ClassTypeHelper.writeClassType("org.slc.sli.modeling.ninja.parser", importNames, classType, edfi,
                            file);
                }
            }
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static final void writeElementsClass(final String name, final Mapper edfi, final File file) {
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(file));
            try {
                writeElementsClass(name, edfi, outstream);
            } finally {
                CloseableHelper.closeQuiet(outstream);
            }
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final void writeElementsClass(final String name, final Mapper edfi, final OutputStream outstream) {
        final JavaOutputFactory jof = JavaOutputFactory.newInstance();
        try {
            final JavaStreamWriter jsw = jof.createJavaStreamWriter(outstream, "UTF-8");
            try {
                jsw.writePackage("org.slc.sli.modeling.ninja.parser");
                jsw.beginClass(name, null);
                try {
                    final Set<String> featureNames = new HashSet<String>();
                    for (final ClassType classType : edfi.getClassTypes()) {
                        final List<JavaFeature> features = ClassTypeHelper.getFeatures(classType, edfi);
                        for (final JavaFeature feature : features) {
                            featureNames.add(feature.getName());
                        }
                    }
                    for (final String featureName : featureNames) {
                        jsw.writeComment(featureName);
                        jsw.beginStmt();
                        jsw.write("public static final String ").write(featureName.toUpperCase());
                        jsw.write(" = ");
                        jsw.dblQte().write(featureName).dblQte();
                        jsw.endStmt();
                    }

                } finally {
                    jsw.endClass();
                }
            } finally {
                jsw.flush();
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final void writeParserClass(final String name, final Mapper edfi, final File file) {
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(file));
            try {
                writeParserClass(name, edfi, outstream);
            } finally {
                CloseableHelper.closeQuiet(outstream);
            }
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final void writeParserClass(final String name, final Mapper edfi, final OutputStream outstream) {
        final JavaOutputFactory jof = JavaOutputFactory.newInstance();
        try {
            final JavaStreamWriter jsw = jof.createJavaStreamWriter(outstream, "UTF-8");
            try {
                jsw.writePackage("org.slc.sli.modeling.ninja.parser");
                jsw.writeImport("org.slc.sli.modeling.ninja.*");
                jsw.writeImport("javax.xml.stream.XMLStreamConstants");
                jsw.writeImport("javax.xml.stream.XMLStreamException");
                jsw.writeImport("javax.xml.stream.XMLStreamReader");
                jsw.beginClass(name, "StAXReader");
                try {
                    for (final ClassType classType : edfi.getClassTypes()) {
                        final List<JavaFeature> features = ClassTypeHelper.getFeatures(classType, edfi);
                        jsw.write("public static final ").write(classType.getName()).write(" read")
                                .write(classType.getName())
                                .write("(final XMLStreamReader reader) throws XMLStreamException");
                        jsw.write("{");
                        {
                            for (final JavaFeature feature : features) {
                                jsw.beginStmt().write(feature.getType()).write(" ").elementName(feature.getName())
                                        .write(" = ").write("null").endStmt();
                            }
                        }
                        jsw.write("  while (reader.hasNext())");
                        jsw.write("  {");
                        jsw.write("    reader.next()").endStmt();
                        jsw.write("    switch(reader.getEventType())");
                        jsw.write("    {");
                        jsw.write("      case XMLStreamConstants.START_ELEMENT:");
                        jsw.beginBlock();
                        {
                            boolean first = true;
                            for (final JavaFeature feature : features) {
                                if (first) {
                                    first = false;
                                    jsw.write("if(match(EdFiCoreElementName.").write(feature.getName().toUpperCase())
                                            .write(", reader))");
                                } else {
                                    jsw.write("else if(match(EdFiCoreElementName.")
                                            .write(feature.getName().toUpperCase()).write(", reader))");
                                }
                                jsw.beginBlock();
                                jsw.beginStmt().elementName(feature.getName());
                                jsw.write(" = read").write(feature.getType()).write("(reader)").endStmt();
                                jsw.endBlock();
                            }
                            if (!first) {
                                jsw.write("else");
                            }
                            jsw.beginBlock();
                            jsw.beginStmt().write("skipElement(reader)").endStmt();
                            jsw.endBlock();
                        }
                        jsw.beginStmt().write("break").endStmt();
                        jsw.endBlock();
                        jsw.write("      case XMLStreamConstants.END_ELEMENT:");
                        jsw.beginBlock();
                        jsw.beginStmt().write("return new ").write(classType.getName());
                        jsw.write("(");
                        {
                            boolean first = true;
                            for (final JavaFeature feature : features) {
                                if (first) {
                                    first = false;
                                } else {
                                    jsw.write(", ");
                                }
                                jsw.elementName(feature.getName());
                            }
                        }
                        jsw.write(")");
                        jsw.endStmt();
                        jsw.endBlock();
                        jsw.write("      case XMLStreamConstants.CHARACTERS:");
                        jsw.write("      case XMLStreamConstants.COMMENT:");
                        jsw.write("      {");
                        jsw.write("        break").endStmt();
                        jsw.write("      }");
                        jsw.write("      default:");
                        jsw.write("      {");
                        jsw.write("        throw new AssertionError(reader.getEventType())").endStmt();
                        jsw.write("      }");
                        jsw.write("    }");
                        jsw.write("  }");
                        jsw.write("  throw new AssertionError()").endStmt();
                        jsw.write("}");
                    }
                    for (final EnumType enumType : edfi.getEnumTypes()) {
                        jsw.write("public static final ").write(enumType.getName()).write(" read")
                                .write(enumType.getName())
                                .write("(final XMLStreamReader reader) throws XMLStreamException");
                        jsw.write("{");
                        jsw.write("  while (reader.hasNext())");
                        jsw.write("  {");
                        jsw.write("    reader.next()").endStmt();
                        jsw.write("    switch(reader.getEventType())");
                        jsw.write("    {");
                        jsw.write("      case XMLStreamConstants.START_ELEMENT:");
                        jsw.beginBlock();
                        jsw.beginStmt().write("break").endStmt();
                        jsw.endBlock();
                        jsw.write("      case XMLStreamConstants.END_ELEMENT:");
                        jsw.beginBlock();
                        jsw.beginStmt().write("return new ").write(enumType.getName());
                        jsw.write("(");
                        jsw.write(")");
                        jsw.endStmt();
                        jsw.endBlock();
                        jsw.write("      case XMLStreamConstants.CHARACTERS:");
                        jsw.write("      case XMLStreamConstants.COMMENT:");
                        jsw.write("      {");
                        jsw.write("        break").endStmt();
                        jsw.write("      }");
                        jsw.write("      default:");
                        jsw.write("      {");
                        jsw.write("        throw new AssertionError(reader.getEventType())").endStmt();
                        jsw.write("      }");
                        jsw.write("    }");
                        jsw.write("  }");
                        jsw.write("  throw new AssertionError()").endStmt();
                        jsw.write("}");
                    }
                    for (final DataType dataType : edfi.getDataTypes()) {
                        jsw.write("public static final ").write(dataType.getName()).write(" read")
                                .write(dataType.getName())
                                .write("(final XMLStreamReader reader) throws XMLStreamException");
                        jsw.write("{");
                        jsw.write("  while (reader.hasNext())");
                        jsw.write("  {");
                        jsw.write("    reader.next()").endStmt();
                        jsw.write("    switch(reader.getEventType())");
                        jsw.write("    {");
                        jsw.write("      case XMLStreamConstants.START_ELEMENT:");
                        jsw.beginBlock();
                        jsw.beginStmt().write("break").endStmt();
                        jsw.endBlock();
                        jsw.write("      case XMLStreamConstants.END_ELEMENT:");
                        jsw.beginBlock();
                        jsw.beginStmt().write("return new ").write(dataType.getName());
                        jsw.write("(");
                        jsw.write(")");
                        jsw.endStmt();
                        jsw.endBlock();
                        jsw.write("      case XMLStreamConstants.CHARACTERS:");
                        jsw.write("      case XMLStreamConstants.COMMENT:");
                        jsw.write("      {");
                        jsw.write("        break").endStmt();
                        jsw.write("      }");
                        jsw.write("      default:");
                        jsw.write("      {");
                        jsw.write("        throw new AssertionError(reader.getEventType())").endStmt();
                        jsw.write("      }");
                        jsw.write("    }");
                        jsw.write("  }");
                        jsw.write("  throw new AssertionError()").endStmt();
                        jsw.write("}");
                    }
                } finally {
                    jsw.endClass();
                }
            } finally {
                jsw.flush();
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
