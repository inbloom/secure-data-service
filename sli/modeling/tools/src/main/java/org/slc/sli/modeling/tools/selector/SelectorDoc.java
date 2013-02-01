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

package org.slc.sli.modeling.tools.selector;

import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Command line utility to read an XMI File, and print out features (attributes,
 * associations) of each element type.
 *
 * @author wscott
 */
public class SelectorDoc {

    private static final Logger LOG = LoggerFactory.getLogger(SelectorDoc.class);

    public static final String ATTRIBUTE = "Attribute";
    public static final String ASSOCIATION = "Association";

    protected static final String SIMPLE_SECT_START = "<simpleSect xml:id = \"selector-%s\">\n";
    protected static final String FEATURES_START = "    <features>\n";
    protected static final String FEATURE = "        <feature type = \"%s\" name = \"%s\"/>\n";
    protected static final String FEATURES_END = "    </features>\n";
    protected static final String SIMPLE_SECT_END = "</simpleSect>\n";

    private String inputXmiFilename;
    private String outputXmlFilename;

    public SelectorDoc(String inputXmiFilename, String outputXmlFilename) {
        this.inputXmiFilename = inputXmiFilename;
        this.outputXmlFilename = outputXmlFilename;
    }

    protected String getSelectorDocumentation(ModelIndex modelIndex) {
        StringBuffer stringBuffer = new StringBuffer();

        Map<String, ClassType> classTypes = modelIndex.getClassTypes();

        for (Entry<String, ClassType> classTypeEntry : classTypes.entrySet()) {

            ClassType classType = classTypeEntry.getValue();

            stringBuffer.append(String.format(SIMPLE_SECT_START, classType.getName()));
            stringBuffer.append(FEATURES_START);

            this.appendClassTypeAttributes(stringBuffer, classType);
            this.appendClassTypeAssociations(stringBuffer, classType, modelIndex);

            stringBuffer.append(FEATURES_END);
            stringBuffer.append(SIMPLE_SECT_END);

        }

        return stringBuffer.toString();
    }

    protected void appendClassTypeAttributes(StringBuffer stringBuffer, ClassType classType) {
        for (Attribute attribute : classType.getAttributes()) {
            stringBuffer.append(String.format(FEATURE, ATTRIBUTE, attribute.getName()));
        }
    }

    protected void appendClassTypeAssociations(StringBuffer stringBuffer, ClassType classType, ModelIndex modelIndex) {
        for (AssociationEnd associationEnd : modelIndex.getAssociationEnds(classType.getId())) {
            stringBuffer.append(String.format(FEATURE, ASSOCIATION, associationEnd.getName()));
        }
    }

    protected Model readModel() throws FileNotFoundException {
        return XmiReader.readModel(this.inputXmiFilename);
    }

    protected Writer getBufferedWriter() throws IOException {
        return new BufferedWriter(new FileWriter(this.outputXmlFilename));
    }

    protected ModelIndex getModelIndex(Model model) {
        if (model != null) {
            return new DefaultModelIndex(model);
        } else {
            return null;
        }
    }

    protected boolean writeSelectorDocumentationToFile(String documentationString, Writer output) {

        if (documentationString == null) {
            return false;
        }

        try {
            output.write(documentationString);
            output.flush();
            output.close();
        } catch (IOException e) {
            LOG.warn(e.getMessage());
            return false;
        }
        return true;
    }

    protected void generateSelectorDocumentation() throws IOException {

        ModelIndex mi = this.getModelIndex(this.readModel());
        String selectorDocumentation = this.getSelectorDocumentation(mi);
        this.writeSelectorDocumentationToFile(selectorDocumentation, this.getBufferedWriter());
    }

    public static void main(String[] args) {
        try {
            new SelectorDoc(args[0], args[1]).generateSelectorDocumentation();
        } catch (IOException e) {
            LOG.warn(e.getMessage());
        }
    }

}
