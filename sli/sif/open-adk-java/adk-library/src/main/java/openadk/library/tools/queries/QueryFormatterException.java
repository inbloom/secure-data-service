//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.queries;

import openadk.library.Element;
import openadk.library.SIFErrorCategory;
import openadk.library.SIFErrorCodes;
import openadk.library.SIFException;

/**
 *  Signals an unrecoverable processing error by a QueryFormatter
 *
 *  @author Edustructures LLC
 *  @version ADK 1.0
 */
public class QueryFormatterException extends SIFException
{
    /**
	 * 
	 */
	private static final long serialVersionUID = Element.CURRENT_SERIALIZE_VERSION;

	/**
	 * Creates a new SIF Error that indicates the Query is not supported by the
	 * responding agent.
	 * 
	 * @param msg detail about the error message
	 */
	public QueryFormatterException( String msg )
	{
		super( SIFErrorCategory.REQUEST_RESPONSE, SIFErrorCodes.REQRSP_UNSUPPORTED_QUERY_9,  msg,  null );
    }
}
