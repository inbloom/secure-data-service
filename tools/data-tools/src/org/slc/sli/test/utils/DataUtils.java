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
<<<<<<< HEAD
    
    /*
     * Takes *.xml or *.ctl files at path and zips them to an output.zip
     * 
=======

    /*
     * Takes *.xml or *.ctl files at path and zips them to an output.zip
     *
>>>>>>> master
     * @path path where *.xml input files are and where output.zip will be written
     */
	public static final void zipIngestionData(String path) {

		final class OnlyIngestionFiles implements FilenameFilter {
			public boolean accept(File dir, String name) {
				return (name.endsWith(".xml") || name.endsWith(".ctl"));
			}
		}

		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(path + "\\output.zip");
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					dest));
			// out.setMethod(ZipOutputStream.DEFLATED);
			byte data[] = new byte[BUFFER];
			// get a list of files from the path
			File f = new File(path);
			String files[] = f.list(new OnlyIngestionFiles());

			for (String basename : files) {
				String filename = path + "\\" + basename;
				System.out.println("Adding: " + filename);
				FileInputStream fi = new FileInputStream(filename);
				origin = new BufferedInputStream(fi, BUFFER);
				ZipEntry entry = new ZipEntry(basename);
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
<<<<<<< HEAD
    
    /*
     * Determine the interchange of a file
     * 
=======

    /*
     * Determine the interchange of a file
     *
>>>>>>> master
     * @fileName input file
     */
    public static final InterchangeType determineInterchange(String fileName) {
    	String lowerCaseBaseName = FilenameUtils.getBaseName(fileName).toLowerCase();
<<<<<<< HEAD
    	
    	// TODO this should parse the xml header to determine the interchange
    	
=======

    	// TODO this should parse the xml header to determine the interchange

>>>>>>> master
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
    	} else if (lowerCaseBaseName.startsWith("interchangeattendance")) {
    		return InterchangeType.STUDENT_ATTENDANCE;
    	} else if (lowerCaseBaseName.startsWith("interchangestudentparent")) {
    		return InterchangeType.STUDENT_PARENT_ASSOCIATION;
    	} else if (lowerCaseBaseName.startsWith("interchangestudent")) {  // must be after other interchanges it's name is a subset of
    		return InterchangeType.STUDENT;
    	}
<<<<<<< HEAD
    	
    	return null;
    }
    
=======

    	return null;
    }

>>>>>>> master
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
                BufferedReader read = new BufferedReader(new FileReader(controlFile));
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
                read.close();
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileToWrite));
                writer.write(writeBack);
                if (!foundMatchingLine) {
                    writer.write("edfi-xml," + interchange + "," + interchangeFile.getName() + "," + md5HashWord);
                    writer.close();
                    success = true;
                }
                writer.close();
                success = true;
            } else {
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileToWrite));
                writer.write("edfi-xml," + interchange + "," + interchangeFile.getName() + "," + md5HashWord);
                writer.close();
                success = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
<<<<<<< HEAD
        System.out.println("--- Wrote control file entry for " + filename + " --- ");
=======
        //System.out.println("--- Wrote control file entry for " + filename + " --- ");
>>>>>>> master
        return success;
    }

}
