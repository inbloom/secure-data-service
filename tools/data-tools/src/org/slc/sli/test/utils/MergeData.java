package org.slc.sli.test.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MergeData {
    private static Pattern endPattern = Pattern.compile("</Interchange");

    /**
     * @param args
     * @throws FileNotFoundException
     */
    public static void main(String[] args) {
        String root = "data";
        try {
            merge(root);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void merge(String root) throws IOException {
        File dir = new File(root);
        File[] subFolders = dir.listFiles();

        Map<String, List<File>> allDataFiles = new HashMap<String, List<File>>();

        for (File subFolder : subFolders) {
            if (subFolder.isFile())
                continue;

            File[] xmlFiles = subFolder.listFiles();
            for (File xmlFile : xmlFiles) {
                String filename = xmlFile.getName();
                if (!filename.endsWith(".xml")) continue;

                if (allDataFiles.containsKey(filename)) {
                    List<File> list = allDataFiles.get(filename);
                    list.add(xmlFile);
                } else {
                    List<File> list = new ArrayList<File>();
                    list.add(xmlFile);
                    allDataFiles.put(filename, list);
                }
            }
        }

        for (String filename : allDataFiles.keySet()) {
            List<File> xmlFiles = allDataFiles.get(filename);
            File newFile = new File(root + "/" + filename);
            PrintWriter output = new PrintWriter(newFile);

            for (int i = 0; i < xmlFiles.size(); i++) {
                File xmlFile = xmlFiles.get(i);

                BufferedReader input = new BufferedReader(new FileReader(xmlFile));
                try {
                    String line = null;
                    if (i == 0) {
                        output.println(input.readLine());
                        output.println(input.readLine());
                    } else {
                        input.readLine();
                        input.readLine();
                    }

                    while ((line = input.readLine()) != null) {
                        Matcher endMatcher = endPattern.matcher(line);
                        if (!endMatcher.find()) {
                            output.println(line);
                        } else {
                            if (i == xmlFiles.size() - 1) {
                                output.println(line);
                            }
                            output.flush();
                        }
                    }
                } finally {
                    input.close();
                }

            }
            output.close();
        }

    }
}
