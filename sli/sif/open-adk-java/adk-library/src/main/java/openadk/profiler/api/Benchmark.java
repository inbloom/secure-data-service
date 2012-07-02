//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.profiler.api;

import java.util.Vector;

/**
 * 	Encapsulates a benchmark
 */
public class Benchmark
{
	/**
	 * 	The benchmark code. The first letter identifies the type of benchmark as either
	 * 	a synchronization/query benchmark ("Q") or an event reporting benchmark ("V"). The
	 * 	remaining letters identify the type of SIF Data Object (e.g. "SCH" for SchoolInfo,
	 * 	"STU" for StudentPersonal, etc.)  Benchmark codes are defined in the <i>SIF 
	 * 	Profiling Harness User's Guide</i>.
	 */
	protected String fCode;
	
	/**
	 * 	The associated SIF Data Object type code, or -1 if this is a summary benchmark. 
	 * 	Type codes are defined in the <i>SIF Profiling Harness User's Guide</i> (by 
	 * 	convention, a given type code is 1000 	greater than the object type code used in 
	 * 	the SIF Agent Library.)
	 */
	protected short fObjType;
	
	/**
	 * 	The metric times that comprise this benchmark.<p>
	 * 	@see #setBenchmarkTimes
	 * 	@see #getBenchmarkTimes
	 */
	protected Times fTimes;

	/**
	 * 	Constructor
	 * 	@param code The benchmark code (e.g. "QSTU")
	 * 	@param objType The object type code (e.g. 1000)
	 */
	public Benchmark( String code, short objType )
	{
		fCode = code;
		fObjType = objType;
		if( fObjType < 1000 )
			fObjType = (short)( objType + 1000 );
	}
	
	/**
	 * 	Gets the benchmark code
	 */
	public String getCode() {
		return fCode;
	}
	
	/**
	 * 	Gets the object type code
	 */
	public short getObjType() {
		return fObjType;
	}
	
	/**
	 * 	Gets the benchmark times
	 */
	public Times getBenchmarkTimes() {
		return fTimes;
	}
	
	/**
	 * 	Sets the benchmark times
	 */
	public void setBenchmarkTimes( Times times ) {
		fTimes = times;
	}
}
