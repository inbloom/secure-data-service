package org.slc.sli.modeling.sdkgen;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaChoice;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaDatatype;
import org.apache.ws.commons.schema.XmlSchemaDerivationMethod;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaType;

import org.slc.sli.modeling.jgen.JavaParam;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Include;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Param;
import org.slc.sli.modeling.rest.Representation;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.rest.Response;
import org.slc.sli.modeling.rest.helpers.RestHelper;
import org.slc.sli.modeling.sdkgen.grammars.SdkGenGrammars;
import org.slc.sli.modeling.sdkgen.grammars.SdkGenResolver;
import org.slc.sli.modeling.sdkgen.grammars.xsd.SdkGenGrammarsWrapper;
import org.slc.sli.modeling.xsd.XsdReader;

public final class Level3ClientInterfaceWriter extends Level3ClientWriter {

    private final String packageName;
    private final String className;
    private final File wadlFile;
    private boolean first = true;
    /**
     * As we encounter schemas in the grammars, we add them here.
     */
    final List<XmlSchema> schemas = new LinkedList<XmlSchema>();

    public Level3ClientInterfaceWriter(final String packageName, final String className, final File wadlFile,
            final JavaStreamWriter jsw) {
        super(jsw);
        if (packageName == null) {
            throw new NullPointerException("packageName");
        }
        if (className == null) {
            throw new NullPointerException("className");
        }
        if (wadlFile == null) {
            throw new NullPointerException("wadlFile");
        }
        this.packageName = packageName;
        this.className = className;
        this.wadlFile = wadlFile;
    }

    @Override
    public void beginApplication(final Application application) {
        try {
            jsw.writePackage(packageName);
            jsw.writeImport("java.io.IOException");
            jsw.writeImport("java.util.List");
            jsw.writeImport("java.util.Map");
            jsw.beginInterface(className);
            for (final Include include : application.getGrammars().getIncludes()) {
                final File schemaFile = new File(wadlFile.getParentFile(), include.getHref());
                schemas.add(XsdReader.readSchema(schemaFile, new SdkGenResolver()));
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showElement(final XmlSchemaElement element, final Stack<QName> elementNames) {
        final QName elementName = element.getQName();
        System.out.println("element : " + elementName);
        final long minOccurs = element.getMinOccurs();
        final long maxOccurs = element.getMaxOccurs();
        System.out.println("minOccurs : " + minOccurs);
        System.out.println("maxOccurs : " + maxOccurs);
        if (elementNames.contains(elementName)) {
            // Avoid infinite recursion.
            return;
        }
        elementNames.push(elementName);
        try {
            final XmlSchemaType schemaType = element.getSchemaType();
            showSchemaType(schemaType, elementNames);
        } finally {
            elementNames.pop();
        }
    }

    private void showSchemaType(final XmlSchemaType schemaType, final Stack<QName> elementNames) {
        if (schemaType instanceof XmlSchemaComplexType) {
            final XmlSchemaComplexType complexType = (XmlSchemaComplexType) schemaType;
            showComplexType(complexType, elementNames);
        } else {
            System.out.println("schemaType : " + schemaType);
        }
    }

    private void showComplexType(final XmlSchemaComplexType complexType, final Stack<QName> elementNames) {
        System.out.println("complexType : " + complexType);
        final QName name = complexType.getQName();
        System.out.println("complexType.name : " + name);
        if (name != null) {

        } else {
            @SuppressWarnings("unused")
            final XmlSchemaDatatype dataType = complexType.getDataType();
            // System.out.println("dataType : " + dataType);
            @SuppressWarnings("unused")
            final XmlSchemaDerivationMethod deriveBy = complexType.getDeriveBy();
            // System.out.println("deriveBy : " + deriveBy);
            final XmlSchemaParticle particle = complexType.getParticle();
            if (particle != null) {
                showParticle(particle, elementNames);
            }
        }
    }

    private void showParticle(final XmlSchemaParticle particle, final Stack<QName> elementNames) {
        if (particle == null) {
            throw new NullPointerException("particle");
        }
        if (particle instanceof XmlSchemaSequence) {
            final XmlSchemaSequence sequence = (XmlSchemaSequence) particle;
            showSequence(sequence, elementNames);
        } else {
            System.out.println("particle : " + particle);
            throw new AssertionError(particle);
        }
    }

    private void showSequence(final XmlSchemaSequence sequence, final Stack<QName> elementNames) {
        System.out.println("sequence : " + sequence);
        final XmlSchemaObjectCollection items = sequence.getItems();
        final int count = items.getCount();
        for (int i = 0; i < count; i++) {
            final XmlSchemaObject schemaObject = items.getItem(i);
            showObject(schemaObject, elementNames);
        }
    }

    private void showObject(final XmlSchemaObject schemaObject, final Stack<QName> elementNames) {
        if (schemaObject instanceof XmlSchemaElement) {
            final XmlSchemaElement element = (XmlSchemaElement) schemaObject;
            // Just gone recursive on element depth.
            showElement(element, elementNames);
        } else if (schemaObject instanceof XmlSchemaChoice) {
            System.out.println("choice : " + schemaObject);
        } else {
            System.out.println("schemaObject : " + schemaObject);
            throw new AssertionError(schemaObject);
        }
    }

    @Override
    protected void writeGET(final Method method, final Resource resource, final Resources resources,
            final Application application, final Stack<Resource> ancestors) throws IOException {

        // We're going to need to be able to analyze the request and response types.
        final SdkGenGrammars grammars = new SdkGenGrammarsWrapper(schemas);

        jsw.writeComment(method.getId());
        jsw.beginStmt();
        try {
            final List<Response> responses = method.getResponses();
            for (final Response response : responses) {
                try {
                    final List<Representation> representations = response.getRepresentations();
                    for (final Representation representation : representations) {
                        representation.getMediaType();
                        final QName elementName = representation.getElement();
                        final XmlSchemaElement element = grammars.getElement(elementName);
                        if (element != null) {
                            final Stack<QName> elementNames = new Stack<QName>();
                            if (first) {
                                showElement(element, elementNames);
                                first = false;
                            }
                        } else {
                            // FIXME: We need to resolve these issues...
                            // System.out.println(elementName +
                            // " cannot be resolved as a schema element name.");
                        }
                    }
                } finally {
                }
            }
            jsw.writeType(GENERIC_ENTITY).space().write(method.getId());
            jsw.parenL();
            final List<Param> templateParams = RestHelper.computeRequestTemplateParams(resource, ancestors);
            final List<JavaParam> params = Level2ClientJavaHelper.computeJavaGETParams(templateParams);
            jsw.writeParams(params);
            jsw.parenR();
            jsw.writeThrows(IO_EXCEPTION, STATUS_CODE_EXCEPTION);

        } finally {
            jsw.endStmt();
        }
    }

    @Override
    protected void writePOST(Method method, Resource resource, Resources resources, Application application,
            Stack<Resource> ancestors) throws IOException {
    }

    @Override
    protected void writePUT(Method method, Resource resource, Resources resources, Application application,
            Stack<Resource> ancestors) throws IOException {
    }

    @Override
    protected void writeDELETE(Method method, Resource resource, Resources resources, Application application,
            Stack<Resource> ancestors) throws IOException {
    }

    @Override
    public void beginResource(final Resource resource, final Resources resources, final Application app,
            final Stack<Resource> ancestors) {
    }

    @Override
    public void endApplication(final Application application) {
        try {
            jsw.endClass();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void endResource(final Resource resource, final Resources resources, final Application app,
            final Stack<Resource> ancestors) {
    }
}
