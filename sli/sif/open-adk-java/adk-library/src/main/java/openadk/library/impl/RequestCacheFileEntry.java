//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

import openadk.library.Agent;
import openadk.library.RequestInfo;


public class RequestCacheFileEntry implements RequestInfo, Serializable
{
	static final long serialVersionUID = -4896286788438360315L;
	private String fObjType;
	private String fMessageId;
	private Date fRequestTime;
	private Serializable fState;
    
    
    private void writeObject(ObjectOutputStream stream)
    throws IOException
    {
        // 1 Write the fObjType variable
        stream.writeUTF( fObjType );
        
        // 2 Write the fMessageId variable;
        stream.writeUTF( fMessageId );
        
        // 3 Write the fRequestTime variable
        stream.writeLong( fRequestTime.getTime() );
        
        // 4 Write the user state variable
        stream.writeObject( fState );
    }
    
    private void readObject(ObjectInputStream stream)
    throws IOException, ClassNotFoundException
    {

        // 1 read the fObjType variable
        fObjType = stream.readUTF();
        
        // 2 read the fMessageId variable;
        fMessageId = stream.readUTF();
        
        // 3 read the fRequestTime variable
        fRequestTime = new Date( stream.readLong() );
        
        try
        {
            // 4 read the user state variable
            fState = (Serializable)stream.readObject();
        }
        catch( Exception ex )
        {
            Agent.getLog().warn( "Error Deserializing UserState : " + ex, ex );
        }
       
    }
    
    

	/**
	 * The offset that this entry is stored in the file. This value is not serialized
	 * because it is written to the stream seperately
	 */
	private transient long fOffset;

	/**
	 * Whether or not this item represents an active request. This value is not serialized
	 * because it is written to the stream seperately
	 */
	private transient boolean fIsActive = true;

	/**
	 * Creates a RequestInfo entry
	 * @param active
	 */
	RequestCacheFileEntry( boolean active )
	{
		fIsActive = active;
	}


	/**
	 * @return The Object Type of the Request. e.g. "StudentPersonal"
	 */
	public String getObjectType()
	{
		return fObjType;
	}

	/**
	 * Sets the object type
	 * @param objType
	 */
	void setObjectType( String objType )
	{
		fObjType = objType;
	}

	
	/**
	 * @return The SIF_Request MessageId
	 */
	public String getMessageId()
	{
		return fMessageId;
	}

	/**
	 * Sets the message Id
	 * @param messageId
	 */
	void setMessageId( String messageId )
	{
		fMessageId = messageId;
	}

	/**
	 * The Date and Time that that this request was initially made
	 * @return
	 */
	public Date getRequestTime()
	{
		return fRequestTime;
	}

	/**
	 * Sets the Request Time
	 * @param requestTime
	 */
	void setRequestTime( Date requestTime )
	{
		fRequestTime = requestTime;
	}

	
	/**
	 *  Returns whether or not this Request is Active
	 * @return
	 */
	public boolean isActive()
	{
		return fIsActive;
	}

	/**
	 * Sets whether or not the request is active
	 * @param active
	 */
	void setIsActive( boolean active )
	{
		fIsActive = active;
	}

	
	/**
	 * Returns the Serializable UserData state object that was placed in the 
	 * {@link openadk.library.Query} class at the time of the original request.
	 * @return
	 */
	public Serializable getUserData()
	{
		return fState;
	}

	/**
	 * Used internally by the ADK to set the userData state variable
	 * @param userData
	 */
	void setUserData( Serializable userData )
	{
		fState = userData;
	}

	
	/**
	 * Used internally by the ADK to track the persistant location of the RequestInfo
	 * @return
	 */
	long getLocation()
	{
		return fOffset;
	}
	
	/**
	 * Used internally by the ADK to track the persistant location of the RequestInfo
	 * @return
	 */
	void setLocation( long value) 
	{
		fOffset = value;
	}
    
    
    

}
