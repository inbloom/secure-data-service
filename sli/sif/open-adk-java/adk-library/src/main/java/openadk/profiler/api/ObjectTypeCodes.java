//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.profiler.api;

import openadk.library.ElementDef;


/**
 * 	Implemented by agents to convert an ElementDef to an object type code defined by the
 * 	Edustructures SIF Profiling Harness.<p>
 * 
 * 	The SIFAgentLib contains a class that implements this interface and registers it with 
 * 	the SIFProfilerClient of the ADK. Any time the ADK needs to determine an object type 
 * 	code for an ElementDef constant, it calls the methods of the SIFAgentLib-supplied 
 * 	implementation.<p>
 */
public interface ObjectTypeCodes
{
	/**
	 * 	Return an object type code for an ElementDef instance.
	 * 	@return A code defined in the SIF Profiling Harness specification for the specified
	 * 		SIF Data Object type.
	 */
	public short getObjectTypeCode( ElementDef def );
}
