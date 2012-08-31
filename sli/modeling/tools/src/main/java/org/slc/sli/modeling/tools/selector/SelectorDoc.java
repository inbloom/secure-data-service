/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

import java.util.Map;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.xmi.reader.XmiReader;

/**
 * Command line utility to read an XMI File, and print out features (attributes,
 * associations) of each element type.
 *
 * @author wscott
 *
 */
public class SelectorDoc {

	public final static String ATTRIBUTE = "Attribute";
	public final static String ASSOCIATION = "Association";

    private final static String simpleSectStart = "<simpleSect xml:id = \"selector-%s\">\n";
    private final static String featuresStart   = "    <features>\n";
    private final static String feature         = "        <feature type = \"%s\" name = \"%s\"/>\n";
    private final static String featuresEnd     = "    </features>\n";
    private final static String simpleSectEnd   = "</simpleSect>\n";
    
    private String inputXmiFilename;
    private String outputXmlFilename;
    
    public SelectorDoc(String inputXmiFilename, String outputXmlFilename) {
    	this.inputXmiFilename = inputXmiFilename;
    	this.outputXmlFilename = outputXmlFilename;
    }

	protected String getSelectorDocumentation(ModelIndex modelIndex) {
		StringBuffer stringBuffer = new StringBuffer();

        Map<String, ClassType> classTypes = modelIndex.getClassTypes();
        for(String classTypeKey : classTypes.keySet()) {

            ClassType classType = classTypes.get(classTypeKey);
            String classTypeName = classType.getName();
            
            stringBuffer.append(String.format(simpleSectStart, classTypeName));
            stringBuffer.append(featuresStart);
            
            this.appendClassTypeAttributes(stringBuffer, classType);
            this.appendClassTypeAssociations(stringBuffer, classType, modelIndex);
            
            stringBuffer.append(featuresEnd);
            stringBuffer.append(simpleSectEnd);
            
        }
        
        return stringBuffer.toString();
	}

	protected void appendClassTypeAttributes(StringBuffer stringBuffer, ClassType classType) {
		for (Attribute attribute : classType.getAttributes()) {
            stringBuffer.append(String.format(feature, ATTRIBUTE, attribute.getName()));
        }
	}

	protected void appendClassTypeAssociations(StringBuffer stringBuffer, ClassType classType, ModelIndex modelIndex) {
		for (AssociationEnd associationEnd : modelIndex.getAssociationEnds(classType.getId())) {
        	stringBuffer.append(String.format(feature, ASSOCIATION, associationEnd.getName()));
        }
	}
	
	protected Model readModel() throws FileNotFoundException {
		return XmiReader.readModel(this.inputXmiFilename);
	}

	protected BufferedWriter getBufferedWriter() throws IOException {
		return new BufferedWriter(new FileWriter(this.outputXmlFilename));
	}
	
	protected ModelIndex getModelIndex() {
		final Model model;
        
        try {
            model = this.readModel();
        } catch (FileNotFoundException e) {
            return null;
        }

        if (model != null) {
            return new DefaultModelIndex(model);
        } else {
            return null;
        }
	}
	
	protected void writeSelectorDocumentationToFile(String documentationString) {
		
		if (documentationString == null) {
			return;
		}
		
		try {
            BufferedWriter output = this.getBufferedWriter();
            output.write(documentationString);
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	protected void generateSelectorDocumentation() {
		
		ModelIndex mi = this.getModelIndex();
        String selectorDocumentation = this.getSelectorDocumentation(mi);
        this.writeSelectorDocumentationToFile(selectorDocumentation);
	}
    
    public static void main(String[] args) {
    	new SelectorDoc(args[0], args[1]).generateSelectorDocumentation();
    }

}
