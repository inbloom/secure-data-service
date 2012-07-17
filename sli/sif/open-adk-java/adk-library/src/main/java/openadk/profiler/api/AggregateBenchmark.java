//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.profiler.api;

import java.util.Vector;

/**
 */
public class AggregateBenchmark extends Benchmark
{
	/**
	 * 	For summary benchmarks, the aggregate Times used to calculate the summary
	 * 	benchmark; not used for individual benchmarks.<p>
	 * 	@see #addAggregateTimes
	 * 	@see #getAggregateTimes
	 */
	protected Vector fAggTimes;
	
	/**
	 * 	Constructor
	 * 	@param code The benchmark code (e.g. "SYNC")
	 */
	public AggregateBenchmark( String code )
	{
		super( code, (short)-1 );
		
		fTimes = new Times();
	}
	
	/**
	 * 	Adds a Times to the set of aggregate times for this summary benchmark
	 */
	public void addAggregateTimes( Times times ) 
	{
		if( fAggTimes == null )
			fAggTimes = new Vector();
		fAggTimes.add( times );
		
		fTimes.combineWith( times );
	}
	
	/**
	 * 	Gets the aggregate benchmark times
	 */
	public Times[] getAggregateTimes() 
	{
		Times[] arr = new Times[ fAggTimes == null ? 0 : fAggTimes.size() ];
		if( fAggTimes != null )
			fAggTimes.copyInto( arr );
		
		return arr;
	}
}
