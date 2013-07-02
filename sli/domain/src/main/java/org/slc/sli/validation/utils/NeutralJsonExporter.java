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


package org.slc.sli.validation.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.slc.sli.validation.schema.NeutralSchemaJSONStringWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.slc.sli.validation.NeutralSchemaFactory;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.schema.NeutralSchema;

/**
 * Utility class for exporting Neutral Schema files to flat files.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
public class NeutralJsonExporter {
    
    private static final Logger LOG = LoggerFactory.getLogger(NeutralJsonExporter.class);
    
    /**
     * Loads the XSD files and dumps the Neutral Schema objects to JSON files in the specified
     * directory.
     * 
     * Option 1:
     * Param 1: xsdDirectory (defaults to "classpath:sliXsd")
     * Param 2: outputDir (defaults to "neutral-schemas")
     * 
     * Option 2: (does not print out JSON files)
     * Param 1: --test
     * Param 2: xsdDirectory (defaults to classpath:sliXsd)
     * 
     * @param args command line arguments
     * @throws IOException
     */
    @SuppressWarnings({"PMD.SystemPrintln"})   // this is main function of a utility potentiall executed from the command line  
    public static void main(String[] args) throws IOException {
        String xsdPath = "classpath:sliXsd";
        String outputDir = "neutral-schemas";
        PrintStream outputStream = System.out;
        boolean output = true;
        if (args.length == 2 && !args[0].equals("--test")) {
            xsdPath = args[0];
            outputDir = args[1];
        } else if (args.length == 2 && args[0].equals("--test")) {
            xsdPath = args[1];
            output = false;
        }
        
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
                new String[] { "spring/neutral-json-exporter-config.xml" });
        
        XsdToNeutralSchemaRepo repo = new XsdToNeutralSchemaRepo(xsdPath, new NeutralSchemaFactory());
        repo.setApplicationContext(appContext);
        
        File enumDir = new File(outputDir, "enums");
        File primitiveDir = new File(outputDir, "primitive");
        File complexDir = new File(outputDir, "complexDir");
        
        try {
            if (!enumDir.exists() && !enumDir.mkdirs()) {
                System.err.println("Failed to create enum output directory");
                return;
            }
            if (!primitiveDir.exists() && !primitiveDir.mkdirs()) {
                System.err.println("Failed to freate primitive output directory");
                return;
            }
            if (!complexDir.exists() && !complexDir.mkdirs()) {
                System.err.println("Failed to create complex output directory");
                return;
            }
        } catch (SecurityException e) {
            System.err.println("Failed to create output directories. Error:" + e.getLocalizedMessage());
        }
        
        List<NeutralSchema> schemas = repo.getSchemas();
        
        // sanity check consistency
        Set<String> schemaNames = new HashSet<String>();
        for (NeutralSchema ns : schemas) {
            schemaNames.add(ns.getType());
        }
        boolean sane = true;
        for (NeutralSchema ns : schemas) {
            for (Entry<String, NeutralSchema> entry : ns.getFields().entrySet()) {
                Object obj = entry.getValue();
                if (obj instanceof NeutralSchema) {
                    NeutralSchema field = (NeutralSchema) obj;
                    if (!schemaNames.contains(field.getType()) && NeutralSchemaType.findByName(field.getType()) == null) {
                        sane = false;
                        LOG.error("Missing schema for field: " + ns.getType() + "." + entry.getKey() + "["
                                + field.getType() + "]");
                    }
                } else {
                    sane = false;
                    LOG.error("Unknown field type: " + obj.getClass().getCanonicalName());
                }
            }
        }
        if (!sane) {
            throw new RuntimeException("Dependency check failed against XSDs in: " + xsdPath);
        }
        
        for (NeutralSchema ns : schemas) {
            if (NeutralSchemaType.COMPLEX.equals(ns.getSchemaType())) {
                if (ns.getFields().size() == 1) {
                    outputStream.println("1 field schema: " + ns.getType());
                }
            }
        }
        
        if (output) {
            for (NeutralSchema ns : schemas) {
                if (ns.isSimple() && !ns.isPrimitive()) {
                    writeSchema(enumDir, ns);
                } else if (ns.isPrimitive()) {
                    writeSchema(primitiveDir, ns);
                } else {
                    writeSchema(complexDir, ns);
                }
            }
        }
    }
    
    private static void writeSchema(File dir, NeutralSchema schema) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(new File(dir, schema.getType() + ".json")));
            writer.write(new NeutralSchemaJSONStringWriter().transform(schema));
            writer.flush();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
