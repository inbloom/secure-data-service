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

import java.io.File;
import java.io.FilenameFilter;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ControlFile {

	private static final String CONTROL_FILE_SUFFIX = ".ctl";

	Properties attributes = new Properties();

	private Map<InterchangeType, List<String>> interchangeListMap = new EnumMap<InterchangeType, List<String>>(
			InterchangeType.class);

	public ControlFile() {
		InterchangeType allInterchangeTypes[] = InterchangeType.values();

		for (InterchangeType itype : allInterchangeTypes) {
			interchangeListMap.put(itype, new LinkedList<String>());
		}
	}

	public ControlFile(String path) {
		this();

		if (path == null) {
			path = "./data/";
		}

		File dir = new File(path);

		// TODO parse a properties file for attributes
		// TODO write attributes to the control file

		final class OnlyXml implements FilenameFilter {
			public boolean accept(File dir, String name) {
				return (name.endsWith(".xml"));
			}
		}

		String[] xmlFiles = dir.list(new OnlyXml());

		// order the interchanges

		for (String filename : xmlFiles) {
			System.out.println("--- Considering file " + filename + " --- ");
			if (filename.endsWith(".xml")) {
				InterchangeType interchange = DataUtils
						.determineInterchange(filename);
				System.out.println("--- Using interchange \""
						+ interchange.getName() + "\" for " + filename
						+ " --- ");
				interchangeListMap.get(interchange).add(path + filename);
			}
		}

	}

	public void save(String filename) {
		String ctrlFileName;

		if (filename == null || filename.isEmpty()) {
			ctrlFileName = "MainControlFile" + CONTROL_FILE_SUFFIX;
		} else if (filename.endsWith(".ctl")) {
			ctrlFileName = filename;
		} else {
			ctrlFileName = filename + CONTROL_FILE_SUFFIX;
		}

		System.out.println("Creating control file " + ctrlFileName);

		File cfile = new File(ctrlFileName);
		if (cfile.exists()) {
			cfile.delete();
		}

		// TODO write out attributes

		InterchangeType allInterchangeTypes[] = InterchangeType.values();

		for (InterchangeType itype : allInterchangeTypes) {
			for (String xmlfile : interchangeListMap.get(itype)) {
				System.out.println("Writing " + xmlfile + " to " + ctrlFileName);
				DataUtils.writeControlFile(ctrlFileName, itype.getName(), xmlfile);
			}
		}
	}

	public void setAttributes(Properties props) {
		attributes = props;
	}

	public Properties getAttributes(Properties props) {
		return attributes;
	}

	public static void main(String[] args) {
		String path = null;

		if (args.length > 0) {
			path = args[0];
		} else {
			path = "./data/";
		}

		ControlFile cfile = new ControlFile(path);

		cfile.save(path + "GeneratedControlFile");
	}
}
