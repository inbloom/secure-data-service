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


package org.slc.sli.test.exportTool;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityConfigParser {
    private Map<String, String> sectionPairs = new LinkedHashMap<String, String>();
    private EdFiEntity edfiEntity = new EdFiEntity();

    private Map<String, EmbeddedElement> allElements = new HashMap<String, EmbeddedElement>();

    private Pattern sectionPattern = Pattern.compile("\\s*#{3,}(\\w+)#{3,}\\s*");

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
        ecp.parse(configFilename);
    }

    public EdFiEntity parse(String filename) {
        this.getPartNamesAndStrings(filename);
        this.generateEdFiEntityConfig();
        if (this.validateConfig())
            return null;
        else
            return edfiEntity;
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
                if (line.trim().isEmpty())
                    continue;

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
        allElements.put("main", this.edfiEntity);

        this.edfiEntity.name = "main";

        for (String sectionName : this.sectionPairs.keySet()) {
            if (sectionName.equals("begin")) {
                this.edfiEntity.begin = this.sectionPairs.get(sectionName);
            } else if (sectionName.equals("end")) {
                this.edfiEntity.end = this.sectionPairs.get(sectionName);
            } else if (sectionName.equals("mainTemplate")) {
                this.edfiEntity.template = this.sectionPairs.get(sectionName);
                parseTemplate(this.edfiEntity.template, this.edfiEntity.valuePlaceholders,
                        this.edfiEntity.embeddedPlaceholders);
            } else if (sectionName.equals("mainQuery")) {
                this.edfiEntity.query = this.sectionPairs.get(sectionName);
                parseQuery(this.edfiEntity.query, this.edfiEntity.columnNames, this.edfiEntity.joinKeys);
            } else {
                Matcher templateMatcher = template.matcher(sectionName);
                if (templateMatcher.find()) {
                    String name = templateMatcher.group(1);

                    EmbeddedElement embedded;
                    if (!this.allElements.containsKey(name)) {
                        embedded = new EmbeddedElement();
                        embedded.name = name;
                        this.allElements.put(name, embedded);
                    } else {
                        embedded = this.allElements.get(name);
                    }

                    embedded.template = this.sectionPairs.get(sectionName);

                    parseTemplate(embedded.template, embedded.valuePlaceholders, embedded.embeddedPlaceholders);

                    EmbeddedElement parent = null;

                    for (EmbeddedElement temp : this.allElements.values()) {
                        if (temp.embeddedPlaceholders.contains(name))
                            parent = temp;
                    }

                    // System.out.println("template: " + name);

                    if (!parent.embeddedElementMap.containsKey(name)) {
                        parent.embeddedElementMap.put(name, embedded);
                    }

                } else {
                    Matcher queryMatcher = query.matcher(sectionName);
                    if (queryMatcher.find()) {
                        String name = queryMatcher.group(1);

                        EmbeddedElement embedded;
                        if (!this.allElements.containsKey(name)) {
                            embedded = new EmbeddedElement();
                            embedded.name = name;
                            this.allElements.put(name, embedded);
                        } else {
                            embedded = this.allElements.get(name);
                        }

                        embedded.query = this.sectionPairs.get(sectionName);

                        parseQuery(embedded.query, embedded.columnNames, embedded.joinKeys);

                        EmbeddedElement parent = null;

                        // System.out.println("query: " + name);

                        for (EmbeddedElement temp : this.allElements.values()) {
                            if (temp.embeddedPlaceholders.contains(name))
                                parent = temp;
                        }

                        if (!parent.embeddedElementMap.containsKey(name)) {
                            parent.embeddedElementMap.put(name, embedded);
                        }
                    }
                }
            }
        }

        return;
    }

    private void parseTemplate(String template, List<String> valueHolders, List<String> embeddedHolders) {
        Matcher valuePlaceholderMatcher = valuePlaceholderPattern.matcher(template);
        while (valuePlaceholderMatcher.find()) {
            String valuePlaceholder = valuePlaceholderMatcher.group(1);
            // System.out.println(valuePlaceholder);
            if (!valueHolders.contains(valuePlaceholder)) {
                valueHolders.add(valuePlaceholder);
            }
        }

        Matcher embeddedPlaceholderMatcher = embeddedPlaceholderPattern.matcher(template);
        while (embeddedPlaceholderMatcher.find()) {
            String embeddedPlaceholder = embeddedPlaceholderMatcher.group(1);
            // System.out.println(embeddedPlaceholder);
            if (!embeddedHolders.contains(embeddedPlaceholder)) {
                embeddedHolders.add(embeddedPlaceholder);
            }
        }
    }

    private void parseQuery(String query, List<String> columns, List<String> joinKeys) {
        String tempQuery = query.replaceAll("\\-{2,}(.*)", "");
        // System.out.println(tempQuery);

        tempQuery = tempQuery.replaceAll("\\n", " ");
        // System.out.println(tempQuery);

        Matcher selectLineMatcher = selectLinePattern.matcher(tempQuery);
        if (selectLineMatcher.find()) {
            String selectLine = selectLineMatcher.group(1);
            // System.out.println(selectLine);
            // System.out.println("=========================================");
            String[] selectedColumns = selectLine.split(",");
            for (String selectedColumn : selectedColumns) {
                String trimedSelectedColumn = selectedColumn.trim();

                Matcher columnMatcher = columnPattern.matcher(trimedSelectedColumn);
                if (columnMatcher.find()) {
                    String column = columnMatcher.group(1);
                    if (!columns.contains(column))
                        columns.add(column);
                    // System.out.println(column);
                }
            }
        }

        Matcher joinLineMatcher = joinLinePattern.matcher(query);
        while (joinLineMatcher.find()) {
            String orderBy = joinLineMatcher.group(1);
            String[] localJoinKeys = orderBy.split(",");

            // System.out.println("+++++++++++++++++++++++++++++");
            for (String localKey : localJoinKeys) {
                localKey = localKey.trim();
                Matcher keyMatcher = joinKeyPattern.matcher(localKey);
                if (keyMatcher.find()) {
                    String temp = keyMatcher.group(1);
                    // System.out.println(temp);
                    if (!joinKeys.contains(temp))
                        joinKeys.add(temp);
                }
            }
        }

    }

    private boolean validateConfig() {
        boolean foundError = false;
        for (EmbeddedElement element : this.allElements.values()) {
            // values vs. columns
            for (String valueName : element.valuePlaceholders) {
                if (!element.columnNames.contains(valueName)) {
                    foundError = true;
                    System.err.println("Section name: " + element.name + "\tMissing value: " + valueName);
                }
            }

            // embeddedPlaceHolders vs. embeddedElements
            for (String embeddedPlaceHolder : element.embeddedPlaceholders) {
                if (!element.embeddedElementMap.containsKey(embeddedPlaceHolder)) {
                    foundError = true;
                    System.err.println("Section name: " + element.name + "\tMissing embedded section: "
                            + embeddedPlaceHolder);
                }
            }

            // joinKeys
            for (EmbeddedElement embeddedElement : element.embeddedElementMap.values()) {

                if (embeddedElement.joinKeys.size() > element.joinKeys.size()) {
                    foundError = true;
                    System.err.println("Section " + embeddedElement.name + " has more join keys than parent section "
                            + element.name);
                } else {
                    for (int i = 0; i < embeddedElement.joinKeys.size(); i++) {
                        String keyInEmbedded = embeddedElement.joinKeys.get(i);
                        String keyInElement = element.joinKeys.get(i);

                        if (!keyInEmbedded.equals(keyInElement)) {
                            foundError = true;
                            System.err.println("Section name: " + element.name + "\tSubsection: "
                                    + embeddedElement.name + "\tMissing join key: " + keyInEmbedded);
                        }
                    }
                }
            }

            // JoinKeys in query
            for (String key : element.joinKeys) {
                if (!element.columnNames.contains(key)) {
                    foundError = true;
                    System.err.println("Section name:" + element.name + " join key " + key + " is not a part of the selected column");
                }
            }
        }
        return foundError;
    }
}
