//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.services.impl;

import openadk.library.ADKException;
import openadk.library.DataObjectInputStream;
import openadk.library.MessageInfo;
import openadk.library.SIFDataObject;
import openadk.library.SIFElement;
import openadk.library.SIFException;
import openadk.library.Zone;
import openadk.library.infra.SIF_Error;
import openadk.library.services.ServiceObjectInputStream;


/**
*  Implements {@link openadk.library.services.ServiceObjectInputStream}.
*  Used in {@link openadk.library.services.SIFZoneServiceProxy}
*  to wrap Query Results returned.<p>
*
*  @author Andrew Elmhorst
*  @version ADK 2.3
*/
public class ServiceObjectInputStreamImpl<T extends SIFElement> implements
		ServiceObjectInputStream<T> {

	private final SIF_Error fError;
	private final DataObjectInputStream fInputStream;
	private final Zone fZone;

	/**
	 *  Constructs a new ServiceObjectInputStream
	 *  @return A new ServiceObjectInputStream object which 
	 *  wraps a SIF Error
	 */
	public ServiceObjectInputStreamImpl(Zone zone, MessageInfo info,
			SIF_Error error) {
		fError = error;
		fInputStream = null;
		fZone = zone;
	}

	/**
	 *  Constructs a new ServiceObjectInputStream
	 *  @return A new ServiceObjectInputStream object which 
	 *  wraps a DataObjectInputStream
	 */
	public ServiceObjectInputStreamImpl(Zone zone, MessageInfo info,
			DataObjectInputStream in) {
		fError = null;
		fInputStream = in;
		fZone = zone;
	}

	public boolean available() {
		return fError != null
				|| (fInputStream != null && fInputStream.available());
	}

	public T read() throws ADKException {
		if (fError != null) {
			throw new SIFException(fError.getSIF_Category(), fError
					.getSIF_Code(), fError.getSIF_Desc(), fError
					.getSIF_ExtendedDesc(), fZone);
		} else {
			SIFDataObject sdo = fInputStream.readDataObject();
			return (T) sdo.getChildList().get(0);
		}
	}
	
	public SIFDataObject readSIFDataObject() throws ADKException {
		if (fError != null) {
			throw new SIFException(fError.getSIF_Category(), fError
					.getSIF_Code(), fError.getSIF_Desc(), fError
					.getSIF_ExtendedDesc(), fZone);
		} else {
			return fInputStream.readDataObject();
		}
	}

}
