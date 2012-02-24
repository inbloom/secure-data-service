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
        if (args.length != 2) {
            System.out.println("Usage: java EdFi");
        }

        String configFile = "/Users/yzhang/Documents/Course.config";
        String output = "/Users/yzhang/Documents/Course.xml";

        configFile = args[0];
        output = args[1];

        EdFiEntityXMLGenerator generator = new EdFiEntityXMLGenerator(configFile);
        generator.generateXML(output);
    }

    public EdFiEntityXMLGenerator(String configFile) {
        EntityConfigParser ecp = new EntityConfigParser();
        this.edfiEntity = ecp.parse(configFile);
    }

    public void generateXML(String filename) {
            try {
                xmlOut = new PrintWriter(new FileWriter(filename));
                getMains();
                xmlOut.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }

    private void getData() {
        Connection conn = Utility.getConnection();
        ResultSet mainResultSet = Utility.getResultSet(conn, this.edfiEntity.mainQuery);
        dataResultSets.put("main", mainResultSet);

        for (String embededElementName : this.edfiEntity.EmbeddedElements.keySet()) {
            EmbeddedElement ee = this.edfiEntity.EmbeddedElements.get(embededElementName);
            ResultSet embeddedResultSet = Utility.getResultSet(conn, ee.query);
            dataResultSets.put(ee.name, embeddedResultSet);
        }
    }

    private void getMain() {
        String mainXML = Utility.generateXMLbasedOnTemplate(this.dataResultSets.get("main"),
                this.edfiEntity.mainTemplate, this.edfiEntity.valuePlaceholders);

//        System.out.println("count: " + entityCount++);

        for (String embeddedName : this.edfiEntity.embeddedElementPlaceholders) {
//            System.out.println(embeddedName);
            String embeddedXML = Utility.generateEmbeddedXMLbasedOnTemplate(
                    this.dataResultSets.get("main"),
                    this.edfiEntity.EmbeddedElements.get(embeddedName).joinKeys,
                    this.dataResultSets.get(embeddedName),
                    this.edfiEntity.EmbeddedElements.get(embeddedName).template,
                    this.edfiEntity.EmbeddedElements.get(embeddedName).valuePlaceholders);
            mainXML = Utility.replace(mainXML, "\n==" + embeddedName + "==\n\n", embeddedXML);
        }

        xmlOut.print(mainXML);
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
