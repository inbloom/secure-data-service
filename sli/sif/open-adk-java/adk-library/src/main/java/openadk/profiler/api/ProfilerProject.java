//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.profiler.api;

/**
 */
public class ProfilerProject
{
	protected int fID = -1;
	protected String fName;
	protected String fTitle;
	
	public ProfilerProject( int id )
	{
		fID = id;
	}

	public ProfilerProject( String name, String title )
	{
		fName = name;
		fTitle = title;
	}
	
	public String getName() 
	{
		return fName;
	}
	
	public void setName( String n )
	{
		fName = n;
	}
	
	public String getTitle()
	{
		return fTitle;
	}
	
	public void setTitle( String str )
	{
		fTitle = str;
	}
	
	public void setID( int id )
	{
		fID = id;
	}
	
	public String toString() 
	{
		return fName;
	}
}
