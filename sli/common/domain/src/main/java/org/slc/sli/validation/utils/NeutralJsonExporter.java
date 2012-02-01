package org.slc.sli.validation.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.slc.sli.validation.NeutralSchemaFactory;
import org.slc.sli.validation.schema.NeutralSchema;

/**
 * Utility class for exporting Neutral Schema files to flat files.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
public class NeutralJsonExporter {
    
    /**
     * Loads the XSD files and dumps the Neutral Schema objects to JSON files in the specified
     * directory.
     * 
     * Param 1: xsdDirectory (defaults to "classpath:xliXsd")
     * Param 2: outputDir (defaults to "neutral-schemas")
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String xsdPath = "classpath:sliXsd-wip";
        String outputDir = "neutral-schemas";
        if (args.length == 2) {
            xsdPath = args[0];
            outputDir = args[1];
        }
        
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
                new String[] { "spring/neutral-json-exporter-config.xml" });
        
        XsdToNeutralSchemaRepo repo = new XsdToNeutralSchemaRepo(xsdPath, new NeutralSchemaFactory());
        repo.setApplicationContext(appContext);
        
        File enumDir = new File(outputDir, "enums");
        File primitiveDir = new File(outputDir, "primitive");
        File complexDir = new File(outputDir, "complexDir");
        
        enumDir.mkdirs();
        primitiveDir.mkdirs();
        complexDir.mkdirs();
        
        List<NeutralSchema> schemas = repo.getSchemas();
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
    
    private static void writeSchema(File dir, NeutralSchema schema) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(new File(dir, schema.getType() + ".json")));
            writer.write(schema.toJson());
            writer.flush();
        } finally {
            writer.close();
        }
    }
}
