//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

/**
 * 	Defines static build options that determine if certain features such as support for
 * 	the Edustructures SIF Profiling Harness are enabled. The SIFWorks ADK Ant script will
 * 	rewrite this source file at build time. During development, change the values in your 
 * 	local copy.<p>
 */
public class BuildOptions
{
	/** 
	 * 	Enable support for the Edustructures SIF Profiling Harness. This requires that
	 * 	the SIFProfilerLib.jar file be on the classpath.
	 */
	public static final boolean PROFILED = false;
}
