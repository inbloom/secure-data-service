//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.services;

import java.lang.reflect.Method;

import openadk.library.ADKException;
import openadk.library.SIFErrorCategory;
import openadk.library.SIFErrorCodes;
import openadk.library.SIFException;
import openadk.library.Zone;

/**
 * Utilities used for SIF Services
 * 
 * @author Andrew Elmhorst
 * @version ADK 2.3
 */
class ServiceUtils {

	/**
	 * Returns the method on the specified proxy class that should be called. If
	 * the method is not defined, an exception is thrown
	 * 
	 * @param obj
	 * @param serviceElement
	 * @param zone
	 * @return
	 * @throws ADKException
	 */
	public static Method getMethod(Object proxy, String methodName, Zone zone)
			throws ADKException {

		// Look for the first method matching the name
		// TODO: Will SIF Services support overloads? Probably not.
		Class cls = proxy.getClass();
		Method[] methods = cls.getDeclaredMethods();
		for (int a = 0; a < methods.length; a++) {
			if (methods[a].getName().equalsIgnoreCase(methodName)) {
				return methods[a];
			}
		}

		// TODO: Fix up error handling when SIF Services error codes are defined
		throw new SIFException(SIFErrorCategory.EVENTS,
				SIFErrorCodes.EVENT_GENERIC_ERROR_1, "Operation " + methodName
						+ " is not supported.", zone);

	}

}
