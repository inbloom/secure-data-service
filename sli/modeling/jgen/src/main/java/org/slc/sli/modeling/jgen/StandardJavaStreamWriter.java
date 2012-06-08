package org.slc.sli.modeling.jgen;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.slc.sli.modeling.uml.Feature;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;

public final class StandardJavaStreamWriter implements JavaStreamWriter {

    private static final String SPACE = " ";
    private static final String LPAREN = "(";
    private static final String RPAREN = ")";
    private static final String SEMICOLON = ";";
    private static final String COMMA = ",";
    private static final String CR = "\r";
    @SuppressWarnings("unused")
    private static final String LF = "\n";
    private static final String DBLQTE = "\"";

    private static final String camelCase(final String text) {
        return text.substring(0, 1).toLowerCase().concat(text.substring(1));
    }

    private static final String makeDefensiveCopy(final JavaFeature javaFeature, final JavaGenConfig config) {
        final Feature feature = javaFeature.getFeature();
        final Multiplicity multiplicity = feature.getMultiplicity();
        final Range range = multiplicity.getRange();
        final String primeType = javaFeature.getPrimeTypeName(config);
        if (range.getUpper() == Occurs.UNBOUNDED) {
            return "Collections.unmodifiableList(new ArrayList<" + primeType + ">("
                    + camelCase(javaFeature.getName(config)) + "))";
        } else {
            return camelCase(feature.getName());
        }
    }

    private static final String titleCase(final String text) {
        return text.substring(0, 1).toUpperCase().concat(text.substring(1));
    }

    private final OutputStreamWriter writer;

    private final JavaGenConfig config;

    public StandardJavaStreamWriter(final OutputStream stream, final String encoding, final JavaGenConfig config)
            throws UnsupportedEncodingException {
        this.writer = new OutputStreamWriter(stream, encoding);
        this.config = config;
    }

    private void assign(final String lhs, final String rhs) throws IOException {
        beginStmt().write(lhs).write(" = ").write(rhs).endStmt();
    }

    @Override
    public JavaStreamWriter beginBlock() throws IOException {
        writer.write("{");
        return this;
    }

    @Override
    public void beginClass(final String name) throws IOException {
        if (name == null) {
            throw new NullPointerException("name");
        }
        writer.write("public");
        writer.write(SPACE);
        writer.write("final");
        writer.write(SPACE);
        writer.write("class");
        writer.write(SPACE);
        writer.write(name);
        beginBlock();
    }

    @Override
    public void beginClass(final String name, final List<String> implementations) throws IOException {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (implementations == null) {
            throw new NullPointerException("implementations");
        }
        writer.write("public");
        writer.write(SPACE);
        writer.write("final");
        writer.write(SPACE);
        writer.write("class");
        writer.write(SPACE);
        writer.write(name);
        if (!implementations.isEmpty()) {
            writer.write(" implements ");
            for (final String implementation : implementations) {
                writer.write(implementation);
            }
        }
        beginBlock();
    }

    @Override
    public void beginClass(final String name, final String extendsClass) throws IOException {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (extendsClass == null) {
            throw new NullPointerException("extendsClass");
        }
        writer.write("public");
        writer.write(SPACE);
        writer.write("final");
        writer.write(SPACE);
        writer.write("class");
        writer.write(SPACE);
        writer.write(name);
        if (null != extendsClass) {
            writer.write(" extends ");
            writer.write(extendsClass);
        }
        beginBlock();
    }

    @Override
    public void beginEnum(final String name) throws IOException {
        writer.write("public");
        writer.write(SPACE);
        writer.write("enum");
        writer.write(SPACE);
        writer.write(name);
        beginBlock();
    }

    @Override
    public void beginInterface(final String name) throws IOException {
        if (name == null) {
            throw new NullPointerException("name");
        }
        writer.write("public");
        writer.write(SPACE);
        writer.write("interface");
        writer.write(SPACE);
        writer.write(name);
        beginBlock();
    }

    @Override
    public JavaStreamWriter beginStmt() throws IOException {
        return this;
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

    @Override
    public JavaStreamWriter dblQte() throws IOException {
        writer.write(DBLQTE);
        return this;
    }

    @Override
    public JavaStreamWriter elementName(final String name) throws IOException {
        writer.write(camelCase(name));
        return this;
    }

    @Override
    public void endBlock() throws IOException {
        writer.write("}");
    }

    @Override
    public void endClass() throws IOException {
        endBlock();
    }

    @Override
    public void endEnum() throws IOException {
        endBlock();
    }

    @Override
    public void endStmt() throws IOException {
        writer.write(SEMICOLON);
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }

    @Override
    public JavaStreamWriter write(final String text) throws IOException {
        writer.write(text);
        return this;
    }

    @Override
    public void writeAccessor(final String name, final String typeName) throws IOException {
        writer.write("public");
        writer.write(SPACE);
        writer.write(typeName);
        writer.write(SPACE);
        writer.write("get");
        writer.write(titleCase(name));
        writer.write(LPAREN);
        writer.write(RPAREN);
        beginBlock();
        writer.write("return");
        writer.write(SPACE);
        writer.write(camelCase(name));
        writer.write(SEMICOLON);
        endBlock();
    }

    @Override
    public void writeAttribute(final String name, final String typeName) throws IOException {
        writer.write("private");
        writer.write(SPACE);
        writer.write("final");
        writer.write(SPACE);
        writer.write(typeName);
        writer.write(SPACE);
        writer.write(camelCase(name));
        writer.write(SEMICOLON);
    }

    @Override
    public void writeComma() throws IOException {
        writer.write(COMMA);
    }

    @Override
    public void writeOverride() throws IOException {
        writer.write("@Override");
        writer.write(CR);
    }

    @Override
    public void writeComment(final String comment) throws IOException {
        writer.write(CR);
        writer.write("/**");
        writer.write(CR);
        writer.write(" * ");
        writer.write(comment);
        writer.write(CR);
        writer.write(" */");
        writer.write(CR);
    }

    @Override
    public void writeEnumLiteral(final String name, final String unused) throws IOException {
        final String replaced = name.toUpperCase().trim().replace(' ', '_').replace("-", "_HYPHEN_")
                .replace("/", "_FWDSLASH_").replace(",", "_COMMA_").replace("(", "_LPAREN_").replace(")", "_RPAREN_")
                .replace("'", "_APOS_").replace(":", "_COLON_").replace(";", "_SEMICOLON_").replace(".", "_PERIOD_")
                .replace("’", "_RIGHTAPOS_");
        if (Character.isJavaIdentifierStart(replaced.charAt(0))) {
            writer.write(replaced);
        } else {
            writer.write("FUDGED_".concat(replaced));
        }
    }

    @Override
    public void writeImport(final String name) throws IOException {
        writer.write("import");
        writer.write(SPACE);
        writer.write(name);
        writer.write(SEMICOLON);
    }

    @Override
    public void writeInitializer(final String name, List<JavaFeature> features) throws IOException {
        writer.write("public");
        writer.write(SPACE);
        writer.write(name);
        writer.write("(");
        {
            // Arguments
            boolean first = true;
            for (final JavaFeature feature : features) {
                if (feature.isAttribute() || feature.isNavigable()) {
                    if (first) {
                        first = false;
                    } else {
                        writer.write(COMMA);
                        writer.write(SPACE);
                    }
                    writer.write("final");
                    writer.write(SPACE);
                    if (feature.isAttribute()) {
                        writer.write(feature.getAttributeTypeName(config));
                    } else if (feature.isNavigable()) {
                        writer.write(feature.getNavigableTypeName());
                    }
                    writer.write(SPACE);
                    writer.write(camelCase(feature.getName(config)));
                }
            }
        }
        writer.write(")");
        beginBlock();
        // Preconditions
        for (final JavaFeature feature : features) {
            if (feature.isAttribute() || feature.isNavigable()) {
                if (!feature.isOptional()) {
                    final String argName = camelCase(feature.getName(config));
                    write("if (null == ").write(argName).write(")");
                    beginBlock();
                    beginStmt().write("throw new NullPointerException(").dblQte().write(argName).dblQte().write(")")
                            .endStmt();
                    endBlock();
                }
            }
        }
        // Assignments
        for (final JavaFeature feature : features) {
            if (feature.isAttribute() || feature.isNavigable()) {
                final String lhs = "this.".concat(camelCase(feature.getName(config)));
                final String rhs = makeDefensiveCopy(feature, config);
                assign(lhs, rhs);
            }
        }
        endBlock();
    }

    @Override
    public void writePackage(final String name) throws IOException {
        writer.write("package");
        writer.write(SPACE);
        writer.write(name);
        writer.write(SEMICOLON);
    }
}
