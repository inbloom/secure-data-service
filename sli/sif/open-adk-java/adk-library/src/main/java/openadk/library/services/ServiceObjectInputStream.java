//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.services;

import openadk.library.ADKException;
import openadk.library.SIFDataObject;
import openadk.library.SIFElement;

/**
*  This interface defines the methods to read a Services Input Stream.<p>
*
*  @author Andrew Elmhorst
*  @version ADK 2.3
*/
public interface ServiceObjectInputStream<T extends SIFElement> {
	/**
	 * Read the next ServiceObject from the stream
	 *  @return SIFElement
	 */
	public T read() throws ADKException;
	public SIFDataObject readSIFDataObject() throws ADKException;

	/**
	 * Determines if any Service Objects are currently available for reading
	 *  @return boolean
	 */
	public boolean available();
}
