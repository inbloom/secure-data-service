package org.slc.sli.ingestion.jaxb.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.ReflectionUtils;

/**
 * Generate template mappings for an SLI domain class
 *
 * @author dduran
 *
 */
public class MappingGenerator {

    private static final String ED_FI_DOMAIN_PACKAGE = "org.slc.sli.ingestion.jaxb.domain.edfi";
    private static final String SLI_DOMAIN_PACKAGE = "org.slc.sli.ingestion.jaxb.domain.sli";

    public static void main(String[] args) {
        // generateClassMapping(someclass.class);
    }

    /**
     * Generate and print to standard out the template mapping code for the provided SLI domain
     * class
     *
     * @param clazz
     */
    public static void generateClassMapping(final Class<?> clazz) {

        Set<ClassDescriptor> complexFieldClasses = new LinkedHashSet<ClassDescriptor>();
        complexFieldClasses.add(new ClassDescriptor(clazz, false));

        // first traverse the class tree to find classes we will need to map
        traverseClassMembers(clazz, complexFieldClasses);

        // write mapping for all classes identified by traversal
        for (ClassDescriptor complexFieldClass : complexFieldClasses) {
            generateMapForComplexType(complexFieldClass.clazz, complexFieldClass.isList);
        }
    }

    private static void traverseClassMembers(final Class<?> clazz, final Collection<ClassDescriptor> complexFieldClasses) {
        // traverse a class and execute callback on all fields
        ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {

                if (field.getType() == List.class) {
                    // a list we will need to map. below we identify the parameterized type of list
                    if (field.getGenericType() instanceof ParameterizedType) {
                        ParameterizedType pt = (ParameterizedType) field.getGenericType();
                        if (pt.getActualTypeArguments().length > 0) {
                            Class actualType = (Class) pt.getActualTypeArguments()[0];

                            complexFieldClasses.add(new ClassDescriptor(actualType, true));
                            traverseClassMembers(actualType, complexFieldClasses);
                        }
                    }
                } else if (field.getType().getName().startsWith(SLI_DOMAIN_PACKAGE)
                        && (!field.getType().getName().endsWith("Type"))) {
                    // sli domain class that we will need to map
                    complexFieldClasses.add(new ClassDescriptor(field.getType(), false));
                    traverseClassMembers(field.getType(), complexFieldClasses);
                }
            }
        });
    }

    private static void generateMapForComplexType(final Class<?> clazz, boolean isList) {

        // method header
        if (isList) {
            generateListMapHeader(clazz);
        } else {
            generateSimpleMapHeader(clazz);
        }

        // mappings
        generateClassFieldMappings(clazz);

        // method footer
        if (isList) {
            generateListMapFooter(clazz);
        } else {
            generateSimpleMapFooter(clazz);
        }
    }

    private static void generateSimpleMapHeader(final Class<?> clazz) {
        out("public static " + clazz.getName() + " map" + clazz.getSimpleName() + "(" + ED_FI_DOMAIN_PACKAGE + "."
                + clazz.getSimpleName() + " edFi" + clazz.getSimpleName() + ") {");

        out(clazz.getName() + " sli" + clazz.getSimpleName() + " = new " + clazz.getName() + "();");
    }

    private static void generateSimpleMapFooter(final Class<?> clazz) {
        out("return sli" + clazz.getSimpleName() + ";");
        out("}");
    }

    private static void generateListMapHeader(final Class<?> clazz) {

        out("public static java.util.Collection<" + clazz.getName() + "> map" + clazz.getSimpleName()
                + "List(java.util.List<" + ED_FI_DOMAIN_PACKAGE + "." + clazz.getSimpleName() + "> edFi"
                + clazz.getSimpleName() + "List) {");

        out("java.util.Collection<" + clazz.getName() + "> sli" + clazz.getSimpleName()
                + "List = new java.util.ArrayList<" + clazz.getName() + ">(edFi" + clazz.getSimpleName()
                + "List.size());");

        out("for (" + ED_FI_DOMAIN_PACKAGE + "." + clazz.getSimpleName() + " edFi" + clazz.getSimpleName() + " : edFi"
                + clazz.getSimpleName() + "List) {");

        if (clazz.getSimpleName().endsWith("Type")) {
            // for enum we can do the mapping here
            out(clazz.getName() + " sli" + clazz.getSimpleName() + " = " + clazz.getName() + ".fromValue(edFi"
                    + clazz.getSimpleName() + ".value());");
        } else {
            // instantiate item mapped to (field mappings will be done later)
            out(clazz.getName() + " sli" + clazz.getSimpleName() + " = new " + clazz.getName() + "();");
        }
    }

    private static void generateListMapFooter(final Class<?> clazz) {
        out("sli" + clazz.getSimpleName() + "List.add(sli" + clazz.getSimpleName() + ");");
        out("}");
        out("return sli" + clazz.getSimpleName() + "List;");
        out("}");
    }

    private static void generateClassFieldMappings(final Class<?> clazz) {

        // write out the mappign code for fields in this class (unless it is an enum!)
        if (!clazz.getName().endsWith("Type")) { // no enums!

            ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
                @Override
                public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                    String fieldPascalCase = Character.toUpperCase(field.getName().charAt(0))
                            + field.getName().substring(1);

                    // basic structure of a mapping that we can fill in based on the field type
                    String setFieldString = "sli" + clazz.getSimpleName() + ".set" + fieldPascalCase
                            + "(%EDFI_GETTER%);";

                    if (field.getType() == List.class) {

                        // lists
                        String actualTypeName = null;
                        if (field.getGenericType() instanceof ParameterizedType) {
                            ParameterizedType pt = (ParameterizedType) field.getGenericType();
                            if (pt.getActualTypeArguments().length > 0) {
                                Class actualType = (Class) pt.getActualTypeArguments()[0];
                                actualTypeName = actualType.getSimpleName();
                            }
                        }

                        setFieldString = "sli" + clazz.getSimpleName() + ".get" + fieldPascalCase + "().addAll(map"
                                + actualTypeName + "List(edFi" + clazz.getSimpleName() + ".get" + fieldPascalCase
                                + "()));";
                    } else if (field.getType().getName().endsWith("Type")) {

                        // enums
                        setFieldString = setFieldString.replaceAll("%EDFI_GETTER%", field.getType().getName()
                                + ".fromValue(edFi" + clazz.getSimpleName() + ".get" + fieldPascalCase + "().value())");
                    } else if (field.getType().getName().startsWith(SLI_DOMAIN_PACKAGE)) {

                        // complex types (in SLI domain)
                        setFieldString = setFieldString.replaceAll("%EDFI_GETTER%", "map" + fieldPascalCase + "(edFi"
                                + clazz.getSimpleName() + ".get" + fieldPascalCase + "())");
                    } else if (field.getType() == Boolean.class) {

                        // booleans
                        setFieldString = setFieldString.replaceAll("%EDFI_GETTER%", "edFi" + clazz.getSimpleName()
                                + ".is" + fieldPascalCase + "()");
                    } else {

                        // all other types (assume simple)
                        setFieldString = setFieldString.replaceAll("%EDFI_GETTER%", "edFi" + clazz.getSimpleName()
                                + ".get" + fieldPascalCase + "()");
                    }

                    out(setFieldString);
                }
            });
        }
    }

    private static void out(String outString) {
        System.out.println(outString);
    }

    private static final class ClassDescriptor implements Comparable<ClassDescriptor> {
        private Class clazz;
        private boolean isList;

        private ClassDescriptor(Class clazz, boolean isList) {
            this.clazz = clazz;
            this.isList = isList;
        }

        @Override
        public int compareTo(ClassDescriptor o) {
            if (this.equals(o))
                return 0;
            return 1;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
            result = prime * result + (isList ? 1231 : 1237);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ClassDescriptor other = (ClassDescriptor) obj;
            if (clazz == null) {
                if (other.clazz != null)
                    return false;
            } else if (!clazz.equals(other.clazz))
                return false;
            if (isList != other.isList)
                return false;
            return true;
        }

    }
}
