package org.slc.sli.test.exportTool;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityConfigParser {
    private Map<String, String> sectionPairs = new HashMap<String, String>();
    private EdFiEntity edfiEntity = new EdFiEntity();
    
    private Pattern sectionPattern = Pattern.compile("#{3,}(\\w+)#{3,}");

    private Pattern template = Pattern.compile("(.*)Template");
    private Pattern query = Pattern.compile("(.*)Query");
    
    private Pattern valuePlaceholderPattern = Pattern.compile("--(\\w+)--");
    private Pattern embeddedPlaceholderPattern = Pattern.compile("==(\\w+)==");
    
    private Pattern selectLinePattern = Pattern.compile("SELECT (.*) FROM", Pattern.CASE_INSENSITIVE);
    private Pattern columnPattern = Pattern.compile("(\\w+)$");
    
    private Pattern joinLinePattern = Pattern.compile("ORDER BY (.*)", Pattern.CASE_INSENSITIVE);
    private Pattern joinKeyPattern = Pattern.compile("(\\w+)$");

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
        parseTemplate(this.edfiEntity.mainTemplate, this.edfiEntity.valuePlaceholders, this.edfiEntity.embeddedElementPlaceholders);

        parseQuery(this.edfiEntity.mainQuery, this.edfiEntity.columnNames, this.edfiEntity.joinKeys);
        
        for (String elementName : this.edfiEntity.EmbeddedElements.keySet()) {
            EmbeddedElement element = this.edfiEntity.EmbeddedElements.get(elementName);
            parseTemplate(element.template, element.valuePlaceholders, element.embeddedPlaceholders);
            parseQuery(element.query, element.columnNames, element.joinKeys);
        }
        return;
    }
    
    private void parseTemplate(String template, List<String> valueHolders, List<String> embeddedHolders) {
        Matcher valuePlaceholderMatcher = valuePlaceholderPattern.matcher(template);
        while (valuePlaceholderMatcher.find()) {
            String valuePlaceholder = valuePlaceholderMatcher.group(1);
            System.out.println(valuePlaceholder);
            if(!valueHolders.contains(valuePlaceholder)) {
                valueHolders.add(valuePlaceholder);
            }
        }

        Matcher embeddedPlaceholderMatcher = embeddedPlaceholderPattern.matcher(template);
        while (embeddedPlaceholderMatcher.find()) {
            String embeddedPlaceholder = embeddedPlaceholderMatcher.group(1);
            System.out.println(embeddedPlaceholder);
            if (!embeddedHolders.contains(embeddedPlaceholder)) {
                embeddedHolders.add(embeddedPlaceholder);
            }
        }
    }
    
    private void parseQuery(String query, List<String> columns, List<String> joinKeys) {
        String tempQuery = query.replaceAll("\\-{2,}(.*)", "");
        System.out.println(tempQuery);
        
        tempQuery = tempQuery.replaceAll("\\n", " ");
        System.out.println(tempQuery);
        
        Matcher selectLineMatcher = selectLinePattern.matcher(tempQuery);
        if (selectLineMatcher.find()) {
            String selectLine = selectLineMatcher.group(1);
            System.out.println(selectLine);
            System.out.println("=========================================");
            String[] selectedColumns = selectLine.split(",");
            for (String selectedColumn : selectedColumns) {
                String trimedSelectedColumn = selectedColumn.trim();
                
                Matcher columnMatcher = columnPattern.matcher(trimedSelectedColumn);
                if (columnMatcher.find()) {
                    String column = columnMatcher.group(1);
                    if (!columns.contains(column))
                        columns.add(column);
                    System.out.println(column);
                }
            }
        }
        
        
        Matcher joinLineMatcher = joinLinePattern.matcher(query);
        while (joinLineMatcher.find()) {
            String orderBy = joinLineMatcher.group(1);
            String[] localJoinKeys = orderBy.split(",");
            
            System.out.println("+++++++++++++++++++++++++++++");
            for (String localKey : localJoinKeys) {
                localKey = localKey.trim();
                Matcher keyMatcher = joinKeyPattern.matcher(localKey);
                if (keyMatcher.find()) {
                    String temp = keyMatcher.group(1);             
                    System.out.println(temp);
                    if(!joinKeys.contains(temp))
                        joinKeys.add(temp);
                }
            }
        }
        
    }
}
