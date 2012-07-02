//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import openadk.library.infra.*;

/**
 *  SIFException describes a SIF error condition.
 *
 *  When handling inbound messages, the agent may throw a SIFException from a
 * 	message handler to signal that an error has occurred and should be returned 
 * 	to the ZIS in the SIF_Ack message. The ADK will convert the SIFException to 
 * 	a SIF_Error element when sending the associated SIF_Ack. It is desirable to 
 * 	throw SIFException in your message handlers (versus generic exceptions) if 
 * 	you want control over setting the SIF_Error category, code, description, and 
 * 	extended description elements.<p>
 *  
 *  SIFException may also be thrown by ADK methods in response to a SIF_Ack 
 * 	received from the server. The actual SIF_Ack object that generated the exception
 * 	can be retrieved by calling <code>getAck</code>. Any SIF_Error elements 
 * 	included in the acknowledgement can be retrieved by calling <code>getErrors</code> 
 * 	and associated methods such as <code>hasError</code>. Note SIF 1.0r1 allowed 
 * 	for multiple SIF_Error elements per SIF_Ack, but later versions of SIF do 
 * 	not. For backward compatibility, the ADK captures all SIF_Error elements 
 * 	received in SIF_Ack messages and makes them available as an array. The array
 *  can be obtained by calling the <code>getErrors</code> method.<p>
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class SIFException extends ADKMessagingException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = Element.CURRENT_SERIALIZE_VERSION;

	/**
	 *  The SIF_Ack that caused this exception (if the exception was raised in
	 *  response to an incoming message)
	 */
	public SIF_Ack fAck;

	/**
	 *  Optional SIF_Errors wrapped by this exception
	 */
	protected SIF_Error fError;


	/**
	 *  Constructs an exception to wrap one or more SIF_Errors received from an
	 *  inbound SIF_Ack message. This form of constructor is only called by
	 *  the ADK.
	 */
	/**
	 * @param ack The SIF_Ack to use
	 * @param zone The zone that this error applies to
	 */
	public SIFException( SIF_Ack ack, Zone zone )
	{
		super(null,zone);

		fAck = ack;
		fError = ack != null ? ack.getSIF_Error() : null;
	}

	/**
	 *  Constructs an exception to wrap one or more SIF_Errors received from an
	 *  inbound SIF_Ack message. This form of constructor is only called by
	 *  the ADK.
	 */
	/**
	 * @param msg The error message
	 * @param ack a SIF_Ack element to use
	 * @param zone The zone that this error applies to
	 */
	public SIFException( String msg, SIF_Ack ack, Zone zone )
	{
		super(msg,zone);

		fAck = ack;
		fError = ack != null ? ack.getSIF_Error() : null;
	}

	/**
	 *  Constructs a SIFException for delivery to the ZIS. The ADK will include
	 *  the error information provided by the exception when it sends a SIF_Ack
	 *  in response to the message being processed. This form of constructor is
	 *  typically called by the ADK, but may also be called by agent code if an
	 *  exception occurs in a <i>Publisher</i>, <i>Subscriber</i>, or <i>QueryResults</i>
	 * 	message handler implementation.
	 * 
	 * @deprecated Please use the overload that uses SIFErrorCategory as the first parameter
	 * 
	 * 	@param category A <code>SIFErrorCodes.CAT_</code> error category
	 * 	@param code A <code>SIFErrorCodes</code> error code
	 * 	@param desc The error description
	 * 	@param zone The zone on which the error occurred
	 */
	public SIFException( int category, int code, String desc, Zone zone )
	{
		this( category, code, desc, null, zone );
	}

	/**
	 *  Constructs a SIFException for delivery to the ZIS. The ADK will include
	 *  the error information provided by the exception when it sends a SIF_Ack
	 *  in response to the message being processed. This form of constructor is
	 *  typically called by the ADK, but may also be called by agent code if an
	 *  exception occurs in a <i>Publisher</i>, <i>Subscriber</i>, or <i>QueryResults</i>
	 * 	message handler implementation.
	 * 
	 * @deprecated Please use the overload that uses SIFErrorCategory as the first parameter 
	 * 	@param category A <code>SIFErrorCodes.CAT_</code> error category
	 * 	@param code A <code>SIFErrorCodes</code> error code
	 * 	@param desc The error description
	 * 	@param extDesc An option extended error description
	 * 	@param zone The zone on which the error occurred
	 */
	public SIFException( int category, int code, String desc, String extDesc, Zone zone )
	{
		super( desc, zone );
		fAck = null;
        SIF_Error error =  new SIF_Error( category, code,  desc == null ? "" : desc );
        if( extDesc != null ){
        	error.setSIF_ExtendedDesc( extDesc );
        }

		fError = error;
	}
    

    /**
     * Constructs a SifException for delivery to the ZIS<p>
     * 
     *  The Adk will include
     *  the error information provided by the exception when it sends a SIF_Ack
     *  in response to the message being processed. This form of constructor is
     *  typically called by the Adk, but may also be called by agent code if an
     *  exception occurs in a <c>IPublisher</c>, <c>ISubscriber</c>, or <c>IQueryResults</c>
     *  message handler implementation.
     * @deprecated Please use the overload that uses SIFErrorCategory as the first parameter
     * 
     * @param category A <c>SifErrorCategoryCode.</c> error category
     * @param code A <c>SifErrorCodes</c> error code
     * @param desc The error description
     * @param extDesc An optional extended error description
     * @param zone The zone on which the error occurred
     * @param innerException The internal error that was thrown by the agent
     */
    public SIFException( int category, int code, String desc, String extDesc, Zone zone, Exception innerException ) 
    {
        super( desc, zone, innerException );
        fAck = null;
        SIF_Error error =  new SIF_Error( category, code,  desc == null ? "" : desc );
        if( extDesc != null ){
        	error.setSIF_ExtendedDesc( extDesc );
        }
        fError = error;
    }
    
    
    /**
     * Constructs a SifException for delivery to the ZIS<p>
     * 
     *  The Adk will include
     *  the error information provided by the exception when it sends a SIF_Ack
     *  in response to the message being processed. This form of constructor is
     *  typically called by the Adk, but may also be called by agent code if an
     *  exception occurs in a <c>IPublisher</c>, <c>ISubscriber</c>, or <c>IQueryResults</c>
     *  message handler implementation.
     * @deprecated Please use the overload that uses SIFErrorCategory as the first parameter
     * @param category A <c>SifErrorCategoryCode.</c> error category
     * @param code A <c>SifErrorCodes</c> error code
     * @param desc The error description
     * @param zone The zone on which the error occurred
     * @param innerException The internal error that was thrown by the agent
     */
    public SIFException( int category, int code, String desc,  Zone zone, Exception innerException ) 
    {
        this( category, code, desc, null, zone, innerException );
    }
    

	/**
	 *  Constructs a SIFException for delivery to the ZIS. The ADK will include
	 *  the error information provided by the exception when it sends a SIF_Ack
	 *  in response to the message being processed. This form of constructor is
	 *  typically called by the ADK, but may also be called by agent code if an
	 *  exception occurs in a <i>Publisher</i>, <i>Subscriber</i>, or <i>QueryResults</i>
	 * 	message handler implementation.
	 * 
	 * 	@param category The category that applies to this type of error
	 * 	@param code A <code>SIFErrorCodes</code> error code
	 * 	@param desc The error description
	 * 	@param zone The zone on which the error occurred
	 */
	public SIFException( SIFErrorCategory category, int code, String desc, Zone zone )
	{
		this( category, code, desc, null, zone );
	}
    
	/**
	 *  Constructs a SIFException for delivery to the ZIS. The ADK will include
	 *  the error information provided by the exception when it sends a SIF_Ack
	 *  in response to the message being processed. This form of constructor is
	 *  typically called by the ADK, but may also be called by agent code if an
	 *  exception occurs in a <i>Publisher</i>, <i>Subscriber</i>, or <i>QueryResults</i>
	 * 	message handler implementation.
	 * 
	 * 	@param category The category that applies to this type of error
	 * 	@param code A <code>SIFErrorCodes</code> error code
	 * 	@param desc The error description
	 * 	@param extDesc An option extended error description
	 * 	@param zone The zone on which the error occurred
	 */
	public SIFException( SIFErrorCategory category, int code, String desc, String extDesc, Zone zone )
	{
		super( desc, zone );
		fAck = null;
        SIF_Error error =  new SIF_Error( category, code,  desc == null ? "" : desc, extDesc );
		fError = error;
	}
    
    
    /**
     * Constructs a SifException for delivery to the ZIS<p>
     * 
     *  The Adk will include
     *  the error information provided by the exception when it sends a SIF_Ack
     *  in response to the message being processed. This form of constructor is
     *  typically called by the Adk, but may also be called by agent code if an
     *  exception occurs in a <c>Publisher</c>, <c>Subscriber</c>, or <c>QueryResults</c>
     *  message handler implementation.
     * 
     * @param category The category that applies to this type of error
     * @param code A <c>SifErrorCodes</c> error code
     * @param desc The error description
     * @param zone The zone on which the error occurred
     * @param innerException The internal error that was thrown by the agent
     */
    public SIFException( SIFErrorCategory category, int code, String desc, Zone zone, Exception innerException ) 
    {
        this( category, code, desc, null, zone, innerException );
    }
    
    /**
     * Constructs a SifException for delivery to the ZIS<p>
     * 
     *  The Adk will include
     *  the error information provided by the exception when it sends a SIF_Ack
     *  in response to the message being processed. This form of constructor is
     *  typically called by the Adk, but may also be called by agent code if an
     *  exception occurs in a <c>IPublisher</c>, <c>ISubscriber</c>, or <c>IQueryResults</c>
     *  message handler implementation.
     * 
     * @param category The category that applies to this type of error
     * @param code A <c>SifErrorCodes</c> error code
     * @param desc The error description
     * @param extDesc An optional extended error description
     * @param zone The zone on which the error occurred
     * @param innerException The internal error that was thrown by the agent
     */
    public SIFException( SIFErrorCategory category, int code, String desc, String extDesc, Zone zone, Exception innerException ) 
    {
        super( desc, zone, innerException );
        fAck = null;
        if( extDesc == null && innerException != null ){
        	extDesc = innerException.getMessage();
        }
        SIF_Error error =  new SIF_Error( category, code,  desc == null ? "" : desc, extDesc );
        fError = error;
    }


	/**
	 *  Gets all SIF_Errors wrapped by this exception
	 *  @return an array of SIF_Error elements
	 */
	public SIF_Error getError()
	{
		return fError;
	}

	/**
	 *  Determines if this SIFException describes any SIF_Errors
	 *  @return true if the exception wraps at least on SIF_Error
	 */
	public boolean hasErrors()
	{
		return fError != null;
	}

	/**
	 *  Determines if this SIFException has an error with the specified category
	 *  and code. In some versions of SIF, a SIFException may describe more than
	 *  one error. This method searches through all of the wrapped errors and
	 * 	returns <code>true</code> if any match the category and code.<p>
	 *
	 *  @deprecated Please use the overload of this method that takes a SIFErrorCategory 
	 *  		as the first parameter
	 *
	 *  @param category The SIF error category to search for
	 *  @param code The SIF error code to search for
	 * 	
	 * 	@return <code>true</code> if any errors wrapped by this exception match
	 * 		the category and code
	 */
	public boolean hasError( int category, int code )
	{
		if( fError != null )
		{
			if( fError.getSIF_Category() == category &&
					fError.getSIF_Code() == code )
			{
				return true;
			}
		}

		return false;
	}
	
	/**
	 *  Determines if this SIFException has an error with the specified category
	 *  and code. In some versions of SIF, a SIFException may describe more than
	 *  one error. This method searches through all of the wrapped errors and
	 * 	returns <code>true</code> if any match the category and code.<p>
	 *
	 *  @param category The SIF error category to search for
	 *  @param code The SIF error code to search for
	 * 	
	 * 	@return <code>true</code> if any errors wrapped by this exception match
	 * 		the category and code
	 */
	public boolean hasError( SIFErrorCategory category, int code )
	{
		if( fError != null )
		{
			if( SIFErrorCategory.lookup( fError.getSIF_Category() ) == category &&
					fError.getSIF_Code() == code )
			{
				return true;
			}
		}

		return false;
	}

	/**
	 *  Determines if this SIFException has an error with the specified category.
	 *  In some versions of SIF, a SIFException may describe more than
	 *  one error. This method searches through all of the wrapped errors and
	 * 	returns <code>true</code> if any match the category.<p>
	 * 
	 *  @deprecated please use the overload that takes a SIFErrorCategory
	 *
	 *  @param category The SIF error category to search for
	 * 	
	 * 	@return <code>true</code> if any errors wrapped by this exception match
	 * 		the category
	 */
	public boolean hasErrorCategory( int category )
	{
		if( fError != null )
		{
			return fError.getSIF_Category() == category;
		}

		return false;
	}
	
	/**
	 *  Determines if this SIFException has an error with the specified category.
	 *  In some versions of SIF, a SIFException may describe more than
	 *  one error. This method searches through all of the wrapped errors and
	 * 	returns <code>true</code> if any match the category.<p>
	 *
	 *  @param category The SIF error category to search for
	 * 	
	 * 	@return <code>true</code> if any errors wrapped by this exception match
	 * 		the category
	 */
	public boolean hasErrorCategory( SIFErrorCategory category )
	{
		return category == getSIFErrorCategory();
	}

	
	/**
	 * Sets the SIF_Error element associated with this exception.
	 * @param category A <code>SIFErrorCodes.CAT_</code> error category
	 * @param code A <code>SIFErrorCodes_</code> error code
	 * @param desc
	 * @param extDesc
	 */
	public void setSIF_Error( int category, int code, String desc, String extDesc )
	{
		fError = new SIF_Error( category, code, desc, extDesc );
	}
	
	/**
	 * Sets the SIF_Error element associated with this exception.
	 * @param category The category associated with this error
	 * @param code A <code>SIFErrorCodes_</code> error code
	 * @param desc
	 * @param extDesc
	 */
	public void setSIF_Error( SIFErrorCategory category, int code, String desc, String extDesc )
	{
		fError = new SIF_Error( category, code, desc, extDesc );
	}


	private void _checkErrorExists()
	{
		if( fError == null ){
			fError = new SIF_Error();
		}
	}
			
	/**
	 * 	Sets the error category code of the first SIF_Error wrapped by this
	 * 	exception. If no SIF_Errors are wrapped by this exception, a new one 
	 * 	is created.<p>
	 * 
	 * 	@param category A <code>SIFErrorCodes.CAT_</code> error category
	 */
	public void setErrorCategory( int category )
	{
		_checkErrorExists();
		fError.setSIF_Category( category );
	}
	
	/**
	 * 	Sets the error category code of the first SIF_Error wrapped by this
	 * 	exception. If no SIF_Errors are wrapped by this exception, a new one 
	 * 	is created.<p>
	 * 
	 * 	@param category The category to set to this exception
	 */
	public void setErrorCategory( SIFErrorCategory category )
	{
		_checkErrorExists();
		fError.setSIF_Category( category.getValue() );
	}
	
	/**
	 * 	Sets the error code of the first SIF_Error wrapped by this exception. 
	 * 	If no SIF_Errors are wrapped by this exception, a new one is created.<p>
	 * 
	 * 	@param code A <code>SIFErrorCodes</code> error code
	 */
	public void setErrorCode( int code )
	{
		_checkErrorExists();
		fError.setSIF_Code( code );
	}
	
	/**
	 * 	Sets the error description of the first SIF_Error wrapped by this 
	 * 	exception. If no SIF_Errors are wrapped by this exception, a new one 
	 * 	is created.<p>
	 * 
	 * 	@param desc The error description
	 */
	public void setErrorDesc( String desc )
	{
		_checkErrorExists();
		fError.setSIF_Desc( desc );
	}
	
	/**
	 * 	Sets the optional extended error description of the first SIF_Error 
	 * 	wrapped by this exception. If no SIF_Errors are wrapped by this exception, 
	 * 	a new one is created.<p>
	 * 
	 * 	@param extDesc The extended error description
	 */
	public void setErrorExtDesc( String extDesc )
	{
		_checkErrorExists();
		
		fError.setSIF_ExtendedDesc( extDesc );
	}

	/**
	 *  Gets the Error Category of the first SIF_Error element, or 0xFFFFFFFF
	 *  if there are no errors.
   * @deprecated Please use getSIFErrorCategory	
	 * @return The Category associated with this error
	 */
	public int getErrorCategory()
	{
		if( fError != null  ){
			return fError.getSIF_Category();
		}
		return 0xFFFFFFFF;
	}
	
	/**
	 *  Gets the Error Category of the first SIF_Error element, or 0xFFFFFFFF
	 *  if there are no errors.
	 * @return The Category associated with this error
	 */
	public SIFErrorCategory getSIFErrorCategory()
	{
		if( fError != null  ){
			return SIFErrorCategory.lookup( fError.getSIF_Category() );
		}
		return SIFErrorCategory.UNKNOWN;
	}

	/**
	 *  Gets the Error Code of the first SIF_Error element, or 0xFFFFFFFF
	 *  if there are no errors. 
	 * @return The Code associated with this error
	 */
	public int getErrorCode()
	{
		if( fError != null ){
			return fError.getSIF_Code();
		}

		return 0xFFFFFFFF;
	}

	/**
	 *  Gets the Error Description of the first SIF_Error element, or null
	 *  if there are no errors. 
	 * @return The description of the error
	 */
	public String getErrorDesc()
	{
		if( fError != null ){
			return fError.getSIF_Desc();
		}

		return null;
	}

	/**
	 *  Gets the Extended Error Description of the first SIF_Error element, or
	 *  null if there are no errors. 
	 * @return The extended error description
	 */
	public String getErrorExtDesc()
	{
		if( fError != null  ){
			return fError.getSIF_ExtendedDesc();
		}
		return null;
	}


	/**
	 * The SIF_Ack that is part of this exception
	 * @return The SIF_Ack that is part of this exception
	 */
	public SIF_Ack getAck()
	{
		return fAck;
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage()
	{
		StringBuffer buf = new StringBuffer();

		String msg = super.getMessage();
		if( msg != null )
		{
			//  Only include super.getMessage() text if there is no SIF_Error or
			//  if there is a SIF_Error and its SIF_Desc text is different. This
			//  makes for much less annoying error output.
			if( fError == null || ( fError.getSIF_Desc() != null && !fError.getSIF_Desc().equals(msg) ) )
			{
				buf.append(msg);
	    		buf.append(": ");
			}
		}

		if( fError != null )
		{
			buf.append("[Category=");
			buf.append(fError.getSIF_Category());
			buf.append("; Code=");
			buf.append(fError.getSIF_Code());
			buf.append("] ");
			String desc = fError.getSIF_Desc();
			if( desc != null )
				buf.append( desc );
			desc = fError.getSIF_ExtendedDesc();
			if( desc != null ) {
				buf.append(". ");
				buf.append( desc );
			}
		}
		return buf.toString();
	}
}
