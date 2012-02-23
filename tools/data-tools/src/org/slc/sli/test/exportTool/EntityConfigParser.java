package org.slc.sli.test.exportTool;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityConfigParser {
    private Map<String, String> sectionPairs = new HashMap<String, String>();
    private EdFiEntity edfiEntity = new EdFiEntity();
    /**
     * @param args
     */
    public static void main(String[] args) {
        String configFilename = "/Users/yzhang/Documents/Course.config";
        EntityConfigParser ecp = new EntityConfigParser();
        ecp.getPartNamesAndStrings(configFilename);
        ecp.generateEdFiEntityConfig();
        ecp.generatePlaceholders();
    }

    public void getPartNamesAndStrings(String filename) {
        Pattern sectionPattern = Pattern.compile("###(\\w*)###");

        try {
            FileInputStream fstream = new FileInputStream(filename);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line;
            String lineName = null;
            StringBuilder lineBuilder = new StringBuilder();

            boolean foundFirst = false;

            while ((line = br.readLine()) != null) {
                Matcher sectionMatcher = sectionPattern.matcher(line);
                boolean result = sectionMatcher.matches();
                if (result) {
                    if (foundFirst) {
                        System.out.println(lineName);
                        System.out.println(lineBuilder.toString());
                        this.sectionPairs.put(lineName, lineBuilder.toString());
                        lineBuilder = new StringBuilder();
                    }
                    if (!foundFirst) {
                        foundFirst = true;
                    }
                    lineName = sectionMatcher.group(1);
                } else {
                    lineBuilder.append(line).append("\n");
                }
            }

            System.out.println(lineName);
            System.out.println(lineBuilder.toString());
            this.sectionPairs.put(lineName, lineBuilder.toString());

            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void generateEdFiEntityConfig() {
        Pattern template = Pattern.compile("(.*)Template");
        Pattern query = Pattern.compile("(.*)Query");

        for (String sectionName : this.sectionPairs.keySet()) {
            if (sectionName.equals("begin")) {
                this.edfiEntity.begin = this.sectionPairs.get(sectionName);
            } else if (sectionName.equals("end")) {
                this.edfiEntity.end = this.sectionPairs.get(sectionName);
            } else if (sectionName.equals("mainTemplate")) {
                this.edfiEntity.mainTemplate = this.sectionPairs.get(sectionName);
            } else if (sectionName.equals("mainQuery")) {
                this.edfiEntity.mainQuery = this.sectionPairs.get(sectionName);
            } else {
                Matcher templateMatcher = template.matcher(sectionName);
                if (templateMatcher.find()) {
                    String name = templateMatcher.group(1);
                    if (this.edfiEntity.EmbeddedElements.containsKey(name)) {
                        this.edfiEntity.EmbeddedElements.get(name).template = this.sectionPairs.get(sectionName);
                    } else {
                        EmbeddedElement embedded = new EmbeddedElement();
                        embedded.name = name;
                        embedded.template = this.sectionPairs.get(sectionName);
                        this.edfiEntity.EmbeddedElements.put(name, embedded);
                    }
                } else {
                    Matcher queryMatcher = query.matcher(sectionName);
                    if (queryMatcher.find()) {
                        String name = queryMatcher.group(1);
                        if (this.edfiEntity.EmbeddedElements.containsKey(name)) {
                            this.edfiEntity.EmbeddedElements.get(name).query = this.sectionPairs.get(sectionName);
                        } else {
                            EmbeddedElement embedded = new EmbeddedElement();
                            embedded.name = name;
                            embedded.query = this.sectionPairs.get(sectionName);
                            this.edfiEntity.EmbeddedElements.put(name, embedded);
                        }
                    }
                }
            }
        }

        return;
    }

    public void generatePlaceholders() {
        Pattern valuePlaceholderPattern = Pattern.compile("--(\\w*)--");
        Matcher valuePlaceholderMatcher = valuePlaceholderPattern.matcher(this.edfiEntity.mainTemplate);
        while (valuePlaceholderMatcher.find()) {
            String valuePlaceholder = valuePlaceholderMatcher.group(1);
            System.out.println(valuePlaceholder);
            if(!this.edfiEntity.valuePlaceholders.contains(valuePlaceholder)) {
                this.edfiEntity.valuePlaceholders.add(valuePlaceholder);
            }
        }

        Pattern embeddedPlaceholderPattern = Pattern.compile("==(\\w*)==");
        Matcher embeddedPlaceholderMatcher = embeddedPlaceholderPattern.matcher(this.edfiEntity.mainTemplate);
        while (embeddedPlaceholderMatcher.find()) {
            String embeddedPlaceholder = embeddedPlaceholderMatcher.group(1);
            System.out.println(embeddedPlaceholder);
            if (!this.edfiEntity.embeddedElementPlaceholders.contains(embeddedPlaceholder)) {
                this.edfiEntity.embeddedElementPlaceholders.add(embeddedPlaceholder);
            }
        }

        Pattern joinKeyPattern = Pattern.compile("ORDER BY (.*)");
        Matcher joinKeyMatcher = joinKeyPattern.matcher(this.edfiEntity.mainQuery);
        while (joinKeyMatcher.find()) {
            String orderBy = joinKeyMatcher.group(1);
            System.out.println("*************************");
            System.out.println(orderBy);
        }
        return;
    }
}
