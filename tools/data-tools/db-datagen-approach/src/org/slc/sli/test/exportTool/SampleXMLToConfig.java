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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SampleXMLToConfig {
    Pattern placeHolderPattern1 = Pattern.compile("<(\\w*)>(.*)<");
    Pattern placeHolderPattern2 = Pattern.compile("(\\w*)=\"([\\w|\\s]*)\"");
    List<String> ignoredTag = Arrays.asList(new String[] { "id", "ref" });

    Pattern rootPattern = Pattern.compile("<(\\w*) xsi:schemaLocation");

    List<String> content = new ArrayList<String>();
    List<Integer> contentCount = new ArrayList<Integer>();

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out
                    .println("Usage:\njava -classpath . org.slc.sli.test.exportTool.SampleXMLToConfig ../entity-configurations/course.sample course.config");
            return;
        }

        String input = args[0];
        String output = args[1];

        SampleXMLToConfig sxtt = new SampleXMLToConfig();
        sxtt.replacePlaceHolder(input, output);
    }

    public void replacePlaceHolder(String input, String output) {
        try {
            FileInputStream fstream = new FileInputStream(input);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            PrintWriter templateOut = new PrintWriter(new FileWriter(output));

            String line = null;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                Matcher lineMatcher = placeHolderPattern1.matcher(line);
                while (lineMatcher.find()) {
                    line = line.replace(">" + lineMatcher.group(2) + "<", ">--" + lineMatcher.group(1) + "--<");
                }

                lineMatcher = placeHolderPattern2.matcher(line);
                while (lineMatcher.find()) {
                    if (!ignoredTag.contains(lineMatcher.group(1))) {
                        line = line.replace("\"" + lineMatcher.group(2) + "\"", "\"--" + lineMatcher.group(1) + "--\"");
                    }
                }

                content.add(line);
            }

            int beginIndex = 0;
            templateOut.println("##############begin###############");
            for (String tempLine : content) {
                Matcher lineMatcher = rootPattern.matcher(tempLine);
                templateOut.println(tempLine);
                beginIndex++;
                if (lineMatcher.find()) break;
            }

            templateOut.println("\n############end############");
            templateOut.println(content.get(content.size() - 1));

            templateOut.println("\n##########mainTemplate#########");
            for (int i = beginIndex; i < content.size() - 1; i++) {
                templateOut.println(content.get(i));
            }

            templateOut.println("\n##########mainQuery##############\n");

            br.close();
            templateOut.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
