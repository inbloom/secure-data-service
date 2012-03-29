package org.slc.sli.test.mappingGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MergeData {
    private Pattern begin1 = Pattern.compile("<?xml");
    private Pattern begin2 = Pattern.compile("<Interchange");
    private static Pattern endPattern = Pattern.compile("</Interchange");
    
    /**
     * @param args
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        String root = "data";
        File dir = new File(root);
        File[] subFolders = dir.listFiles();
        
        Map<String, List<File>> allDataFiles = new HashMap<String, List<File>>();
        
        for (File subFolder : subFolders) {
            if (subFolder.isFile())
                continue;
            
            File[] xmlFiles = subFolder.listFiles();
            for (File xmlFile : xmlFiles) {
                String filename = xmlFile.getName();
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
            
            for (int i = 0; i < xmlFiles.size(); i++) {
                File xmlFile = xmlFiles.get(i);
                if (!xmlFile.getName().endsWith(".xml")) continue;
                
                StringBuilder header = new StringBuilder();
                StringBuilder content = new StringBuilder();
                StringBuilder end = new StringBuilder();

                BufferedReader input = new BufferedReader(new FileReader(xmlFile));
                try {
                    try {
                        String line = null;
                        header.append(input.readLine()).append("\n");
                        header.append(input.readLine());
                        
                        while ((line = input.readLine()) != null) {
                            Matcher endMatcher = endPattern.matcher(line);
                            if (!endMatcher.find()) {
                                content.append(line).append("\n");
                            } else {
                                end.append(line);
                                break;
        }
                        }    
                    } finally {
                        input.close();
                    }                  
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                System.out.println(xmlFile.getName());
//                System.out.println("header");
//                System.out.println(header.toString());
//                System.out.println("end");
//                System.out.println(end.toString());
                System.out.println(content.toString());
                
            }
        }
        return;
    }
    
}
