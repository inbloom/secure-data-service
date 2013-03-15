
import java.io.*;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.*;
import java.util.zip.*;

//
// A simple tool to split the files within an EdFi XML ZIP file
//
public class ZipSplitter {
	
	private static Charset charset = Charset.forName("UTF-8");
	
	private static ArrayList<String> controlFileLines = new ArrayList<String>();

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		//
		// Parse parameters
		//
		if ((args.length < 2) || (args.length > 3)) {
            System.out.println("Usage: ZipSplitter INPUT_FILE OUTPUT_FILE [#ENTITIES]");
			System.exit(1);
		}
		String inputFileName = args[0];
		String outputFileName = args[1];
		int entityLimitPerFile = args.length > 2 ? Integer.parseInt(args[2]) : 15000;
		//
		// Make sure the file exists
		//
		File file = new File(inputFileName);
		if (!file.isFile()) {
			System.out.println("Can't find file: " + inputFileName);
			System.exit(2);
		}
		System.out.println("Splitting " + inputFileName + " (" + file.length() + " bytes), " + entityLimitPerFile + " max per file");
		try {
			//
			// Open the input ZIP
			//
			ZipFile inputZipFile = new ZipFile(inputFileName);
			//
			// Open the output ZIP
			//
			File outputFile = new File (outputFileName);
			ZipOutputStream outStream = new ZipOutputStream(new FileOutputStream(outputFile));
			//
			// Read the control file
			//
			ZipEntry controlFileEntry = inputZipFile.getEntry("ControlFile.ctl");
			InputStream in = inputZipFile.getInputStream(controlFileEntry);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));	
			while (true) {
				String line = reader.readLine();
				if (line == null) break;
				splitXmlFile(inputZipFile, line, outStream, entityLimitPerFile);
			}
			//
			// Process any other files
			//
			for (Enumeration e = inputZipFile.entries(); e.hasMoreElements(); ) {
				ZipEntry inputEntry = (ZipEntry)e.nextElement();
				InputStream inputStream = inputZipFile.getInputStream(inputEntry);
				if (inputEntry.getName().endsWith(".xml")) {
					//
					// Discard XML files, we already handled them
					//
				} else if (inputEntry.getName().equals("ControlFile.ctl")) {
					//
					// Discard control file (we will write a new one at the end)
					//
				} else {
					//
					// Other file, copy it
					//
					System.out.println("Copying " + inputEntry.getName() + ", " + inputEntry.getSize() + " bytes");
					copyFile(inputEntry, inputStream, outStream);
				}
			}
			//
			// Write new control file
			//
			ZipEntry outputEntry = new ZipEntry("ControlFile.ctl");
			outStream.putNextEntry(outputEntry);
			for (String s : controlFileLines) {
				s += "\n";
				byte[] bytes = s.getBytes(charset);
				outStream.write(bytes);
			}
			outStream.closeEntry();
			//
			// Done
			//
			outStream.close();
			long elapsed = System.currentTimeMillis() - startTime;
			long bytes = file.length();
			if (elapsed < 1000) {
				System.out.println("Processed " + bytes + " bytes in " +
						elapsed + " msec - " + (bytes/elapsed) + " Kbytes/second");
			} else {
				System.out.println("Processed " + bytes + " bytes in " +
						elapsed/1000 + " seconds - " + (bytes/elapsed) + " Kbytes/second");
			}
		} catch (Exception ex) {
			System.out.println("Error: - " + ex.getMessage());
		}
	}
	
	//
	// Copy a file
	//
	private static void copyFile(ZipEntry inputEntry, InputStream inputStream,
								 ZipOutputStream outStream) throws IOException {
		ZipEntry e = new ZipEntry(inputEntry.getName());
		outStream.putNextEntry(e);
		byte buf[] = new byte[256*256];
		while (true) {
			int num = inputStream.read(buf);
			if (num == -1) break;
			outStream.write(buf, 0, num);
		}
		outStream.closeEntry();
	}
	
    private static final Pattern TAG_PATTERN = Pattern.compile("^\\s*<(\\w*)>\\s*$");
    private static final Pattern INTERCHANGE_TAG_PATTERN = Pattern.compile("^\\s*<(Interchange\\w*) .*$");
    
	//
	// Split an XML file in a ZIP
	//
	private static void splitXmlFile(ZipFile inputZipFile, String line,
									 ZipOutputStream outStream, int entityLimitPerFile) throws Exception {
		// edfi-xml,AssessmentMetadata,InterchangeAssessmentMetadata.xml,d055b888d7bf6ae4c615a01109bcd64a
		//
		// Parse the control file line
		//
		String[] controlFileLineParts = line.split(",");
		String dataType = controlFileLineParts[0];
		String interchangeName = controlFileLineParts[1];
		String inputFileName = controlFileLineParts[2];
		String fileNameBase = inputFileName.substring(0, inputFileName.length()-4);
		//
		// Open the input file
		//
		ZipEntry inputEntry = inputZipFile.getEntry(inputFileName);
		InputStream in = inputZipFile.getInputStream(inputEntry);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));	
		String fileHeader = "";
		byte[] fileFooterBytes = null;
		boolean readingFileHeader = true;
		//
		// Parse the input file
		//
		ZipEntry outputEntry = null;
		MessageDigest md5 = null;
		int totalEntities = 0;
		int entitiesThisFile = 0;
		int fileNumber = 0;
		Pattern endTagPattern = null;
		String s = null;
		String outputFileName = null;
		do {
			s = reader.readLine();
			if (s != null) {
				boolean includeFileHeader = false;
				//
				// If we don't have an output file open, open one
				//
				if (outputEntry == null) {
					fileNumber++;
					outputFileName = fileNumber == 1
							? fileNameBase + ".xml"
							: fileNameBase + "_" + fileNumber + ".xml";
					outputEntry = new ZipEntry(outputFileName);
					outStream.putNextEntry(outputEntry);
			        md5 = MessageDigest.getInstance("MD5");
			        includeFileHeader = !readingFileHeader;
				}
				if (endTagPattern == null) {
					//
					// Look for the start of a tag
					//
					Matcher matcher = TAG_PATTERN.matcher(s);
					if (matcher.matches()) {
						//
						// Found a start tag
						//
						String tagName = matcher.group(1);
						endTagPattern = Pattern.compile("^\\s*</" + tagName + ">\\s*$");
						readingFileHeader = false;
					} else {
						//
						// No start tag, just output this line
						//
						if (readingFileHeader) {
							fileHeader = fileHeader + s + "\n";
							matcher = INTERCHANGE_TAG_PATTERN.matcher(s);
							if (matcher.matches()) {
								String fileFooter = "</" + matcher.group(1) + ">\n";
								fileFooterBytes = fileFooter.getBytes(charset);
							}
						}
					}
				} else {
					//
					// Looking for the end of a tag
					//
					Matcher matcher = endTagPattern.matcher(s);
					if (matcher.matches()) {
						//
						// Found the end tag
						//
						endTagPattern = null;
						totalEntities++;
						entitiesThisFile++;
					} else {
						//
						// No end tag, just output this line
						//
					}
				}
				//
				// Output the line
				//
				if (includeFileHeader) {
					s = fileHeader + s;
				}
				s += "\n";
				byte[] bytes = s.getBytes(charset);
				md5.update(bytes);
				outStream.write(bytes);
			}
			//
			// Close this file if we have reached the entity limit
			// or the end of the file
			//
			if ((s == null) || (entitiesThisFile == entityLimitPerFile)) {
				if (s != null) {
					//
					// Add footer
					//
					md5.update(fileFooterBytes);
					outStream.write(fileFooterBytes);
				}
				outStream.closeEntry();
				byte[] digestBytes = md5.digest();
				StringBuilder sb = new StringBuilder();
				for(byte b : digestBytes) {
					sb.append(String.format("%02x", b&0xff));
				}
				String cfl = dataType + "," + interchangeName + "," +
						     outputFileName + "," + sb.toString();
				controlFileLines.add(cfl);
				outputEntry = null;
				entitiesThisFile = 0;
			}
		} while (s != null);
		System.out.println("Finished " + inputFileName + ": wrote " + fileNumber + " files, " + totalEntities + " total entities");
		if (endTagPattern != null) System.out.println("ERROR -- left with a pattern");
	}

}
