//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.cfg;

public class JUnitHelper {
	private static char correctSeparator = '/';
	private static char inCorrectSeparator = '\\';
	private static String prepend = "\\junit\\us\\";
	private static String otherPrepend = "./junit/us/";
	
	public static void setForWindows(String base) {
		correctSeparator = '\\';
		inCorrectSeparator = '/';
		
		prepend = base;
		otherPrepend = fixPath(new StringBuffer(base), correctSeparator, inCorrectSeparator);
	}
	
	public static void setForUnix(String base) {
		correctSeparator = '/';
		inCorrectSeparator = '\\';
		
		prepend = base;
		otherPrepend = fixPath(new StringBuffer(base), correctSeparator, inCorrectSeparator);
	}
	
	public static String fixPathString(String path) {
		StringBuffer fixpath = new StringBuffer ( path);
		if ((!path.contains(prepend)) && (!path.contains(otherPrepend)) )
			fixpath = new StringBuffer (prepend + path);
		
		if (fixpath.toString().contains(correctSeparator + "") || fixpath.toString().contains(inCorrectSeparator + "")) {		
			fixPath(fixpath, inCorrectSeparator, correctSeparator);
			if (fixpath.charAt(0) == '/')
				fixpath.insert(0, ".");
		}
		return fixpath.toString();
	 }
	
	public static String fixPathSeparator(String path) {
		StringBuffer fixpath = new StringBuffer ( path);
		return fixPath(fixpath, inCorrectSeparator, correctSeparator);
	}

	public static String getHomeDir() {
		String dir = System.getProperty("adk.home");
		if( dir != null )
			return dir;

		return System.getProperty("user.dir");
	}

	
	private static String fixPath(StringBuffer fixpath, char from, char to) {
		for (int i=0; i < fixpath.length(); ++i) {
			if (fixpath.charAt(i) == from)
				fixpath.setCharAt(i, to);
		}
		return fixpath.toString();
	}

	public static String getPrepend() {
		return prepend;
	}
}
