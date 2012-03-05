package org.slc.sli.test.exportTool;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class EdFiEntityXMLGenerator {
    private Map<String, ResultSet> dataResultSets = new HashMap<String, ResultSet>();
    private EdFiEntity edfiEntity;
    private PrintWriter xmlOut;

    /**
     * @param args
     */
    public static void main(String[] args) {
<<<<<<< HEAD
    	String configFile= "/Users/lchen/workspace/sli/tools/data-tools/entity-configurations/test.config";
       // String configFile = "/Users/yzhang/Work/git1/sli/tools/data-tools/entity-configurations/teacher.config";
        String output = "/Users/lchen/workspace/sli/tools/data-tools/entity-configurations/test.xml";
/*
=======
        String configFile = "/Users/yzhang/Work/git1/sli/tools/data-tools/entity-configurations/test.config";
        String output = "/Users/yzhang/Documents/test.xml";

>>>>>>> bfc838356b954309a9fe96b229701a8ba35408c1
        if (args.length != 2) {
            System.out
                    .println("Usage:\njava -classpath .:../lib/jtds-1.2.5.jar org.slc.sli.test.exportTool.EdFiEntityXMLGenerator ../entity-configurations/Course.config course.xml");
            return;
        }
*/
        configFile = args[0];
        output = args[1];
        System.out.println("configfile :" + configFile);
        System.out.println("configfile :" + output);
        
        EdFiEntityXMLGenerator generator = new EdFiEntityXMLGenerator(configFile);
        generator.generateXML(output);
    }

    public EdFiEntityXMLGenerator(String configFile) {
        EntityConfigParser ecp = new EntityConfigParser();
        this.edfiEntity = ecp.parse(configFile);
    }

    public void generateXML(String filename) {
        if (this.edfiEntity == null) {
            System.err.println("\nConfig file contains errors, could not generate xml file!");
            return;
        }

        try {
            xmlOut = new PrintWriter(new FileWriter(filename));
            getMains();
            xmlOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getData() {
        Connection conn = Utility.getConnection();
        ResultSet mainResultSet = Utility.getResultSet(conn, this.edfiEntity.query);
        dataResultSets.put("main", mainResultSet);

        for (String embededElementName : this.edfiEntity.embeddedElementMap.keySet()) {
            EmbeddedElement ee = this.edfiEntity.embeddedElementMap.get(embededElementName);
            ResultSet embeddedResultSet = Utility.getResultSet(conn, ee.query);
            dataResultSets.put(ee.name, embeddedResultSet);
        }
    }

    private void getMain() {
        String mainXML = Utility.generateXMLbasedOnTemplate(this.dataResultSets.get("main"),
                this.edfiEntity.template, this.edfiEntity.valuePlaceholders);

        for (String embeddedName : this.edfiEntity.embeddedPlaceholders) {

            ResultSet parentResultSet = this.dataResultSets.get("main");
            EmbeddedElement embeddedElement = this.edfiEntity.embeddedElementMap.get(embeddedName);

            String embeddedXML = this.getEmbeddedXML(parentResultSet, embeddedElement);

            mainXML = Utility.replace(mainXML, "==" + embeddedName + "==\\s*\n", embeddedXML);
        }

        xmlOut.print(mainXML);
    }

    private String getEmbeddedXML(ResultSet parentResultSet, EmbeddedElement element) {
        String xml = "";

        if (element.embeddedPlaceholders.size() > 0) {
            xml = Utility.generateEmbeddedXMLbasedOnTemplate(parentResultSet, element.joinKeys,
                    this.dataResultSets.get(element.name), element.template, element.valuePlaceholders);

            ResultSet resultSet = this.dataResultSets.get(element.name);

            for (String embeddedName : element.embeddedPlaceholders) {
                EmbeddedElement embeddedElement = element.embeddedElementMap.get(embeddedName);

                String embeddedXML = this.getEmbeddedXML(resultSet, embeddedElement);

                xml = Utility.replace(xml, "==" + embeddedName + "==\n", embeddedXML);
            }

        } else {
            xml = Utility.generateEmbeddedXMLbasedOnTemplate(parentResultSet, element.joinKeys,
                    this.dataResultSets.get(element.name), element.template, element.valuePlaceholders);
        }

        return xml;
    }

    private void getMains() {
        this.getData();

        xmlOut.print(this.edfiEntity.begin);

        try {
            ResultSet mainResultSet = this.dataResultSets.get("main");
            if (mainResultSet != null) {
                do {
                    this.getMain();
                } while (mainResultSet.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        xmlOut.print(this.edfiEntity.end);
    }
}
