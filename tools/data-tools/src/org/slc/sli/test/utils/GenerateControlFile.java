package org.slc.sli.test.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

public class GenerateControlFile {

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) {
		String path = null;
		
		if (args.length > 0) {
			path = args[0];
		} else {
			path = "data";
		}
		
		File dir = new File(path);
		String ctrlFileName = path + "/MainControlFile.ctl";

		// TODO parse a properties file for attributes
		// TODO write attributes to the control file

    	final class OnlyXml implements FilenameFilter {
    		public boolean accept(File dir, String name) {
    			return (name.endsWith(".xml"));
    		}
    	}

		String[] xmlFiles = dir.list(new OnlyXml());

		for (String filename : xmlFiles) {
	        System.out.println("--- Considering file " + filename + " --- ");
			if (filename.endsWith(".xml")) {
				String interchange = DataUtils.determineInterchange(filename);
		        System.out.println("--- Using interchange \"" + interchange + "\" for " + filename + " --- ");
				DataUtils.writeControlFile(ctrlFileName, interchange, path + "/" + filename);
			}
		}
	}

}