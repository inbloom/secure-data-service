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


package org.slc.sli.test.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FilenameUtils;

public class DataUtils {
    static final int BUFFER = 1024*4;
    public static final String ZIP_FILE_NAME = "output.zip";

    /*
     * Takes *.xml or *.ctl files at path and zips them to an output.zip
     *
     * @path path where *.xml input files are and where output.zip will be written
     */
    public static final boolean zipIngestionData(String path) {

        final class OnlyIngestionFiles implements FilenameFilter {
            public boolean accept(File dir, String name) {
                return (name.endsWith(".xml") || name.endsWith(".ctl"));
            }
        }

        File pathDir = new File(path);
        File output = new File(pathDir, ZIP_FILE_NAME);
        if (output.exists()) {
            output.delete();
        }

        try {
            File zipFile = new File(pathDir, ZIP_FILE_NAME);
            FileOutputStream dest = null;
            try {
                dest = new FileOutputStream(zipFile);
                ZipOutputStream out = null;
                try {
                    out = new ZipOutputStream(new BufferedOutputStream(
                            dest));
                    // out.setMethod(ZipOutputStream.DEFLATED);
                    byte data[] = new byte[BUFFER];
                    // get a list of files from the path
                    String files[] = pathDir.list(new OnlyIngestionFiles());
        
                    BufferedInputStream origin = null;
                    for (String basename : files) {
                        File filename = new File(pathDir, basename);
                        System.out.println("Adding: " + filename.getPath());
                        FileInputStream fi = null;
                        try {
                            fi = new FileInputStream(filename);
                            try {
                                origin = new BufferedInputStream(fi, BUFFER);
                                ZipEntry entry = new ZipEntry(basename);
                                out.putNextEntry(entry);
                                int count;
                                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                                    out.write(data, 0, count);
                                }
                            } finally {
                                if (null != origin) {
                                    origin.close();
                                }
                            }
                        } finally {
                            if (null != fi) {
                                fi.close();
                            }
                        }
                    }
                } finally {
                    if (null != out) {
                        out.close();
                    }
                }
                return true;
            } finally {
                if (null != dest) {
                    dest.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static final String createMd5ForFile(String file) {
        File myFile = new File(file);
        DigestInputStream dis = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            dis = new DigestInputStream(new FileInputStream(myFile), md);
            byte[] buf = new byte[1024];
            while (dis.read(buf, 0, 1024) != -1) {
            }
            return Hex.encodeHexString(dis.getMessageDigest().digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * Determine the interchange of a file
     *
     * @fileName input file
     */
    public static final InterchangeType determineInterchange(String fileName) {
        String lowerCaseBaseName = FilenameUtils.getBaseName(fileName).toLowerCase();

        // TODO this should parse the xml header to determine the interchange

        if (lowerCaseBaseName.startsWith("interchangeeducationorganization")) {
            return InterchangeType.EDUCATION_ORGANIZATION;
        } else if (lowerCaseBaseName.startsWith("interchangeeducationorgcalendar")) {
            return InterchangeType.EDUCATION_ORG_CALENDAR;
        } else if (lowerCaseBaseName.startsWith("interchangemasterschedule")) {
            return InterchangeType.MASTER_SCHEDULE;
        } else if (lowerCaseBaseName.startsWith("interchangestaffassociation")) {
            return InterchangeType.STAFF_ASSOCIATION;
        } else if (lowerCaseBaseName.startsWith("interchangestudentenrollment")) {
            return InterchangeType.STUDENT_ENROLLMENT;
        } else if (lowerCaseBaseName.startsWith("interchangestudentgrade")) {
            return InterchangeType.STUDENT_GRADES;
        } else if (lowerCaseBaseName.startsWith("interchangeassessmentmetadata")) {
            return InterchangeType.ASSESSMENT_METADATA;
        } else if (lowerCaseBaseName.startsWith("interchangestudentassessment")) {
            return InterchangeType.STUDENT_ASSESSMENT;
        } else if (lowerCaseBaseName.startsWith("interchangestudentattendance")) {
            return InterchangeType.STUDENT_ATTENDANCE;
        } else if (lowerCaseBaseName.startsWith("interchangestudentparent")) {
            return InterchangeType.STUDENT_PARENT_ASSOCIATION;
        } else if (lowerCaseBaseName.startsWith("interchangestudent")) {  // must be after other interchanges it's name is a subset of
            return InterchangeType.STUDENT;
        }

        return null;
    }

    public static final boolean writeControlFile(String fileToWrite, String interchange, String filename) {
        boolean success = false;
//        System.out.println("--- Starting to write control file for " + filename + " --- ");
        try {
            String md5HashWord = DataUtils.createMd5ForFile(filename);
            File controlFile = new File(fileToWrite);
            File interchangeFile = new File(filename);
            if (controlFile.exists()) {
                String writeBack = "";
                boolean foundMatchingLine = false;
                BufferedReader read = null;
                try {
                    read = new BufferedReader(new FileReader(controlFile));
                    for (String line = read.readLine(); line != null; line = read.readLine()) {
                        Pattern p = Pattern.compile("edfi.xml[,]" + interchange + "[,]" + interchangeFile.getName()
                                + "[,]\\w+");
                        Matcher m = p.matcher(line);
                        if (m.find()) {
                            foundMatchingLine = true;
                            writeBack += m.replaceAll("edfi-xml," + interchange + "," + interchangeFile.getName() + ","
                                    + md5HashWord)
                                    + "\n";
                        } else {
                            writeBack += line + "\n";
                        }
                    }
                } finally {
                    if (null != read)
                        read.close();
                }
                BufferedWriter writer = null;
                try {
                    writer = new BufferedWriter(new FileWriter(fileToWrite));
                    writer.write(writeBack);
                    if (!foundMatchingLine) {
                        writer.write("edfi-xml," + interchange + "," + interchangeFile.getName() + "," + md5HashWord);
                        success = true;
                    }
                } finally {
                    if (null != writer)
                        writer.close();
                }
                success = true;
            } else {
                BufferedWriter writer = null;
                try {
                    writer = new BufferedWriter(new FileWriter(fileToWrite));
                    writer.write("edfi-xml," + interchange + "," + interchangeFile.getName() + "," + md5HashWord);
                } finally {
                    if (null != writer)
                        writer.close();
                }
                success = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("--- Wrote control file entry for " + filename + " --- ");
        return success;
    }

}
